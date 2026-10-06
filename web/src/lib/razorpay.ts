import { supabase } from './supabase';

/**
 * Dynamically loads the official Razorpay checkout script and ensures window.Razorpay exists.
 */
export function loadRazorpayScript(): Promise<boolean> {
  return new Promise((resolve) => {
    if (typeof (window as any).Razorpay !== 'undefined') {
      console.log('[Razorpay] checkout.js is already loaded and window.Razorpay exists');
      resolve(true);
      return;
    }

    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    script.onload = () => {
      console.log('[Razorpay] checkout.js loaded successfully');
      // Verify window.Razorpay exists
      if (typeof (window as any).Razorpay !== 'undefined') {
        resolve(true);
      } else {
        console.error('[Razorpay] Script loaded but window.Razorpay is undefined');
        resolve(false);
      }
    };
    script.onerror = (err) => {
      console.error('[Razorpay] Failed to load checkout.js script:', err);
      resolve(false);
    };
    document.body.appendChild(script);
  });
}

export interface RazorpayCheckoutParams {
  orderId: string; // Supabase Order ID
  orderNumber?: string;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  onSuccess: (verifiedOrder: any) => void;
  onFailure: (errorMessage: string) => void;
  onDismiss: () => void;
}

/**
 * Executes strict Razorpay Checkout flow:
 * 1. Verifies checkout.js script and window.Razorpay existence
 * 2. Calls Supabase Edge Function `create-razorpay-order`
 * 3. Opens window.Razorpay with key from VITE_RAZORPAY_KEY_ID
 * 4. Verifies HMAC signature via `verify-razorpay-payment` server function
 * 5. Strictly fails if any step errors; NEVER falls back to dummy success.
 */
export async function initiateRazorpayPayment({
  orderId,
  orderNumber = '',
  customerName,
  customerEmail,
  customerPhone,
  onSuccess,
  onFailure,
  onDismiss,
}: RazorpayCheckoutParams): Promise<void> {
  console.log('[Razorpay] Starting payment flow for Order ID:', orderId);

  // 1. Ensure Razorpay script is loaded and window.Razorpay exists
  const isLoaded = await loadRazorpayScript();
  if (!isLoaded || typeof (window as any).Razorpay === 'undefined') {
    const errorMsg = 'Razorpay SDK (checkout.js) could not be loaded. Please check your network connection.';
    console.error('[Razorpay Error]:', errorMsg);
    onFailure(errorMsg);
    return;
  }

  // 2. Call Edge Function: create-razorpay-order
  console.log('[Razorpay] Calling Edge Function: create-razorpay-order');
  let createData: any = null;
  try {
    const { data, error } = await supabase.functions.invoke('create-razorpay-order', {
      body: { order_id: orderId },
    });

    if (error) {
      console.error('[Razorpay Error] create-razorpay-order invocation error:', error);
      onFailure(`Order preparation failed: ${error.message || 'Server error'}`);
      return;
    }

    if (!data || data.error) {
      const err = data?.error || 'Invalid response from payment server';
      console.error('[Razorpay Error] create-razorpay-order data error:', err);
      onFailure(err);
      return;
    }

    createData = data;
    console.log('[Razorpay] Razorpay order created:', createData);
  } catch (apiErr: any) {
    console.error('[Razorpay Error] Network exception during create-razorpay-order:', apiErr);
    onFailure(apiErr.message || 'Failed to connect to payment server. Check network connection.');
    return;
  }

  const razorpayKeyId =
    createData.key_id ||
    import.meta.env.VITE_RAZORPAY_KEY_ID ||
    'rzp_test_1DP5mmOlF5G5ag';

  if (!createData.razorpay_order_id) {
    const err = 'Payment initialization failed: Missing Razorpay Order ID from server.';
    console.error('[Razorpay Error]:', err, createData);
    onFailure(err);
    return;
  }

  // 3. Open Razorpay Checkout modal
  const options = {
    key: razorpayKeyId,
    amount: createData.amount, // in paise
    currency: createData.currency || 'INR',
    name: 'BharatBazaar',
    description: `Payment for Order #${createData.order_number || orderNumber || orderId.slice(0, 8)}`,
    order_id: createData.razorpay_order_id,
    prefill: {
      name: customerName,
      email: customerEmail || 'customer@bharatbazaar.in',
      contact: customerPhone,
    },
    theme: {
      color: '#2874F0',
    },
    handler: async (response: {
      razorpay_payment_id: string;
      razorpay_order_id: string;
      razorpay_signature: string;
    }) => {
      console.log('[Razorpay] Payment captured in popup, verifying signature...', response);

      // 4. Call Edge Function: verify-razorpay-payment
      try {
        const { data: verifyData, error: verifyError } = await supabase.functions.invoke(
          'verify-razorpay-payment',
          {
            body: {
              order_id: orderId,
              razorpay_order_id: response.razorpay_order_id,
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_signature: response.razorpay_signature,
            },
          }
        );

        if (verifyError) {
          console.error('[Razorpay Error] verify-razorpay-payment call failed:', verifyError);
          onFailure(`Verification failed: ${verifyError.message || 'Signature verification error'}`);
          return;
        }

        if (!verifyData || !verifyData.verified) {
          const err = verifyData?.error || 'Payment signature verification failed. Order not confirmed.';
          console.error('[Razorpay Error] Verification mismatch:', err);
          onFailure(err);
          return;
        }

        // ONLY NOW IS THE ORDER CONFIRMED!
        console.log('[Razorpay] Signature verified successfully. Order confirmed:', verifyData.order);
        onSuccess(verifyData.order);
      } catch (verErr: any) {
        console.error('[Razorpay Error] Exception during verification:', verErr);
        onFailure(`Payment verification exception: ${verErr.message || 'Unknown error'}`);
      }
    },
    modal: {
      ondismiss: () => {
        console.log('[Razorpay] Checkout modal dismissed/closed by user');
        onDismiss();
      },
    },
  };

  try {
    const rzp = new (window as any).Razorpay(options);
    rzp.on('payment.failed', (failResponse: any) => {
      console.error('[Razorpay] Payment transaction failed:', failResponse);
      const desc =
        failResponse.error?.description ||
        failResponse.error?.reason ||
        'Payment transaction was declined or failed. Please retry.';
      onFailure(desc);
    });

    rzp.open();
  } catch (openErr: any) {
    console.error('[Razorpay Error] Failed to open Razorpay modal:', openErr);
    onFailure(`Could not open payment window: ${openErr.message || 'Check browser popup blocker'}`);
  }
}
