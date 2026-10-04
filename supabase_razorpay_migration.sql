-- ============================================================================
-- RAZORPAY PAYMENT INTEGRATION MIGRATION
-- Run this script in the Supabase SQL Editor: Dashboard > SQL Editor > New query
-- ============================================================================

-- 1. Create payment_status enum if not existing
DO $$ BEGIN
    CREATE TYPE payment_status AS ENUM ('pending', 'paid', 'failed', 'refunded');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- 2. Add payment_status, razorpay_order_id, and razorpay_payment_id columns to public.orders
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' 
        AND table_name = 'orders' 
        AND column_name = 'payment_status'
    ) THEN
        ALTER TABLE public.orders ADD COLUMN payment_status payment_status NOT NULL DEFAULT 'pending'::payment_status;
    END IF;
END $$;

ALTER TABLE public.orders 
ADD COLUMN IF NOT EXISTS razorpay_order_id TEXT;

ALTER TABLE public.orders 
ADD COLUMN IF NOT EXISTS razorpay_payment_id TEXT;

-- 3. Create index for fast lookups on Razorpay Order ID
CREATE INDEX IF NOT EXISTS idx_orders_razorpay_order_id ON public.orders(razorpay_order_id);
CREATE INDEX IF NOT EXISTS idx_orders_payment_status ON public.orders(payment_status);
