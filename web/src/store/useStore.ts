import { create } from 'zustand';
import { Address, CartItem, Coupon, Order, Product } from '../types';

interface StoreState {
  cart: CartItem[];
  appliedCoupon: Coupon | null;
  selectedAddress: Address;
  orders: Order[];
  addToCart: (product: Product, quantity?: number) => void;
  updateQuantity: (itemId: string, delta: number) => void;
  removeFromCart: (itemId: string) => void;
  clearCart: () => void;
  applyCoupon: (coupon: Coupon) => void;
  removeCoupon: () => void;
  setAddress: (address: Address) => void;
  addOrder: (order: Order) => void;
}

export const useStore = create<StoreState>((set) => ({
  cart: [
    {
      id: 'cart_1',
      quantity: 1,
      product: {
        id: 'a1000000-0000-0000-0000-000000000005',
        title: 'True Wireless Earbuds with 60H Playtime',
        slug: 'true-wireless-earbuds-60h',
        brand: 'boAt Airdopes',
        description: 'Crystal bionic sound with 13mm dynamic drivers, Beast Mode low latency for gaming.',
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
    },
  ],
  appliedCoupon: null,
  selectedAddress: {
    id: 'addr_1',
    full_name: 'Rahul Sharma',
    phone: '9876543210',
    address_line1: 'Flat 402, Shanti Kunj Apartments',
    address_line2: 'Sector 14, Near Metro Station',
    landmark: 'Opposite City Mall',
    city: 'New Delhi',
    state: 'Delhi',
    pincode: '110001',
    address_type: 'HOME',
    is_default: true,
  },
  orders: [],
  addToCart: (product, quantity = 1) =>
    set((state) => {
      const existing = state.cart.find((item) => item.product.id === product.id);
      if (existing) {
        return {
          cart: state.cart.map((item) =>
            item.product.id === product.id ? { ...item, quantity: item.quantity + quantity } : item
          ),
        };
      }
      return {
        cart: [...state.cart, { id: 'c_' + Date.now(), product, quantity }],
      };
    }),
  updateQuantity: (itemId, delta) =>
    set((state) => ({
      cart: state.cart
        .map((item) => {
          if (item.id === itemId) {
            const newQty = item.quantity + delta;
            return newQty > 0 ? { ...item, quantity: newQty } : null;
          }
          return item;
        })
        .filter(Boolean) as CartItem[],
    })),
  removeFromCart: (itemId) =>
    set((state) => ({
      cart: state.cart.filter((item) => item.id !== itemId),
    })),
  clearCart: () => set({ cart: [], appliedCoupon: null }),
  applyCoupon: (coupon) => set({ appliedCoupon: coupon }),
  removeCoupon: () => set({ appliedCoupon: null }),
  setAddress: (address) => set({ selectedAddress: address }),
  addOrder: (order) => set((state) => ({ orders: [order, ...state.orders] })),
}));
