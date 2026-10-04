import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../store/useStore';
import { ShoppingBag, ArrowRight, Zap, Star } from 'lucide-react';

export const Home: React.FC = () => {
  const navigate = useNavigate();
  const { addToCart, cart } = useStore();

  const sampleProducts = [
    {
      id: 'a1000000-0000-0000-0000-000000000001',
      title: 'Banarasi Soft Silk Kanjivaram Zari Saree',
      brand: 'Virasat Fashion',
      description: 'Luxurious Banarasi woven silk saree with matching blouse piece.',
      slug: 'banarasi-soft-silk-saree',
      mrp: 2999,
      sellingPrice: 899,
      stock_quantity: 85,
      is_active: true,
      is_featured: true,
      is_deal_of_the_day: true,
      is_trending: true,
      rating: 4.5,
      rating_count: 3420,
      badge_tag: 'Top Deal',
      images: ['https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80'],
    },
    {
      id: 'a1000000-0000-0000-0000-000000000005',
      title: 'True Wireless Earbuds with 60H Playtime',
      brand: 'boAt Airdopes',
      description: 'Crystal bionic sound with 13mm dynamic drivers.',
      slug: 'true-wireless-earbuds-60h',
      mrp: 2999,
      sellingPrice: 899,
      stock_quantity: 210,
      is_active: true,
      is_featured: true,
      is_deal_of_the_day: true,
      is_trending: true,
      rating: 4.4,
      rating_count: 15400,
      badge_tag: 'Lowest Price',
      images: ['https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=800&q=80'],
    },
  ];

  return (
    <div className="min-h-screen bg-gray-100 pb-20">
      {/* Top Banner */}
      <div className="bg-[#2874f0] text-white p-4 shadow-md flex justify-between items-center">
        <div>
          <h1 className="text-xl font-black tracking-tight">BharatBazaar</h1>
          <p className="text-[10px] text-yellow-300 font-semibold tracking-wide">
            EXPLORE PLUS ✦
          </p>
        </div>
        <button
          onClick={() => navigate('/checkout')}
          className="relative bg-white text-[#2874f0] px-3.5 py-1.5 rounded-md font-bold text-xs flex items-center space-x-1 shadow"
        >
          <ShoppingBag size={14} />
          <span>Cart ({cart.length})</span>
        </button>
      </div>

      <div className="max-w-4xl mx-auto p-4 space-y-4">
        {/* Festive Promo Hero */}
        <div className="bg-gradient-to-r from-blue-700 to-indigo-800 rounded-xl p-5 text-white shadow-sm flex justify-between items-center">
          <div>
            <span className="bg-yellow-400 text-black text-[10px] font-black px-2 py-0.5 rounded uppercase">
              Mega Festive Sale
            </span>
            <h2 className="text-lg font-black mt-1">Min 60% - 80% Off</h2>
            <p className="text-xs text-blue-200">Ethnic Sarees, TWS Earbuds & Home Essentials</p>
          </div>
          <button
            onClick={() => navigate('/checkout')}
            className="bg-yellow-400 text-black px-4 py-2 rounded-lg text-xs font-bold hover:bg-yellow-300 flex items-center space-x-1"
          >
            <span>Buy Now</span>
            <ArrowRight size={14} />
          </button>
        </div>

        {/* Deals of the Day Header */}
        <div className="flex justify-between items-center pt-2">
          <div className="flex items-center space-x-1.5">
            <Zap size={18} className="text-amber-500 fill-amber-500" />
            <h2 className="text-sm font-bold text-gray-800">Deals of the Day</h2>
          </div>
          <span className="text-xs text-blue-600 font-semibold cursor-pointer">View All</span>
        </div>

        {/* 2-Column Grid */}
        <div className="grid grid-cols-2 gap-3">
          {sampleProducts.map((product) => (
            <div key={product.id} className="bg-white rounded-lg shadow-sm overflow-hidden flex flex-col justify-between">
              <div>
                <img src={product.images[0]} alt={product.title} className="w-full h-40 object-cover" />
                <div className="p-3">
                  <span className="text-[10px] text-gray-500 font-bold uppercase">{product.brand}</span>
                  <h3 className="text-xs font-semibold text-gray-900 line-clamp-2 mt-0.5">{product.title}</h3>
                  <div className="flex items-center space-x-1 mt-1">
                    <span className="bg-green-700 text-white text-[10px] px-1.5 py-0.5 rounded font-bold flex items-center space-x-0.5">
                      <span>{product.rating}</span>
                      <Star size={9} className="fill-white" />
                    </span>
                    <span className="text-[10px] text-gray-400">({product.rating_count})</span>
                  </div>
                  <div className="flex items-center space-x-2 mt-2">
                    <span className="text-sm font-bold text-gray-900">₹{product.sellingPrice}</span>
                    <span className="text-xs text-gray-400 line-through">₹{product.mrp}</span>
                  </div>
                </div>
              </div>
              <div className="p-3 pt-0">
                <button
                  onClick={() => {
                    addToCart(product);
                    navigate('/checkout');
                  }}
                  className="w-full bg-[#ff9f00] text-white py-1.5 rounded text-xs font-bold hover:bg-amber-600 transition"
                >
                  Buy Now
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
