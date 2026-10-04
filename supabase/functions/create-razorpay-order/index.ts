// Supabase Edge Function: create-razorpay-order
// Language: Deno / TypeScript
// Purpose: Validates prices on the server, calculates amount in paise,
//          and creates a Razorpay Order via Razorpay REST API.

import { serve } from 'https://deno.land/std@0.177.0/http/server.ts';
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2.45.4';

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
  'Access-Control-Allow-Methods': 'POST, OPTIONS',
};

serve(async (req) => {
  // Handle CORS preflight
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders });
  }

  try {
    const { order_id, notes } = await req.json();

    if (!order_id) {
      return new Response(
        JSON.stringify({ error: 'order_id is required' }),
        { status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    const razorpayKeyId = Deno.env.get('RAZORPAY_KEY_ID');
    const razorpayKeySecret = Deno.env.get('RAZORPAY_KEY_SECRET');
    const supabaseUrl = Deno.env.get('SUPABASE_URL');
    const supabaseServiceKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY');

    if (!razorpayKeyId || !razorpayKeySecret) {
      return new Response(
        JSON.stringify({ error: 'Razorpay credentials not configured in Supabase Secrets' }),
        { status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    if (!supabaseUrl || !supabaseServiceKey) {
      return new Response(
        JSON.stringify({ error: 'Supabase credentials missing in Edge Function environment' }),
        { status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    // Initialize Supabase Admin client with service_role key
    const supabaseAdmin = createClient(supabaseUrl, supabaseServiceKey);

    // 1. Fetch Order from Database
    const { data: order, error: orderError } = await supabaseAdmin
      .from('orders')
      .select('*, order_items(*)')
      .eq('id', order_id)
      .single();

    if (orderError || !order) {
      return new Response(
        JSON.stringify({ error: 'Order not found in database', details: orderError }),
        { status: 404, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    // 2. CRITICAL SECURITY: Calculate amount on the server from database prices
    // Never trust client-provided amounts!
    let calculatedSubtotal = 0;

    if (order.order_items && order.order_items.length > 0) {
      for (const item of order.order_items) {
        // Fetch real current price from products table
        if (item.product_id) {
          const { data: product } = await supabaseAdmin
            .from('products')
            .select('selling_price')
            .eq('id', item.product_id)
            .single();

          const unitPrice = product ? Number(product.selling_price) : Number(item.unit_price);
          calculatedSubtotal += unitPrice * Number(item.quantity);
        } else {
          calculatedSubtotal += Number(item.unit_price) * Number(item.quantity);
        }
      }
    } else {
      calculatedSubtotal = Number(order.subtotal);
    }

    // Apply coupon discount if valid
    let discount = Number(order.discount_amount) || 0;
    if (order.coupon_code) {
      const { data: coupon } = await supabaseAdmin
        .from('coupons')
        .select('*')
        .eq('code', order.coupon_code.toUpperCase())
        .eq('is_active', true)
        .single();

      if (coupon && calculatedSubtotal >= Number(coupon.min_order_amount)) {
        if (coupon.discount_type === 'fixed') {
          discount = Number(coupon.discount_value);
        } else {
          const pct = (calculatedSubtotal * Number(coupon.discount_value)) / 100;
          discount = coupon.max_discount_amount ? Math.min(pct, Number(coupon.max_discount_amount)) : pct;
        }
      }
    }

    const deliveryFee = calculatedSubtotal >= 499 ? 0 : 40;
    const finalTotalInRupees = Math.max(1, Math.round(calculatedSubtotal - discount + deliveryFee));

    // Razorpay requires amount in smallest currency sub-unit (paise for INR, 1 INR = 100 paise)
    const amountInPaise = finalTotalInRupees * 100;

    // 3. Create Order via Razorpay REST API
    const credentials = btoa(`${razorpayKeyId}:${razorpayKeySecret}`);
    const razorpayResponse = await fetch('https://api.razorpay.com/v1/orders', {
      method: 'POST',
      headers: {
        'Authorization': `Basic ${credentials}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        amount: amountInPaise,
        currency: 'INR',
        receipt: (order.order_number || order_id).slice(0, 40),
        notes: {
          order_id: order_id,
          customer_name: order.delivery_address?.full_name || '',
          customer_phone: order.delivery_address?.phone || '',
          ...notes,
        },
      }),
    });

    if (!razorpayResponse.ok) {
      const errorData = await razorpayResponse.text();
      return new Response(
        JSON.stringify({ error: 'Razorpay API order creation failed', details: errorData }),
        { status: razorpayResponse.status, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    const razorpayOrder = await razorpayResponse.json();

    // 4. Update the order with razorpay_order_id in Supabase
    await supabaseAdmin
      .from('orders')
      .update({
        razorpay_order_id: razorpayOrder.id,
        total_amount: finalTotalInRupees,
        subtotal: calculatedSubtotal,
        discount_amount: discount,
        delivery_fee: deliveryFee,
        payment_status: 'pending',
        updated_at: new Date().toISOString(),
      })
      .eq('id', order_id);

    return new Response(
      JSON.stringify({
        razorpay_order_id: razorpayOrder.id,
        amount: razorpayOrder.amount, // in paise
        currency: razorpayOrder.currency,
        key_id: razorpayKeyId,
        order_number: order.order_number,
      }),
      { status: 200, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    );
  } catch (err: any) {
    return new Response(
      JSON.stringify({ error: err.message || 'Internal server error' }),
      { status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    );
  }
});
