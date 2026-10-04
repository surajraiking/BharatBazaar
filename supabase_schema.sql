-- ============================================================================
-- BHARATBAZAAR / DUKAAN EXPRESS - SUPABASE COMPLETE DATABASE SCHEMA
-- Single-seller Indian Mobile-First E-Commerce (Flipkart / Meesho Inspired)
-- Tables: profiles, categories, products, product_images, variants,
--         cart_items, wishlist, addresses, orders, order_items, coupons, reviews, banners
-- Includes: Triggers, Row Level Security (RLS) Policies, Sample Seed Data (20 Products)
-- All UUIDs are strictly valid hexadecimal (0-9, a-f) RFC 4122 strings.
-- ============================================================================

-- 1. Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. ENUMS
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('customer', 'admin');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE order_status AS ENUM ('placed', 'packed', 'shipped', 'out_for_delivery', 'delivered', 'cancelled');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE payment_method AS ENUM ('razorpay', 'cod');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE payment_status AS ENUM ('pending', 'paid', 'failed', 'refunded');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- 3. PROFILES TABLE (Linked with auth.users)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT,
    phone TEXT,
    avatar_url TEXT,
    role user_role DEFAULT 'customer'::user_role NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 4. CATEGORIES TABLE
CREATE TABLE IF NOT EXISTS public.categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL UNIQUE,
    slug TEXT NOT NULL UNIQUE,
    icon TEXT,
    image_url TEXT,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 5. PRODUCTS TABLE
CREATE TABLE IF NOT EXISTS public.products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id UUID REFERENCES public.categories(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    slug TEXT NOT NULL UNIQUE,
    brand TEXT DEFAULT 'Bharat Choice',
    description TEXT NOT NULL,
    mrp NUMERIC(10, 2) NOT NULL CHECK (mrp > 0),
    selling_price NUMERIC(10, 2) NOT NULL CHECK (selling_price > 0 AND selling_price <= mrp),
    stock_quantity INT DEFAULT 100 NOT NULL CHECK (stock_quantity >= 0),
    is_active BOOLEAN DEFAULT true NOT NULL,
    is_featured BOOLEAN DEFAULT false,
    is_deal_of_the_day BOOLEAN DEFAULT false,
    is_trending BOOLEAN DEFAULT false,
    rating NUMERIC(2, 1) DEFAULT 4.2 CHECK (rating >= 1.0 AND rating <= 5.0),
    rating_count INT DEFAULT 128,
    badge_tag TEXT DEFAULT 'Best Seller',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 6. PRODUCT IMAGES TABLE
CREATE TABLE IF NOT EXISTS public.product_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    alt_text TEXT,
    display_order INT DEFAULT 0,
    is_primary BOOLEAN DEFAULT false
);

-- 7. PRODUCT VARIANTS TABLE (e.g. Size, Color)
CREATE TABLE IF NOT EXISTS public.variants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    variant_type TEXT NOT NULL, -- 'size', 'color', 'storage'
    variant_value TEXT NOT NULL, -- 'Free Size', 'M', 'L', 'Blue', '128GB'
    price_delta NUMERIC(10, 2) DEFAULT 0,
    stock INT DEFAULT 50 NOT NULL
);

-- 8. BANNERS TABLE
CREATE TABLE IF NOT EXISTS public.banners (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    subtitle TEXT,
    image_url TEXT NOT NULL,
    link_url TEXT,
    badge TEXT DEFAULT 'Big Saving Days',
    is_active BOOLEAN DEFAULT true,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 9. USER ADDRESSES TABLE
CREATE TABLE IF NOT EXISTS public.addresses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    full_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    address_line1 TEXT NOT NULL, -- House/Flat No, Apartment
    address_line2 TEXT,          -- Street, Sector, Area
    landmark TEXT,
    city TEXT NOT NULL,
    state TEXT NOT NULL,
    pincode VARCHAR(6) NOT NULL,
    address_type TEXT DEFAULT 'HOME' CHECK (address_type IN ('HOME', 'WORK', 'OTHER')),
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 10. CART ITEMS TABLE
CREATE TABLE IF NOT EXISTS public.cart_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    variant_id UUID REFERENCES public.variants(id) ON DELETE SET NULL,
    quantity INT DEFAULT 1 NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    UNIQUE (user_id, product_id, variant_id)
);

-- 11. WISHLIST TABLE
CREATE TABLE IF NOT EXISTS public.wishlist (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    UNIQUE (user_id, product_id)
);

-- 12. COUPONS TABLE
CREATE TABLE IF NOT EXISTS public.coupons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code TEXT NOT NULL UNIQUE,
    description TEXT NOT NULL,
    discount_type TEXT NOT NULL CHECK (discount_type IN ('fixed', 'percentage')),
    discount_value NUMERIC(10, 2) NOT NULL CHECK (discount_value > 0),
    min_order_amount NUMERIC(10, 2) DEFAULT 0,
    max_discount_amount NUMERIC(10, 2),
    is_active BOOLEAN DEFAULT true,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 13. ORDERS TABLE
CREATE TABLE IF NOT EXISTS public.orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number TEXT NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    delivery_address JSONB NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    discount_amount NUMERIC(10, 2) DEFAULT 0,
    delivery_fee NUMERIC(10, 2) DEFAULT 0,
    total_amount NUMERIC(10, 2) NOT NULL,
    coupon_code TEXT,
    payment_method payment_method NOT NULL DEFAULT 'cod'::payment_method,
    payment_status payment_status NOT NULL DEFAULT 'pending'::payment_status,
    razorpay_order_id TEXT,
    razorpay_payment_id TEXT,
    status order_status NOT NULL DEFAULT 'placed'::order_status,
    tracking_id TEXT,
    courier_partner TEXT DEFAULT 'Ekart Logistics',
    estimated_delivery_date DATE DEFAULT (CURRENT_DATE + INTERVAL '4 days'),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 14. ORDER ITEMS TABLE
CREATE TABLE IF NOT EXISTS public.order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES public.orders(id) ON DELETE CASCADE,
    product_id UUID REFERENCES public.products(id) ON DELETE SET NULL,
    product_title TEXT NOT NULL,
    product_image TEXT,
    variant_info TEXT,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10, 2) NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL
);

-- 15. REVIEWS TABLE (user_id is nullable so seed reviews can exist before user registration)
CREATE TABLE IF NOT EXISTS public.reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    user_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    user_name TEXT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title TEXT,
    comment TEXT NOT NULL,
    is_verified_purchase BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- ============================================================================
-- AUTOMATED TRIGGERS FOR TIMESTAMPS & USER SIGN-UP
-- ============================================================================

CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_profiles_modtime ON public.profiles;
CREATE TRIGGER update_profiles_modtime
    BEFORE UPDATE ON public.profiles
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

DROP TRIGGER IF EXISTS update_products_modtime ON public.products;
CREATE TRIGGER update_products_modtime
    BEFORE UPDATE ON public.products
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

DROP TRIGGER IF EXISTS update_orders_modtime ON public.orders;
CREATE TRIGGER update_orders_modtime
    BEFORE UPDATE ON public.orders
    FOR EACH ROW EXECUTE FUNCTION public.handle_updated_at();

-- Auto-create profile on Supabase auth.users INSERT
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (id, email, full_name, avatar_url, role)
    VALUES (
        NEW.id,
        NEW.email,
        COALESCE(NEW.raw_user_meta_data->>'full_name', split_part(NEW.email, '@', 1)),
        COALESCE(NEW.raw_user_meta_data->>'avatar_url', ''),
        COALESCE((NEW.raw_user_meta_data->>'role')::user_role, 'customer'::user_role)
    )
    ON CONFLICT (id) DO NOTHING;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- ============================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES (Idempotent with DROP POLICY IF EXISTS)
-- ============================================================================

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.products ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.product_images ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.variants ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.banners ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.addresses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.cart_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.wishlist ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.coupons ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.order_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reviews ENABLE ROW LEVEL SECURITY;

-- Helper function: Check if current authenticated user is Admin
CREATE OR REPLACE FUNCTION public.is_admin()
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM public.profiles
        WHERE id = auth.uid() AND role = 'admin'::user_role
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Profiles: Users can read their own profile, Admins can read all.
DROP POLICY IF EXISTS "Users can view own profile" ON public.profiles;
CREATE POLICY "Users can view own profile" ON public.profiles
    FOR SELECT USING (auth.uid() = id OR public.is_admin());

DROP POLICY IF EXISTS "Users can update own profile" ON public.profiles;
CREATE POLICY "Users can update own profile" ON public.profiles
    FOR UPDATE USING (auth.uid() = id);

-- Categories: Anyone can view active categories, Admins can CRUD
DROP POLICY IF EXISTS "Public categories read" ON public.categories;
CREATE POLICY "Public categories read" ON public.categories
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Admin categories manage" ON public.categories;
CREATE POLICY "Admin categories manage" ON public.categories
    FOR ALL USING (public.is_admin());

-- Products & Variants & Images: Public read, Admin manage
DROP POLICY IF EXISTS "Public products read" ON public.products;
CREATE POLICY "Public products read" ON public.products
    FOR SELECT USING (is_active = true OR public.is_admin());

DROP POLICY IF EXISTS "Admin products manage" ON public.products;
CREATE POLICY "Admin products manage" ON public.products
    FOR ALL USING (public.is_admin());

DROP POLICY IF EXISTS "Public images read" ON public.product_images;
CREATE POLICY "Public images read" ON public.product_images
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Admin images manage" ON public.product_images;
CREATE POLICY "Admin images manage" ON public.product_images
    FOR ALL USING (public.is_admin());

DROP POLICY IF EXISTS "Public variants read" ON public.variants;
CREATE POLICY "Public variants read" ON public.variants
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Admin variants manage" ON public.variants;
CREATE POLICY "Admin variants manage" ON public.variants
    FOR ALL USING (public.is_admin());

-- Banners: Public read active, Admin manage
DROP POLICY IF EXISTS "Public banners read" ON public.banners;
CREATE POLICY "Public banners read" ON public.banners
    FOR SELECT USING (is_active = true OR public.is_admin());

DROP POLICY IF EXISTS "Admin banners manage" ON public.banners;
CREATE POLICY "Admin banners manage" ON public.banners
    FOR ALL USING (public.is_admin());

-- Addresses: Users manage their own addresses
DROP POLICY IF EXISTS "Users manage addresses" ON public.addresses;
CREATE POLICY "Users manage addresses" ON public.addresses
    FOR ALL USING (auth.uid() = user_id);

-- Cart: Users manage their own cart
DROP POLICY IF EXISTS "Users manage cart" ON public.cart_items;
CREATE POLICY "Users manage cart" ON public.cart_items
    FOR ALL USING (auth.uid() = user_id);

-- Wishlist: Users manage their own wishlist
DROP POLICY IF EXISTS "Users manage wishlist" ON public.wishlist;
CREATE POLICY "Users manage wishlist" ON public.wishlist
    FOR ALL USING (auth.uid() = user_id);

-- Coupons: Public read active coupons, Admin manage
DROP POLICY IF EXISTS "Public coupons read" ON public.coupons;
CREATE POLICY "Public coupons read" ON public.coupons
    FOR SELECT USING (is_active = true OR public.is_admin());

DROP POLICY IF EXISTS "Admin coupons manage" ON public.coupons;
CREATE POLICY "Admin coupons manage" ON public.coupons
    FOR ALL USING (public.is_admin());

-- Orders & Order Items: User views own, Admin views and updates all
DROP POLICY IF EXISTS "Users view own orders" ON public.orders;
CREATE POLICY "Users view own orders" ON public.orders
    FOR SELECT USING (auth.uid() = user_id OR public.is_admin());

DROP POLICY IF EXISTS "Users insert orders" ON public.orders;
CREATE POLICY "Users insert orders" ON public.orders
    FOR INSERT WITH CHECK (auth.uid() = user_id);

DROP POLICY IF EXISTS "Admin update orders" ON public.orders;
CREATE POLICY "Admin update orders" ON public.orders
    FOR UPDATE USING (public.is_admin());

DROP POLICY IF EXISTS "Users view own order items" ON public.order_items;
CREATE POLICY "Users view own order items" ON public.order_items
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.orders
            WHERE orders.id = order_items.order_id
            AND (orders.user_id = auth.uid() OR public.is_admin())
        )
    );

DROP POLICY IF EXISTS "Users insert order items" ON public.order_items;
CREATE POLICY "Users insert order items" ON public.order_items
    FOR INSERT WITH CHECK (
        EXISTS (
            SELECT 1 FROM public.orders
            WHERE orders.id = order_items.order_id
            AND orders.user_id = auth.uid()
        )
    );

-- Reviews: Public read, Authenticated users create reviews, Admin manage
DROP POLICY IF EXISTS "Public reviews read" ON public.reviews;
CREATE POLICY "Public reviews read" ON public.reviews
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Users create reviews" ON public.reviews;
CREATE POLICY "Users create reviews" ON public.reviews
    FOR INSERT WITH CHECK (auth.uid() = user_id OR user_id IS NULL);

DROP POLICY IF EXISTS "Admin manage reviews" ON public.reviews;
CREATE POLICY "Admin manage reviews" ON public.reviews
    FOR ALL USING (public.is_admin());

-- ============================================================================
-- SEED DATA: 5 CATEGORIES, 20 PRODUCTS, VARIANTS, BANNERS, COUPONS
-- Valid Hexadecimal UUIDs:
-- Categories: c1000000-0000-0000-0000-000000000001 to ...05
-- Products:   a1000000-0000-0000-0000-000000000001 to ...20
-- ============================================================================

-- Categories
INSERT INTO public.categories (id, name, slug, icon, display_order) VALUES
('c1000000-0000-0000-0000-000000000001', 'Fashion & Sarees', 'fashion-sarees', 'checkroom', 1),
('c1000000-0000-0000-0000-000000000002', 'Electronics & Mobiles', 'electronics-mobiles', 'devices', 2),
('c1000000-0000-0000-0000-000000000003', 'Home & Kitchen', 'home-kitchen', 'kitchen', 3),
('c1000000-0000-0000-0000-000000000004', 'Beauty & Personal Care', 'beauty-personal-care', 'spa', 4),
('c1000000-0000-0000-0000-000000000005', 'Footwear & Bags', 'footwear-bags', 'roller_skating', 5)
ON CONFLICT (slug) DO UPDATE SET name = EXCLUDED.name, icon = EXCLUDED.icon;

-- Banners
INSERT INTO public.banners (title, subtitle, image_url, badge, display_order) VALUES
('Mega Bachat Dhamaka!', 'Min 60% - 80% Off on Ethnic Fashion & Kurtis', 'https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=1200&q=80', 'Festive Special', 1),
('Super Electronics Sale', 'Earbuds, Smartwatches & Mobile Accessories from ₹299', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=1200&q=80', 'Deals of the Day', 2),
('Ghar Ka Har Samaan Sasta', 'Cookware, Bedsheets & Storage Essentials up to 70% Off', 'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=1200&q=80', 'Home Carnival', 3)
ON CONFLICT DO NOTHING;

-- Coupons
INSERT INTO public.coupons (code, description, discount_type, discount_value, min_order_amount, max_discount_amount) VALUES
('WELCOME100', 'Special ₹100 Flat discount on your first order!', 'fixed', 100.00, 499.00, 100.00),
('DIWALI20', 'Festive Offer: 20% off on all Fashion & Home items', 'percentage', 20.00, 699.00, 300.00),
('FLIP50', 'Get ₹50 Off on orders above ₹399', 'fixed', 50.00, 399.00, 50.00),
('FREESHIP', 'Free Delivery on any order value', 'fixed', 40.00, 199.00, 40.00)
ON CONFLICT (code) DO NOTHING;

-- 20 Products Seed (All valid hexadecimal UUIDs: a1000000-...)
-- Category 1: Fashion & Sarees (4 products)
INSERT INTO public.products (id, category_id, title, slug, brand, description, mrp, selling_price, stock_quantity, is_featured, is_deal_of_the_day, is_trending, rating, rating_count, badge_tag) VALUES
('a1000000-0000-0000-0000-000000000001', 'c1000000-0000-0000-0000-000000000001', 'Banarasi Soft Silk Kanjivaram Zari Saree with Blouse Piece', 'banarasi-soft-silk-saree', 'Virasat Fashion', 'Luxurious Banarasi woven silk saree featuring intricate gold zari floral motifs across the pallu. Perfect for weddings, festivals, and puja ceremonies. Comes with 0.8m unstitched matching blouse piece.', 2999.00, 899.00, 85, true, true, true, 4.5, 3420, 'Top Deal'),
('a1000000-0000-0000-0000-000000000002', 'c1000000-0000-0000-0000-000000000001', 'Women Pure Cotton Printed Anarkali Kurta with Pant & Dupatta Set', 'pure-cotton-anarkali-kurta-set', 'Jaipuri Libas', 'Breathable 100% cambric cotton 3-piece ethnic set with traditional Bagru block print, round neck with gota patti detailing, and soft malmal dupatta.', 2499.00, 749.00, 120, true, false, true, 4.3, 1890, 'Meesho Bestseller'),
('a1000000-0000-0000-0000-000000000003', 'c1000000-0000-0000-0000-000000000001', 'Men Slim Fit Solid Pure Cotton Casual Shirt', 'men-slim-fit-cotton-shirt', 'Roadster Club', 'Comfortable full sleeve formal & casual button-down shirt crafted from pre-washed combed cotton. Features spread collar and single chest pocket.', 1299.00, 449.00, 95, false, true, false, 4.1, 980, '55% OFF'),
('a1000000-0000-0000-0000-000000000004', 'c1000000-0000-0000-0000-000000000001', 'Embroidered Semi-Stitched Georgette Lehenga Choli with Dupatta', 'georgette-lehenga-choli', 'Surat Shringar', 'Heavy thread work & sequin embroidery designer partywear lehenga choli set with santoon inner lining and net scalloped border dupatta.', 4999.00, 1499.00, 45, true, false, true, 4.6, 750, 'Festive Hit')
ON CONFLICT (slug) DO UPDATE SET title = EXCLUDED.title, selling_price = EXCLUDED.selling_price, mrp = EXCLUDED.mrp;

-- Category 2: Electronics & Mobiles (4 products)
INSERT INTO public.products (id, category_id, title, slug, brand, description, mrp, selling_price, stock_quantity, is_featured, is_deal_of_the_day, is_trending, rating, rating_count, badge_tag) VALUES
('a1000000-0000-0000-0000-000000000005', 'c1000000-0000-0000-0000-000000000002', 'True Wireless Earbuds with 60H Playtime, Quad Mic ENC & Deep Bass', 'true-wireless-earbuds-60h', 'boAt Airdopes', 'Crystal bionic sound with 13mm dynamic drivers, Beast Mode low latency for gaming, ASAP Charge (10 mins = 120 mins playtime), IPX5 sweat proof.', 2999.00, 899.00, 210, true, true, true, 4.4, 15400, 'Lowest Price'),
('a1000000-0000-0000-0000-000000000006', 'c1000000-0000-0000-0000-000000000002', '1.96-inch AMOLED Bluetooth Calling Smartwatch with AI Voice', 'amoled-calling-smartwatch', 'Noise ColorFit', 'Stunning 60Hz curved AMOLED display, functional crown button, 100+ sports modes, SpO2 & 24x7 heart rate tracker with 7-day battery life.', 4999.00, 1699.00, 140, true, false, true, 4.3, 8620, 'Hot Seller'),
('a1000000-0000-0000-0000-000000000007', 'c1000000-0000-0000-0000-000000000002', '20000mAh 22.5W Fast Charging Power Bank with Dual Output Type-C', '20000mah-fast-power-bank', 'Mi Power', 'High density lithium polymer batteries, 12 layers of circuit protection, power delivery PD 3.0 & Quick Charge 3.0 support for smartphones & tablets.', 2199.00, 1199.00, 115, false, true, false, 4.5, 4310, 'Flipkart Choice'),
('a1000000-0000-0000-0000-000000000008', 'c1000000-0000-0000-0000-000000000002', '16W Portable Bluetooth Speaker with RGB Beat Sync Lights & Heavy Bass', '16w-rgb-bluetooth-speaker', 'Zebronics Sound', 'Dual passive radiators delivering room-filling stereo audio, FM radio, USB/microSD playback, AUX in, TWS pairing mode and 10 hours playtime.', 1999.00, 699.00, 75, false, false, true, 4.2, 2150, 'Bachat Deal')
ON CONFLICT (slug) DO UPDATE SET title = EXCLUDED.title, selling_price = EXCLUDED.selling_price, mrp = EXCLUDED.mrp;

-- Category 3: Home & Kitchen (4 products)
INSERT INTO public.products (id, category_id, title, slug, brand, description, mrp, selling_price, stock_quantity, is_featured, is_deal_of_the_day, is_trending, rating, rating_count, badge_tag) VALUES
('a1000000-0000-0000-0000-000000000009', 'c1000000-0000-0000-0000-000000000003', 'Non-Stick Granite Induction & Gas Friendly 3-Piece Cookware Set', 'non-stick-granite-cookware-set', 'Prestige Omega', 'Virgin aluminum body with 5-layer German granite non-stick coating. Includes 24cm Dosa Tawa, 24cm Fry Pan and 24cm Kadhai with tempered glass lid.', 3590.00, 1299.00, 60, true, true, false, 4.4, 3890, 'Kitchen Must-Have'),
('a1000000-0000-0000-0000-000000000010', 'c1000000-0000-0000-0000-000000000003', 'Pure Cotton 210 TC Glace Cotton Double Bedsheet with 2 Pillow Covers', 'cotton-double-bedsheet-set', 'Bombay Dyeing Art', 'Skin-friendly, fade-resistant king-size double bedsheet (90x100 inch) with matching pillow cases in ethnic Rajasthani floral prints.', 1499.00, 499.00, 150, false, false, true, 4.3, 5200, 'Best Value'),
('a1000000-0000-0000-0000-000000000011', 'c1000000-0000-0000-0000-000000000003', '1000W Heavy Duty Mixer Grinder with 3 Stainless Steel Jars', '1000w-heavy-mixer-grinder', 'Bajaj Rex Pro', 'Copper wound powerful motor for smooth chutney, dry masala and wet batter grinding. Equipped with overload protection switch and ergonomically designed handles.', 3899.00, 1899.00, 40, true, false, false, 4.3, 1920, '2 Year Warranty'),
('a1000000-0000-0000-0000-000000000012', 'c1000000-0000-0000-0000-000000000003', 'Unbreakable Airtight Kitchen Storage Container Jars Set of 12 (1000ml)', 'airtight-storage-container-12pc', 'Cello Checkers', '100% food grade BPA-free clear plastic canisters with airtight silicone rim lids to keep spices, dal, and dry fruits fresh for months.', 1299.00, 449.00, 200, false, true, true, 4.5, 6700, 'Top Utility')
ON CONFLICT (slug) DO UPDATE SET title = EXCLUDED.title, selling_price = EXCLUDED.selling_price, mrp = EXCLUDED.mrp;

-- Category 4: Beauty & Personal Care (4 products)
INSERT INTO public.products (id, category_id, title, slug, brand, description, mrp, selling_price, stock_quantity, is_featured, is_deal_of_the_day, is_trending, rating, rating_count, badge_tag) VALUES
('a1000000-0000-0000-0000-000000000013', 'c1000000-0000-0000-0000-000000000004', 'Vitamin C 10% Face Serum with Ferulic Acid for Glowing Skin (30ml)', 'vitamin-c-face-serum-30ml', 'The Derma Co', 'Clinically formulated brightening facial serum that reduces dark spots, pigmentation and protects against sun damage. Fragrance free and dermatologically tested.', 649.00, 399.00, 180, true, true, true, 4.5, 8430, 'Trending Glow'),
('a1000000-0000-0000-0000-000000000014', 'c1000000-0000-0000-0000-000000000004', 'Pure Onion Hair Oil with Redensyl for Hair Fall Control (200ml)', 'pure-onion-hair-oil-200ml', 'Mamaearth Nature', 'Enriched with cold-pressed onion seed oil, bhringraj, and almond oil to nourish hair follicles, boost scalp circulation and strengthen roots.', 499.00, 289.00, 250, false, false, true, 4.2, 11200, 'Natural Herbal'),
('a1000000-0000-0000-0000-000000000015', 'c1000000-0000-0000-0000-000000000004', 'Matte Long-Lasting Waterproof Liquid Lipstick (Pack of 4 Nude Shades)', 'matte-waterproof-liquid-lipstick', 'Insight Beauty', 'Non-drying velvet matte texture, smudge-proof up to 12 hours. Vitamin E infused formula that keeps lips soft and pigmented throughout the day.', 799.00, 299.00, 160, true, false, true, 4.4, 4310, 'Pack of 4'),
('a1000000-0000-0000-0000-000000000016', 'c1000000-0000-0000-0000-000000000004', 'Ultra Light Gel Sunscreen SPF 50+ PA++++ with Hyaluronic Acid (50g)', 'ultra-light-gel-sunscreen-spf50', 'Aqualogica Dew', 'Water-light non-sticky gel sunscreen providing broad spectrum protection against UVA & UVB rays without leaving any white cast.', 599.00, 349.00, 190, false, true, false, 4.6, 9200, 'Summer Essential')
ON CONFLICT (slug) DO UPDATE SET title = EXCLUDED.title, selling_price = EXCLUDED.selling_price, mrp = EXCLUDED.mrp;

-- Category 5: Footwear & Bags (4 products)
INSERT INTO public.products (id, category_id, title, slug, brand, description, mrp, selling_price, stock_quantity, is_featured, is_deal_of_the_day, is_trending, rating, rating_count, badge_tag) VALUES
('a1000000-0000-0000-0000-000000000017', 'c1000000-0000-0000-0000-000000000005', 'Men Lightweight Breathable Mesh Sports Running & Walking Shoes', 'men-lightweight-running-shoes', 'Asian Shoes', 'Orthopedic memory foam cushion insole, anti-skid EVA phylon outsole with air cushion heel support. Ultra-lightweight and durable for all-day comfort.', 1999.00, 599.00, 130, true, true, true, 4.3, 14200, 'Super Saver'),
('a1000000-0000-0000-0000-000000000018', 'c1000000-0000-0000-0000-000000000005', 'Women Ethnic Embellished Handcrafted Mojari Juttis', 'women-ethnic-mojari-juttis', 'Kundan Heritage', 'Traditional handmade Rajasthani jutti with intricate zari embroidery and cushioned insole. Flat sole made from soft synthetic leather for painless wear.', 999.00, 399.00, 90, false, false, true, 4.4, 2100, 'Wedding Choice'),
('a1000000-0000-0000-0000-000000000019', 'c1000000-0000-0000-0000-000000000005', 'Waterproof Laptop Backpack with USB Charging Port & Anti-Theft Pocket (32L)', 'waterproof-laptop-backpack-32l', 'F Gear Armor', 'Multi-compartment organizer fitting laptops up to 15.6 inches, rain cover included in bottom pouch, padded S-shaped shoulder straps for lumbar support.', 2499.00, 799.00, 85, true, false, false, 4.5, 5400, '68% OFF'),
('a1000000-0000-0000-0000-000000000020', 'c1000000-0000-0000-0000-000000000005', 'Women Vegan Leather Structured Handbag with Detachable Sling Strap', 'women-vegan-leather-handbag', 'Lavie Elegance', 'Spacious dual compartment designer shoulder bag with premium gold-tone metal hardware, smooth zips, and inner utility pockets.', 2990.00, 899.00, 70, false, true, true, 4.3, 3100, 'Trendy Pick')
ON CONFLICT (slug) DO UPDATE SET title = EXCLUDED.title, selling_price = EXCLUDED.selling_price, mrp = EXCLUDED.mrp;

-- Product Images (Primary + Gallery for all 20 products)
INSERT INTO public.product_images (product_id, image_url, alt_text, display_order, is_primary) VALUES
('a1000000-0000-0000-0000-000000000001', 'https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80', 'Banarasi Saree Front Pallu', 1, true),
('a1000000-0000-0000-0000-000000000001', 'https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80', 'Banarasi Saree Zari Close-up', 2, false),

('a1000000-0000-0000-0000-000000000002', 'https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80', 'Cotton Anarkali Kurta Set', 1, true),
('a1000000-0000-0000-0000-000000000003', 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80', 'Men Slim Fit Shirt', 1, true),
('a1000000-0000-0000-0000-000000000004', 'https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80', 'Georgette Lehenga Choli', 1, true),

('a1000000-0000-0000-0000-000000000005', 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=800&q=80', 'True Wireless Earbuds Case', 1, true),
('a1000000-0000-0000-0000-000000000005', 'https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?auto=format&fit=crop&w=800&q=80', 'Earbuds in Ear', 2, false),

('a1000000-0000-0000-0000-000000000006', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80', 'Calling Smartwatch on Wrist', 1, true),
('a1000000-0000-0000-0000-000000000007', 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=800&q=80', '20000mAh Power Bank Matte Black', 1, true),
('a1000000-0000-0000-0000-000000000008', 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=800&q=80', 'Portable Bluetooth Speaker', 1, true),

('a1000000-0000-0000-0000-000000000009', 'https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?auto=format&fit=crop&w=800&q=80', 'Non-stick Cookware 3-Piece Set', 1, true),
('a1000000-0000-0000-0000-000000000010', 'https://images.unsplash.com/photo-1629949009765-40fc74c95018?auto=format&fit=crop&w=800&q=80', 'Double Bedsheet Floral Bed', 1, true),
('a1000000-0000-0000-0000-000000000011', 'https://images.unsplash.com/photo-1570222094114-d054a817e56b?auto=format&fit=crop&w=800&q=80', 'Heavy Duty Mixer Grinder', 1, true),
('a1000000-0000-0000-0000-000000000012', 'https://images.unsplash.com/photo-1590736969955-71cc94801759?auto=format&fit=crop&w=800&q=80', 'Airtight Jars Pantry Set', 1, true),

('a1000000-0000-0000-0000-000000000013', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=800&q=80', 'Vitamin C Serum Dropper', 1, true),
('a1000000-0000-0000-0000-000000000014', 'https://images.unsplash.com/photo-1608248597359-598d1a16630f?auto=format&fit=crop&w=800&q=80', 'Onion Hair Oil Bottle', 1, true),
('a1000000-0000-0000-0000-000000000015', 'https://images.unsplash.com/photo-1586495777744-4413f21062fa?auto=format&fit=crop&w=800&q=80', 'Matte Lipsticks Swatches', 1, true),
('a1000000-0000-0000-0000-000000000016', 'https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80', 'Sunscreen Gel Tube', 1, true),

('a1000000-0000-0000-0000-000000000017', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80', 'Men Running Shoes Pair', 1, true),
('a1000000-0000-0000-0000-000000000018', 'https://images.unsplash.com/photo-1535043934128-cf0b28d52f95?auto=format&fit=crop&w=800&q=80', 'Embellished Jutti Pair', 1, true),
('a1000000-0000-0000-0000-000000000019', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80', 'Laptop Backpack Black', 1, true),
('a1000000-0000-0000-0000-000000000020', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80', 'Women Vegan Handbag Tan', 1, true)
ON CONFLICT DO NOTHING;

-- Product Variants
INSERT INTO public.variants (product_id, variant_type, variant_value, price_delta, stock) VALUES
('a1000000-0000-0000-0000-000000000001', 'color', 'Royal Crimson Red', 0, 45),
('a1000000-0000-0000-0000-000000000001', 'color', 'Peacock Green Gold', 0, 40),
('a1000000-0000-0000-0000-000000000002', 'size', 'M (Bust 38 in)', 0, 30),
('a1000000-0000-0000-0000-000000000002', 'size', 'L (Bust 40 in)', 0, 50),
('a1000000-0000-0000-0000-000000000002', 'size', 'XL (Bust 42 in)', 0, 40),
('a1000000-0000-0000-0000-000000000003', 'size', 'M (38)', 0, 35),
('a1000000-0000-0000-0000-000000000003', 'size', 'L (40)', 0, 40),
('a1000000-0000-0000-0000-000000000003', 'size', 'XL (42)', 0, 20),
('a1000000-0000-0000-0000-000000000005', 'color', 'Carbon Black', 0, 110),
('a1000000-0000-0000-0000-000000000005', 'color', 'Teal Blue', 0, 100),
('a1000000-0000-0000-0000-000000000006', 'color', 'Space Black Silicone', 0, 80),
('a1000000-0000-0000-0000-000000000006', 'color', 'Classic Silver Metal', 100, 60),
('a1000000-0000-0000-0000-000000000017', 'size', 'UK 7', 0, 30),
('a1000000-0000-0000-0000-000000000017', 'size', 'UK 8', 0, 40),
('a1000000-0000-0000-0000-000000000017', 'size', 'UK 9', 0, 40),
('a1000000-0000-0000-0000-000000000017', 'size', 'UK 10', 0, 20)
ON CONFLICT DO NOTHING;

-- Initial Reviews (user_id is NULL for initial seed so it works without pre-created auth users)
INSERT INTO public.reviews (product_id, user_id, user_name, rating, title, comment, is_verified_purchase) VALUES
('a1000000-0000-0000-0000-000000000001', NULL, 'Priya Sharma', 5, 'Bahut hi sundar saree!', 'Quality is top notch for ₹899! Zari work bilkul wedding look deta hai. Highly recommended!', true),
('a1000000-0000-0000-0000-000000000001', NULL, 'Anjali Gupta', 4, 'Great value for money', 'Fabric soft hai aur pallu ka design bohat rich hai. Fast delivery in Delhi.', true),
('a1000000-0000-0000-0000-000000000005', NULL, 'Rahul Verma', 5, 'Dhamakedaar Bass!', 'Battery life easily 4-5 days chal jati hai. Calling quality is crisp. Best TWS under 1000.', true)
ON CONFLICT DO NOTHING;
