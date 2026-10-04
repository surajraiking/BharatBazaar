import { supabase } from './supabase';

// Dynamically load the Razorpay checkout.js script
export function loadRazorpayScript(): Promise<boolean> {
  return new Promise((resolve) => {
    // If already loaded
    if ((window as any).Razorpay) {
      resolve(true);
      return;
    }

    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
}

export interface RazorpayCheckoutParams {
  orderId: string; // Internal Supabase order id
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  onSuccess: (order: any) => void;
  onFailure: (errorMessage: string) => void;
  onDismiss?: () => void;
}

/**
 * Executes complete Razorpay payment flow:
 * 1. Loads checkout.js
 * 2. Calls Supabase Edge Function `create-razorpay-order` (amount calculated securely on server)
 * 3. Opens Razorpay Modal with `window.Razorpay`
 * 4. Calls Supabase Edge Function `verify-razorpay-payment` on success (HMAC SHA-256 verification)
 * 5. Handles modal close/cancel and failures gracefully
 */
export async function initiateRazorpayPayment({
  orderId,
  customerName,
  customerEmail,
  customerPhone,
  onSuccess,
  onFailure,
  onDismiss,
}: RazorpayCheckoutParams): Promise<void> {
  try {
    // 1. Load Razorpay script dynamically
    const isLoaded = await loadRazorpayScript();
    if (!isLoaded) {
      onFailure('Failed to load Razorpay SDK. Please check your internet connection and try again.');
      return;
    }

    // 2. Call Edge Function: create-razorpay-order
    const { data: createData, error: createError } = await supabase.functions.invoke(
      'create-razorpay-order',
      {
        body: { order_id: orderId },
      }
    );

    if (createError || !createData || !createData.razorpay_order_id) {
      const msg = createError?.message || createData?.error || 'Failed to initialize payment with Razorpay';
      onFailure(msg);
      return;
    }

    const razorpayKeyId =
      createData.key_id ||
      import.meta.env.VITE_RAZORPAY_KEY_ID ||
      'rzp_test_placeholder';

    // 3. Open Razorpay Checkout modal
    const options = {
      key: razorpayKeyId,
      amount: createData.amount, // in paise
      currency: createData.currency || 'INR',
      name: 'BharatBazaar',
      description: `Payment for Order #${createData.order_number || orderId.slice(0, 8)}`,
      order_id: createData.razorpay_order_id,
      prefill: {
        name: customerName,
        email: customerEmail,
        contact: customerPhone,
      },
      theme: {
        color: '#2874F0', // Flipkart Blue
      },
      handler: async (response: {
        razorpay_payment_id: string;
        razorpay_order_id: string;
        razorpay_signature: string;
      }) => {
        try {
          // 4. Verify payment signature on the server via verify-razorpay-payment
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

          if (verifyError || !verifyData || !verifyData.verified) {
            const err = verifyError?.message || verifyData?.error || 'Payment signature verification failed';
            onFailure(err);
            return;
          }

          // Payment verified and order confirmed!
          onSuccess(verifyData.order);
        } catch (verErr: any) {
          onFailure(verErr.message || 'Error occurred while verifying payment');
        }
      },
      modal: {
        ondismiss: () => {
          if (onDismiss) {
            onDismiss();
          } else {
            onFailure('Payment was cancelled by user. You can retry anytime.');
          }
        },
      },
    };

    const rzp = new (window as any).Razorpay(options);
    rzp.on('payment.failed', (failResponse: any) => {
      onFailure(failResponse.error?.description || 'Payment transaction failed. Please retry.');
    });

    rzp.open();
  } catch (err: any) {
    onFailure(err.message || 'An unexpected error occurred during checkout.');
  }
}
