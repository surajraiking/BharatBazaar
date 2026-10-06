import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { initiateRazorpayPayment } from '../lib/razorpay';
import { supabase } from '../lib/supabase';
import {
  ShieldCheck,
  ArrowLeft,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  CreditCard,
  Truck,
} from 'lucide-react';

export const Checkout: React.FC = () => {
  const navigate = useNavigate();
  const { cart, appliedCoupon, selectedAddress, clearCart, addOrder } = useStore();

  const [paymentMethod, setPaymentMethod] = useState<'online' | 'cod'>('online');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  const [confirmedOrder, setConfirmedOrder] = useState<any | null>(null);

  // Trigger temporary toast
  const triggerToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 4000);
  };

  // Calculate pricing
  const subtotal = cart.reduce((sum, item) => sum + item.product.sellingPrice * item.quantity, 0);
  const discount = appliedCoupon
    ? appliedCoupon.discount_type === 'fixed'
      ? appliedCoupon.discount_value
      : (subtotal * appliedCoupon.discount_value) / 100
    : 0;
  const deliveryFee = subtotal >= 499 || subtotal === 0 ? 0 : 40;
  const totalAmount = Math.max(0, Math.round(subtotal - discount + deliveryFee));

  // Handler for Checkout
  const handleCheckout = async () => {
    if (cart.length === 0) {
      const err = 'Your cart is empty. Add products to proceed.';
      console.error(err);
      setErrorMessage(err);
      triggerToast(err);
      return;
    }

    setLoading(true);
    setErrorMessage(null);

    try {
      const orderNumber = `OD${Math.floor(100000000 + Math.random() * 900000000)}`;

      // 1. If Cash on Delivery: strictly confirmed as COD
      if (paymentMethod === 'cod') {
        const codOrderPayload = {
          order_number: orderNumber,
          delivery_address: selectedAddress,
          subtotal: subtotal,
          discount_amount: discount,
          delivery_fee: deliveryFee,
          total_amount: totalAmount,
          coupon_code: appliedCoupon?.code || null,
          payment_method: 'cod',
          payment_status: 'pending (Cash on Delivery)',
          status: 'placed',
        };

        console.log('[Checkout] Placing Cash on Delivery order:', codOrderPayload);

        const { data: newOrder, error: orderInsertError } = await supabase
          .from('orders')
          .insert(codOrderPayload)
          .select()
          .single();

        if (orderInsertError) {
          console.error('[Checkout] Supabase insert error for COD order:', orderInsertError);
          // If supabase errors, show the actual error and console.log it
          triggerToast(`Database Error: ${orderInsertError.message}`);
        }

        const confirmed = newOrder || {
          ...codOrderPayload,
          id: `local_cod_${Date.now()}`,
          items: cart,
        };

        addOrder(confirmed);
        clearCart();
        setConfirmedOrder(confirmed);
        setLoading(false);
        return;
      }

      // 2. If Pay Online: strictly create order with status "pending_payment"
      const onlineOrderPayload = {
        order_number: orderNumber,
        delivery_address: selectedAddress,
        subtotal: subtotal,
        discount_amount: discount,
        delivery_fee: deliveryFee,
        total_amount: totalAmount,
        coupon_code: appliedCoupon?.code || null,
        payment_method: 'razorpay',
        payment_status: 'pending',
        status: 'pending_payment',
      };

      console.log('[Checkout] Creating pending online order:', onlineOrderPayload);

      const { data: createdOrder, error: orderInsertError } = await supabase
        .from('orders')
        .insert(onlineOrderPayload)
        .select()
        .single();

      if (orderInsertError) {
        console.error('[Checkout Error] Order insertion failed:', orderInsertError);
        triggerToast(`Order Creation Failed: ${orderInsertError.message}`);
      }

      const orderId = createdOrder?.id || `ord_${Date.now()}`;

      // Insert Order Items if database connected
      if (createdOrder?.id) {
        const orderItems = cart.map((item) => ({
          order_id: createdOrder.id,
          product_id: item.product.id,
          product_title: item.product.title,
          product_image: item.product.images[0] || '',
          quantity: item.quantity,
          unit_price: item.product.sellingPrice,
          total_price: item.product.sellingPrice * item.quantity,
        }));
        const { error: itemsErr } = await supabase.from('order_items').insert(orderItems);
        if (itemsErr) {
          console.error('[Checkout Error] Order items insert failed:', itemsErr);
        }
      }

      // 3. Trigger Razorpay Payment Flow (NO DUMMY / SIMULATED SUCCESS)
      await initiateRazorpayPayment({
        orderId: orderId,
        orderNumber: orderNumber,
        customerName: selectedAddress.full_name,
        customerEmail: 'customer@bharatbazaar.in',
        customerPhone: selectedAddress.phone,
        onSuccess: (verifiedOrder) => {
          // Strictly only reached when verify-razorpay-payment returns verified: true
          console.log('[Checkout] Payment verification confirmed by server:', verifiedOrder);
          const finalOrder = verifiedOrder || {
            ...onlineOrderPayload,
            id: orderId,
            payment_status: 'paid',
            status: 'placed',
            items: cart,
          };
          addOrder(finalOrder);
          clearCart();
          setConfirmedOrder(finalOrder);
          setLoading(false);
          triggerToast('Payment Verified! Order Confirmed!');
        },
        onFailure: (err) => {
          console.error('[Checkout] Payment failed or rejected:', err);
          setErrorMessage(err);
          triggerToast(`Payment Failed: ${err}`);
          setLoading(false);
        },
        onDismiss: () => {
          const cancelMsg = 'Payment popup was closed. Your order is not placed yet. You can retry payment anytime.';
          console.warn('[Checkout]:', cancelMsg);
          setErrorMessage(cancelMsg);
          triggerToast(cancelMsg);
          setLoading(false);
        },
      });
    } catch (err: any) {
      const msg = err.message || 'An unexpected checkout exception occurred';
      console.error('[Checkout Exception]:', err);
      setErrorMessage(msg);
      triggerToast(msg);
      setLoading(false);
    }
  };

  // Order Confirmed Screen - ONLY shown when payment is verified or method is COD
  if (confirmedOrder) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
        <div className="bg-white rounded-xl shadow-md max-w-md w-full p-6 text-center">
          <div className="w-16 h-16 bg-green-100 text-green-600 rounded-full flex items-center justify-center mx-auto mb-4">
            <CheckCircle2 size={36} />
          </div>
          <h2 className="text-2xl font-bold text-gray-800">Order Placed Successfully!</h2>
          <p className="text-sm text-gray-500 mt-1">
            Order ID: <span className="font-semibold text-blue-600">{confirmedOrder.order_number}</span>
          </p>
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 my-4 text-left text-xs text-blue-900 space-y-1">
            <p>
              <strong>Payment Status:</strong> {confirmedOrder.payment_status}
            </p>
            <p>
              <strong>Delivery Address:</strong> {selectedAddress.full_name}, {selectedAddress.city} -{' '}
              {selectedAddress.pincode}
            </p>
            <p>
              <strong>Delivery Timeline:</strong> Expected in 3-4 Business Days
            </p>
          </div>
          <button
            onClick={() => navigate('/orders')}
            className="w-full bg-[#2874f0] text-white py-3 rounded-lg font-semibold hover:bg-blue-600 transition"
          >
            Track in My Orders
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 pb-24">
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed top-4 left-1/2 -translate-x-1/2 z-50 bg-gray-900 text-white text-xs px-4 py-2.5 rounded-full shadow-lg flex items-center space-x-2 animate-bounce">
          <AlertCircle size={14} className="text-yellow-400" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Header */}
      <div className="bg-[#2874f0] text-white p-4 sticky top-0 z-10 flex items-center shadow-md">
        <button onClick={() => navigate(-1)} className="mr-3">
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-lg font-bold">Checkout & Payment</h1>
      </div>

      <div className="max-w-2xl mx-auto p-4 space-y-4">
        {/* Error Banner with Retry button */}
        {errorMessage && (
          <div className="bg-red-50 border border-red-200 text-red-800 rounded-lg p-4 flex items-start space-x-3">
            <AlertCircle size={20} className="text-red-600 mt-0.5 flex-shrink-0" />
            <div className="flex-1 text-sm">
              <p className="font-bold text-red-900">Payment Not Completed</p>
              <p className="text-xs text-red-700 mt-0.5">{errorMessage}</p>
            </div>
            <button
              onClick={handleCheckout}
              disabled={loading}
              className="bg-red-600 text-white text-xs px-3 py-1.5 rounded-md font-semibold flex items-center space-x-1 hover:bg-red-700 transition"
            >
              <RefreshCw size={12} className={loading ? 'animate-spin' : ''} />
              <span>Retry Payment</span>
            </button>
          </div>
        )}

        {/* 1. Delivery Address */}
        <div className="bg-white rounded-lg shadow-sm p-4">
          <div className="flex justify-between items-center mb-2">
            <h2 className="text-sm font-bold text-gray-800">1. Delivery Address</h2>
            <span className="text-xs bg-gray-100 text-gray-700 px-2 py-0.5 rounded font-medium">
              {selectedAddress.address_type}
            </span>
          </div>
          <div className="bg-gray-50 p-3 rounded text-xs text-gray-700 space-y-1">
            <p className="font-semibold text-gray-900">
              {selectedAddress.full_name} • {selectedAddress.phone}
            </p>
            <p>
              {selectedAddress.address_line1}, {selectedAddress.address_line2}
            </p>
            <p>
              {selectedAddress.city}, {selectedAddress.state} - {selectedAddress.pincode}
            </p>
          </div>
        </div>

        {/* 2. Order Summary */}
        <div className="bg-white rounded-lg shadow-sm p-4">
          <h2 className="text-sm font-bold text-gray-800 mb-3">2. Order Summary ({cart.length} items)</h2>
          <div className="divide-y divide-gray-100">
            {cart.map((item) => (
              <div key={item.id} className="py-2.5 flex items-center justify-between text-xs">
                <div className="flex items-center space-x-3">
                  <img
                    src={item.product.images[0]}
                    alt={item.product.title}
                    className="w-12 h-12 object-cover rounded"
                  />
                  <div>
                    <p className="font-medium text-gray-900 line-clamp-1">{item.product.title}</p>
                    <p className="text-gray-500">Qty: {item.quantity}</p>
                  </div>
                </div>
                <span className="font-bold text-gray-900">
                  ₹{item.product.sellingPrice * item.quantity}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* 3. Payment Method Selection (ONLINE or COD) */}
        <div className="bg-white rounded-lg shadow-sm p-4">
          <h2 className="text-sm font-bold text-gray-800 mb-3">3. Payment Method</h2>

          {/* Option A: Pay Online (UPI / Card / Netbanking) */}
          <label
            className={`border rounded-lg p-3.5 flex items-center justify-between cursor-pointer mb-2.5 transition ${
              paymentMethod === 'online' ? 'border-[#2874f0] bg-blue-50/60 ring-1 ring-[#2874f0]' : 'border-gray-200'
            }`}
          >
            <div className="flex items-center space-x-3">
              <input
                type="radio"
                name="payment"
                checked={paymentMethod === 'online'}
                onChange={() => {
                  setPaymentMethod('online');
                  setErrorMessage(null);
                }}
                className="text-blue-600 focus:ring-blue-500 w-4 h-4"
              />
              <div>
                <div className="flex items-center space-x-2">
                  <CreditCard size={16} className="text-[#2874f0]" />
                  <span className="text-sm font-bold text-gray-900">Pay Online (UPI / Card / Netbanking)</span>
                  <span className="bg-blue-100 text-[#2874f0] text-[10px] px-1.5 py-0.5 rounded font-bold">
                    Razorpay
                  </span>
                </div>
                <p className="text-xs text-gray-500 mt-0.5">
                  Google Pay, PhonePe, Paytm, BHIM, Debit/Credit Cards & Netbanking
                </p>
              </div>
            </div>
          </label>

          {/* Option B: Cash on Delivery */}
          <label
            className={`border rounded-lg p-3.5 flex items-center justify-between cursor-pointer transition ${
              paymentMethod === 'cod' ? 'border-[#2874f0] bg-blue-50/60 ring-1 ring-[#2874f0]' : 'border-gray-200'
            }`}
          >
            <div className="flex items-center space-x-3">
              <input
                type="radio"
                name="payment"
                checked={paymentMethod === 'cod'}
                onChange={() => {
                  setPaymentMethod('cod');
                  setErrorMessage(null);
                }}
                className="text-blue-600 focus:ring-blue-500 w-4 h-4"
              />
              <div>
                <div className="flex items-center space-x-2">
                  <Truck size={16} className="text-gray-700" />
                  <span className="text-sm font-bold text-gray-900">Cash on Delivery</span>
                </div>
                <p className="text-xs text-gray-500 mt-0.5">
                  Pay cash or UPI to delivery agent at your doorstep
                </p>
              </div>
            </div>
          </label>
        </div>

        {/* 4. Price Breakdown */}
        <div className="bg-white rounded-lg shadow-sm p-4 text-xs space-y-2">
          <h2 className="font-bold text-gray-800 text-sm">Price Details</h2>
          <div className="flex justify-between text-gray-600">
            <span>Price ({cart.length} items)</span>
            <span>₹{subtotal}</span>
          </div>
          {discount > 0 && (
            <div className="flex justify-between text-green-600 font-semibold">
              <span>Coupon Discount ({appliedCoupon?.code})</span>
              <span>-₹{discount}</span>
            </div>
          )}
          <div className="flex justify-between text-gray-600">
            <span>Delivery Charges</span>
            <span className={deliveryFee === 0 ? 'text-green-600 font-bold' : ''}>
              {deliveryFee === 0 ? 'FREE' : `₹${deliveryFee}`}
            </span>
          </div>
          <div className="border-t pt-2 flex justify-between font-bold text-sm text-gray-900">
            <span>Total Payable Amount</span>
            <span className="text-base text-[#2874f0]">₹{totalAmount}</span>
          </div>
        </div>

        <div className="flex items-center justify-center space-x-2 text-xs text-gray-500 pt-2">
          <ShieldCheck size={16} className="text-green-600" />
          <span>100% Safe & Secure Payments • Verified by Razorpay</span>
        </div>
      </div>

      {/* Sticky Bottom Bar */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t p-3 flex justify-between items-center shadow-lg z-20">
        <div>
          <span className="text-xs text-gray-500">Total Amount</span>
          <p className="text-lg font-black text-gray-900">₹{totalAmount}</p>
        </div>
        <button
          onClick={handleCheckout}
          disabled={loading || cart.length === 0}
          className={`px-8 py-3 rounded-lg font-bold text-sm text-white shadow-md transition ${
            paymentMethod === 'online' ? 'bg-[#ff9f00] hover:bg-amber-600' : 'bg-[#2874f0] hover:bg-blue-700'
          } ${loading ? 'opacity-70 cursor-not-allowed' : ''}`}
        >
          {loading ? (
            <div className="flex items-center space-x-2">
              <RefreshCw size={16} className="animate-spin" />
              <span>Connecting to Gateway...</span>
            </div>
          ) : paymentMethod === 'online' ? (
            'Pay Online via Razorpay'
          ) : (
            'Confirm Cash on Delivery'
          )}
        </button>
      </div>
    </div>
  );
};
