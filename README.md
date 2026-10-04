# BharatBazaar 🛍️ (Flipkart / Meesho Style Mobile E-Commerce)

A production-ready, mobile-first single-seller e-commerce application tailored for the Indian market. Built with Flipkart & Meesho inspired design, dual Hinglish/English language support, INR (₹) currency formatting, Deals of the Day, 2-column mobile grid, Razorpay UPI/Card/COD checkout, order tracking timeline, ratings & reviews, and full Admin Panel.

---

## 🚀 Quick Overview

- **Mobile Android App:** Built with Kotlin, Jetpack Compose, Material Design 3, Coil image loading, and Navigation Compose. Ready to run and test immediately inside the AI Studio Streaming Android Emulator!
- **Web App:** Ready-to-deploy React + Vite + Tailwind CSS single-seller storefront and Admin panel.
- **Database:** Supabase PostgreSQL with complete SQL schema, RLS policies, triggers, and 20 seeded Indian products in `supabase_schema.sql`.

---

## 📦 Database & Supabase Setup

### Step 1: Create a Supabase Project
1. Go to [https://supabase.com](https://supabase.com) and sign in.
2. Click **New Project**, select an organization, enter **BharatBazaar** as the project name, and pick the region closest to your target audience (e.g. `ap-south-1` Mumbai).
3. Save your database password securely.

### Step 2: Run the Schema & Seed Data
1. In your Supabase Dashboard, open the **SQL Editor** tab from the left sidebar.
2. Click **New query**.
3. Open `supabase_schema.sql` from this repository, copy all contents, paste it into the editor, and click **Run**.
4. This will create:
   - Tables: `profiles`, `categories`, `products`, `product_images`, `variants`, `cart_items`, `wishlist`, `addresses`, `orders`, `order_items`, `coupons`, `reviews`, `banners`
   - Automated triggers for timestamps and user sign-ups
   - Row Level Security (RLS) policies for user data isolation and admin privileges
   - 20 seeded products across 5 categories (Fashion & Sarees, Electronics & Mobiles, Home & Kitchen, Beauty & Personal Care, Footwear & Bags) with realistic Indian MRP, selling prices in ₹, discounts, and variants!

### Step 3: Get Your API Keys
1. Go to **Project Settings** > **API**.
2. Copy the **Project URL** (`https://<project-ref>.supabase.co`).
3. Copy the **Project API anon / public key**.
4. Set these in your environment variables (see `.env.example`).

---

## 💳 Razorpay Test Mode Setup

1. Sign up for a free merchant test account at [https://razorpay.com](https://razorpay.com).
2. Switch to **Test Mode** in the top navigation bar.
3. Go to **Settings** > **API Keys** > **Generate Key**.
4. Copy the **Key Id** (`rzp_test_...`) and **Key Secret**.
5. Add `VITE_RAZORPAY_KEY_ID` to your `.env` or Vercel environment variables.
6. The app supports test cards, netbanking, and test UPI IDs (e.g., `success@razorpay`). Cash on Delivery (COD) is also fully supported!

---

## 🌐 Web App Deployment on Vercel

1. Push this repository to GitHub.
2. Go to [Vercel](https://vercel.com) and click **Add New Project**.
3. Import your GitHub repository.
4. Set the Root Directory to `web` (or root if using monorepo).
5. In **Environment Variables**, add:
   - `VITE_SUPABASE_URL`
   - `VITE_SUPABASE_ANON_KEY`
   - `VITE_RAZORPAY_KEY_ID`
6. Click **Deploy**. Your e-commerce store is live with PWA and SSL!

---

## 📱 Android App Features

- **Flipkart & Meesho Look & Feel:** Header search bar, Indian festive banner carousel, Category chips, "Deals of the Day" with live countdown ticker, "Trending Now" & "Bachat Dhamaka" shelves.
- **Product Listing:** 2-column mobile grid, category filters, price sort, rating filters, search with instant suggestions.
- **Product Details:** Multi-image gallery, Indian pincode delivery check (format validation + ETA), variant selector (sizes M/L/XL, colors), MRP strike-through, discount badge, reviews & ratings.
- **Cart & Coupons:** Quantity increment/decrement, coupon discounts (`WELCOME100`, `DIWALI20`, `FLIP50`), free delivery over ₹499.
- **Checkout & Orders:** Saved addresses, UPI/Cards/COD payment, order tracking timeline (Placed ➔ Packed ➔ Shipped ➔ Out for Delivery ➔ Delivered), downloadable invoice modal, cancel order with reason.
- **Admin Panel:** Toggle into Admin mode from Account tab to view sales analytics, update order statuses, manage products, coupons, and view customer orders.
