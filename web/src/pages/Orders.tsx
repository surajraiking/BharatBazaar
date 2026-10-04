import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { ArrowLeft, Package, CheckCircle2 } from 'lucide-react';

export const Orders: React.FC = () => {
  const navigate = useNavigate();
  const { orders } = useStore();

  return (
    <div className="min-h-screen bg-gray-100 pb-20">
      <div className="bg-[#2874f0] text-white p-4 sticky top-0 z-10 flex items-center shadow-md">
        <button onClick={() => navigate('/')} className="mr-3">
          <ArrowLeft size={20} />
        </button>
        <h1 className="text-lg font-bold">My Orders</h1>
      </div>

      <div className="max-w-2xl mx-auto p-4 space-y-3">
        {orders.length === 0 ? (
          <div className="bg-white rounded-xl p-8 text-center text-gray-500 shadow-sm">
            <Package size={48} className="mx-auto text-gray-300 mb-2" />
            <p className="font-semibold text-gray-700">No orders placed yet</p>
            <button
              onClick={() => navigate('/')}
              className="mt-4 bg-[#2874f0] text-white px-6 py-2 rounded-lg text-xs font-bold"
            >
              Start Shopping
            </button>
          </div>
        ) : (
          orders.map((order, idx) => (
            <div key={idx} className="bg-white rounded-lg p-4 shadow-sm text-xs space-y-2">
              <div className="flex justify-between items-center border-b pb-2">
                <div>
                  <span className="font-bold text-gray-900">Order #{order.order_number}</span>
                  <p className="text-[10px] text-gray-400">Total: ₹{order.total_amount}</p>
                </div>
                <span className="bg-green-100 text-green-700 font-bold px-2 py-0.5 rounded text-[11px] flex items-center space-x-1">
                  <CheckCircle2 size={12} />
                  <span>{order.payment_status}</span>
                </span>
              </div>
              <p className="text-gray-600">
                Delivery to: {order.delivery_address?.full_name}, {order.delivery_address?.city}
              </p>
              <div className="bg-gray-50 p-2 rounded text-[11px] text-blue-600 font-medium">
                Tracking: Ekart Express • Expected in 3 business days
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
