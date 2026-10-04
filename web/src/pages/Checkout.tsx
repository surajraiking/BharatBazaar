import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { initiateRazorpayPayment } from '../lib/razorpay';
import { supabase } from '../lib/supabase';
import { ShieldCheck, ArrowLeft, CheckCircle2, AlertCircle, RefreshCw } from 'lucide-react';

export const Checkout: React.FC = () => {
  const navigate = useNavigate();
  const { cart, appliedCoupon, selectedAddress, clearCart, addOrder } = useStore();

  const [paymentMethod, setPaymentMethod] = useState<'razorpay' | 'cod'>('razorpay');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [confirmedOrder, setConfirmedOrder] = useState<any | null>(null);

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
      setErrorMessage('Your cart is empty.');
      return;
    }

    setLoading(true);
    setErrorMessage(null);

    try {
      // 1. Create order record in Supabase
      const orderNumber = `OD${Math.floor(100000000 + Math.random() * 900000000)}`;
      const orderPayload = {
        order_number: orderNumber,
        delivery_address: selectedAddress,
        subtotal: subtotal,
        discount_amount: discount,
        delivery_fee: deliveryFee,
        total_amount: totalAmount,
        coupon_code: appliedCoupon?.code || null,
        payment_method: paymentMethod,
        payment_status: paymentMethod === 'cod' ? 'pending' : 'pending',
        status: paymentMethod === 'cod' ? 'placed' : 'placed',
      };

      const { data: newOrder, error: orderInsertError } = await supabase
        .from('orders')
        .insert(orderPayload)
        .select()
        .single();

      const orderId = newOrder?.id || `local_ord_${Date.now()}`;

      // Insert Order Items if database connected
      if (newOrder?.id) {
        const orderItems = cart.map((item) => ({
          order_id: newOrder.id,
          product_id: item.product.id,
          product_title: item.product.title,
          product_image: item.product.images[0] || '',
          quantity: item.quantity,
          unit_price: item.product.sellingPrice,
          total_price: item.product.sellingPrice * item.quantity,
        }));
        await supabase.from('order_items').insert(orderItems);
      }

      // 2. Handle Payment Method
      if (paymentMethod === 'cod') {
        // Cash on delivery confirmed immediately
        const confirmed = {
          ...orderPayload,
          id: orderId,
          payment_status: 'pending (Cash on Delivery)',
          items: cart,
        };
        addOrder(confirmed);
        clearCart();
        setConfirmedOrder(confirmed);
        setLoading(false);
      } else {
        // Razorpay Payment Flow
        await initiateRazorpayPayment({
          orderId: orderId,
          customerName: selectedAddress.full_name,
          customerEmail: 'customer@bharatbazaar.in',
          customerPhone: selectedAddress.phone,
          onSuccess: (verifiedOrder) => {
            const finalOrder = verifiedOrder || {
              ...orderPayload,
              id: orderId,
              payment_status: 'paid',
              items: cart,
            };
            addOrder(finalOrder);
            clearCart();
            setConfirmedOrder(finalOrder);
            setLoading(false);
          },
          onFailure: (err) => {
            setErrorMessage(err);
            setLoading(false);
          },
          onDismiss: () => {
            setErrorMessage('Payment window was closed. You can retry with Razorpay or select Cash on Delivery.');
            setLoading(false);
          },
        });
      }
    } catch (err: any) {
      setErrorMessage(err.message || 'Failed to complete checkout');
      setLoading(false);
    }
  };

  // Order Confirmed Screen
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
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 my-4 text-left text-xs text-blue-900">
            <p>
              <strong>Payment:</strong> {confirmedOrder.payment_status}
            </p>
            <p>
              <strong>Delivery To:</strong> {selectedAddress.full_name}, {selectedAddress.city} -{' '}
              {selectedAddress.pincode}
            </p>
            <p>
              <strong>Expected Delivery:</strong> In 3-4 Business Days
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
    <div className="min-h-screen bg-gray-100 pb-20">
      {/* Header */}
      <div className="bg-[#2874f0] text-white p-4 sticky top-0 z-10 flex items-center shadow-md">
        <button onClick={() => navigate(-1)} className="mr-3">
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-lg font-bold">Order Summary & Payment</h1>
      </div>

      <div className="max-w-2xl mx-auto p-4 space-y-4">
        {/* Error Banner with Retry */}
        {errorMessage && (
          <div className="bg-red-50 border border-red-200 text-red-800 rounded-lg p-4 flex items-start space-x-3">
            <AlertCircle size={20} className="text-red-500 mt-0.5 flex-shrink-0" />
            <div className="flex-1 text-sm">
              <p className="font-semibold">Payment Issue</p>
              <p className="text-xs text-red-700 mt-0.5">{errorMessage}</p>
            </div>
            <button
              onClick={handleCheckout}
              disabled={loading}
              className="bg-red-600 text-white text-xs px-3 py-1.5 rounded flex items-center space-x-1 hover:bg-red-700"
            >
              <RefreshCw size={12} className={loading ? 'animate-spin' : ''} />
              <span>Retry</span>
            </button>
          </div>
        )}

        {/* Step 1: Delivery Address */}
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

        {/* Step 2: Items Summary */}
        <div className="bg-white rounded-lg shadow-sm p-4">
          <h2 className="text-sm font-bold text-gray-800 mb-3">2. Order Items ({cart.length})</h2>
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

        {/* Step 3: Payment Options */}
        <div className="bg-white rounded-lg shadow-sm p-4">
          <h2 className="text-sm font-bold text-gray-800 mb-3">3. Choose Payment Method</h2>

          {/* Option A: Razorpay */}
          <label
            className={`border rounded-lg p-3 flex items-center justify-between cursor-pointer mb-2 transition ${
              paymentMethod === 'razorpay' ? 'border-[#2874f0] bg-blue-50/50' : 'border-gray-200'
            }`}
          >
            <div className="flex items-center space-x-3">
              <input
                type="radio"
                name="payment"
                checked={paymentMethod === 'razorpay'}
                onChange={() => setPaymentMethod('razorpay')}
                className="text-blue-600 focus:ring-blue-500"
              />
              <div>
                <div className="flex items-center space-x-2">
                  <span className="text-sm font-semibold text-gray-900">Razorpay Secure</span>
                  <span className="bg-blue-100 text-[#2874f0] text-[10px] px-1.5 py-0.5 rounded font-bold">
                    UPI / Card / Netbanking
                  </span>
                </div>
                <p className="text-xs text-gray-500 mt-0.5">
                  Instant verification via Google Pay, PhonePe, Paytm, Cards & Netbanking
                </p>
              </div>
            </div>
          </label>

          {/* Option B: Cash on Delivery */}
          <label
            className={`border rounded-lg p-3 flex items-center justify-between cursor-pointer transition ${
              paymentMethod === 'cod' ? 'border-[#2874f0] bg-blue-50/50' : 'border-gray-200'
            }`}
          >
            <div className="flex items-center space-x-3">
              <input
                type="radio"
                name="payment"
                checked={paymentMethod === 'cod'}
                onChange={() => setPaymentMethod('cod')}
                className="text-blue-600 focus:ring-blue-500"
              />
              <div>
                <span className="text-sm font-semibold text-gray-900">Cash on Delivery (COD)</span>
                <p className="text-xs text-gray-500 mt-0.5">
                  Pay cash or UPI to delivery executive at your doorstep
                </p>
              </div>
            </div>
          </label>
        </div>

        {/* Step 4: Price Details */}
        <div className="bg-white rounded-lg shadow-sm p-4 text-xs space-y-2">
          <h2 className="font-bold text-gray-800 text-sm">Price Breakdown</h2>
          <div className="flex justify-between text-gray-600">
            <span>Subtotal</span>
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
            <span>Total Payable</span>
            <span className="text-base text-[#2874f0]">₹{totalAmount}</span>
          </div>
        </div>

        <div className="flex items-center justify-center space-x-2 text-xs text-gray-500">
          <ShieldCheck size={16} className="text-green-600" />
          <span>100% Safe & Secure Payments • Verified by Razorpay</span>
        </div>
      </div>

      {/* Sticky Bottom Bar */}
      <div className="fixed bottom-0 left-0 right-0 bg-white border-t p-3 flex justify-between items-center shadow-lg">
        <div>
          <span className="text-xs text-gray-500">Amount to Pay</span>
          <p className="text-lg font-black text-gray-900">₹{totalAmount}</p>
        </div>
        <button
          onClick={handleCheckout}
          disabled={loading || cart.length === 0}
          className={`px-8 py-3 rounded-lg font-bold text-sm text-white shadow-md transition ${
            paymentMethod === 'razorpay' ? 'bg-[#ff9f00] hover:bg-amber-600' : 'bg-[#2874f0] hover:bg-blue-700'
          } ${loading ? 'opacity-70 cursor-not-allowed' : ''}`}
        >
          {loading ? (
            <div className="flex items-center space-x-2">
              <RefreshCw size={16} className="animate-spin" />
              <span>Processing...</span>
            </div>
          ) : paymentMethod === 'razorpay' ? (
            'Pay via Razorpay'
          ) : (
            'Confirm COD Order'
          )}
        </button>
      </div>
    </div>
  );
};
