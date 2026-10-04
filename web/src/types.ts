export interface Category {
  id: string;
  name: string;
  slug: string;
  icon: string;
  display_order: number;
}

export interface Variant {
  id: string;
  product_id: string;
  variant_type: 'size' | 'color' | 'storage';
  variant_value: string;
  price_delta: number;
  stock: number;
}

export interface ProductImage {
  id: string;
  product_id: string;
  image_url: string;
  alt_text?: string;
  is_primary: boolean;
}

export interface Product {
  id: string;
  category_id?: string;
  category_name?: string;
  title: string;
  slug: string;
  brand: string;
  description: string;
  mrp: number;
  selling_price: number;
  stock_quantity: number;
  is_active: boolean;
  is_featured: boolean;
  is_deal_of_the_day: boolean;
  is_trending: boolean;
  rating: number;
  rating_count: number;
  badge_tag?: string;
  images: string[];
  variants?: Variant[];
}

export interface CartItem {
  id: string;
  product: Product;
  selected_variant?: Variant;
  quantity: number;
}

export interface Coupon {
  id: string;
  code: string;
  description: string;
  discount_type: 'fixed' | 'percentage';
  discount_value: number;
  min_order_amount: number;
  max_discount_amount?: number;
}

export interface Address {
  id: string;
  full_name: string;
  phone: string;
  address_line1: string;
  address_line2?: string;
  landmark?: string;
  city: string;
  state: string;
  pincode: string;
  address_type: 'HOME' | 'WORK' | 'OTHER';
  is_default?: boolean;
}

export type OrderStatus = 'placed' | 'packed' | 'shipped' | 'out_for_delivery' | 'delivered' | 'cancelled';

export interface OrderItem {
  product_id: string;
  product_title: string;
  product_image: string;
  variant_info?: string;
  quantity: number;
  unit_price: number;
  total_price: number;
}

export interface Order {
  id: string;
  order_number: string;
  items: OrderItem[];
  subtotal: number;
  discount_amount: number;
  delivery_fee: number;
  total_amount: number;
  coupon_code?: string;
  delivery_address: Address;
  payment_method: 'razorpay' | 'cod';
  payment_status: 'pending' | 'paid' | 'failed' | 'refunded';
  status: OrderStatus;
  tracking_id?: string;
  courier_partner?: string;
  estimated_delivery_date: string;
  created_at: string;
}

export interface Banner {
  id: string;
  title: string;
  subtitle?: string;
  image_url: string;
  badge?: string;
}

export interface Review {
  id: string;
  product_id: string;
  user_name: string;
  rating: number;
  title?: string;
  comment: string;
  created_at: string;
}
