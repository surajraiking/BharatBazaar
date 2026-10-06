package com.example.data

import com.example.model.Address
import com.example.model.Banner
import com.example.model.Category
import com.example.model.Coupon
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.Review
import com.example.model.Variant

object SampleData {

  val categories = listOf(
    Category("c1", "Fashion & Sarees", "fashion-sarees", "checkroom", 1),
    Category("c2", "Electronics & Mobiles", "electronics-mobiles", "devices", 2),
    Category("c3", "Home & Kitchen", "home-kitchen", "kitchen", 3),
    Category("c4", "Beauty & Care", "beauty-personal-care", "spa", 4),
    Category("c5", "Footwear & Bags", "footwear-bags", "shopping_bag", 5)
  )

  val banners = listOf(
    Banner(
      id = "b1",
      title = "Mega Bachat Dhamaka!",
      subtitle = "Min 60% - 80% Off on Ethnic Sarees & Kurtis",
      imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=1000&q=80",
      badge = "Festive Special"
    ),
    Banner(
      id = "b2",
      title = "Super Electronics Carnival",
      subtitle = "Earbuds, Smartwatches & Powerbanks from ₹299",
      imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=1000&q=80",
      badge = "Deals of the Day"
    ),
    Banner(
      id = "b3",
      title = "Ghar Ka Har Samaan Sasta",
      subtitle = "Cookware & Storage Containers up to 70% Off",
      imageUrl = "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=1000&q=80",
      badge = "Home Fiesta"
    )
  )

  val coupons = listOf(
    Coupon("cp1", "WELCOME100", "Flat ₹100 Off on your first order!", "fixed", 100.0, 499.0, 100.0),
    Coupon("cp2", "DIWALI20", "Festive 20% Off on Fashion & Home items", "percentage", 20.0, 699.0, 300.0),
    Coupon("cp3", "FLIP50", "Extra ₹50 Off on orders above ₹399", "fixed", 50.0, 399.0, 50.0),
    Coupon("cp4", "MEESHO15", "Special 15% discount for smart shoppers", "percentage", 15.0, 349.0, 150.0)
  )

  val initialAddress = Address(
    id = "addr1",
    fullName = "Rahul Sharma",
    phone = "9876543210",
    addressLine1 = "Flat 402, Shanti Kunj Apartments",
    addressLine2 = "Sector 14, Near Metro Station",
    landmark = "Opposite City Mall",
    city = "New Delhi",
    state = "Delhi",
    pincode = "110001",
    addressType = "HOME",
    isDefault = true
  )

  val initialProducts = listOf(
    // Category 1: Fashion
    Product(
      id = "p1",
      categoryId = "c1",
      categoryName = "Fashion & Sarees",
      title = "Banarasi Soft Silk Kanjivaram Zari Saree with Blouse Piece",
      slug = "banarasi-soft-silk-saree",
      brand = "Virasat Fashion",
      description = "Luxurious Banarasi woven silk saree featuring intricate gold zari floral motifs across the pallu. Perfect for weddings, festivals, and puja ceremonies. Comes with 0.8m unstitched matching blouse piece.",
      mrp = 2999.0,
      sellingPrice = 899.0,
      stockQuantity = 85,
      isFeatured = true,
      isDealOfTheDay = true,
      isTrending = true,
      rating = 4.5f,
      ratingCount = 3420,
      badgeTag = "Top Deal",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1583391733956-3750e0ff4e8b?auto=format&fit=crop&w=800&q=80"
      ),
      variants = listOf(
        Variant("v1_1", "p1", "color", "Royal Crimson Red", 0.0, 45),
        Variant("v1_2", "p1", "color", "Peacock Green Gold", 0.0, 40)
      )
    ),
    Product(
      id = "p2",
      categoryId = "c1",
      categoryName = "Fashion & Sarees",
      title = "Women Pure Cotton Printed Anarkali Kurta with Pant & Dupatta Set",
      slug = "pure-cotton-anarkali-kurta-set",
      brand = "Jaipuri Libas",
      description = "Breathable 100% cambric cotton 3-piece ethnic set with traditional Bagru block print, round neck with gota patti detailing, and soft malmal dupatta.",
      mrp = 2499.0,
      sellingPrice = 749.0,
      stockQuantity = 120,
      isFeatured = true,
      isDealOfTheDay = false,
      isTrending = true,
      rating = 4.3f,
      ratingCount = 1890,
      badgeTag = "Meesho Bestseller",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80"
      ),
      variants = listOf(
        Variant("v2_1", "p2", "size", "M (Bust 38 in)", 0.0, 30),
        Variant("v2_2", "p2", "size", "L (Bust 40 in)", 0.0, 50),
        Variant("v2_3", "p2", "size", "XL (Bust 42 in)", 0.0, 40)
      )
    ),
    Product(
      id = "p3",
      categoryId = "c1",
      categoryName = "Fashion & Sarees",
      title = "Men Slim Fit Solid Combed Cotton Casual Shirt",
      slug = "men-slim-fit-cotton-shirt",
      brand = "Roadster Club",
      description = "Comfortable full sleeve formal & casual button-down shirt crafted from pre-washed combed cotton. Features spread collar and single chest pocket.",
      mrp = 1299.0,
      sellingPrice = 449.0,
      stockQuantity = 95,
      isFeatured = false,
      isDealOfTheDay = true,
      isTrending = false,
      rating = 4.1f,
      ratingCount = 980,
      badgeTag = "65% OFF",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80"
      ),
      variants = listOf(
        Variant("v3_1", "p3", "size", "M (38)", 0.0, 35),
        Variant("v3_2", "p3", "size", "L (40)", 0.0, 40),
        Variant("v3_3", "p3", "size", "XL (42)", 0.0, 20)
      )
    ),
    Product(
      id = "p4",
      categoryId = "c1",
      categoryName = "Fashion & Sarees",
      title = "Embroidered Semi-Stitched Georgette Lehenga Choli with Dupatta",
      slug = "georgette-lehenga-choli",
      brand = "Surat Shringar",
      description = "Heavy thread work & sequin embroidery designer partywear lehenga choli set with santoon inner lining and net scalloped border dupatta.",
      mrp = 4999.0,
      sellingPrice = 1499.0,
      stockQuantity = 45,
      isFeatured = true,
      isDealOfTheDay = false,
      isTrending = true,
      rating = 4.6f,
      ratingCount = 750,
      badgeTag = "Festive Hit",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80"
      )
    ),

    // Category 2: Electronics
    Product(
      id = "p5",
      categoryId = "c2",
      categoryName = "Electronics & Mobiles",
      title = "True Wireless Earbuds with 60H Playtime, Quad Mic ENC & Deep Bass",
      slug = "true-wireless-earbuds-60h",
      brand = "boAt Airdopes",
      description = "Crystal bionic sound with 13mm dynamic drivers, Beast Mode low latency for gaming, ASAP Charge (10 mins = 120 mins playtime), IPX5 sweat proof.",
      mrp = 2999.0,
      sellingPrice = 899.0,
      stockQuantity = 210,
      isFeatured = true,
      isDealOfTheDay = true,
      isTrending = true,
      rating = 4.4f,
      ratingCount = 15400,
      badgeTag = "Lowest Price",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?auto=format&fit=crop&w=800&q=80"
      ),
      variants = listOf(
        Variant("v5_1", "p5", "color", "Carbon Black", 0.0, 110),
        Variant("v5_2", "p5", "color", "Teal Blue", 0.0, 100)
      )
    ),
    Product(
      id = "p6",
      categoryId = "c2",
      categoryName = "Electronics & Mobiles",
      title = "1.96-inch AMOLED Bluetooth Calling Smartwatch with AI Voice",
      slug = "amoled-calling-smartwatch",
      brand = "Noise ColorFit",
      description = "Stunning 60Hz curved AMOLED display, functional crown button, 100+ sports modes, SpO2 & 24x7 heart rate tracker with 7-day battery life.",
      mrp = 4999.0,
      sellingPrice = 1699.0,
      stockQuantity = 140,
      isFeatured = true,
      isDealOfTheDay = false,
      isTrending = true,
      rating = 4.3f,
      ratingCount = 8620,
      badgeTag = "Hot Seller",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80"
      ),
      variants = listOf(
        Variant("v6_1", "p6", "color", "Space Black", 0.0, 80),
        Variant("v6_2", "p6", "color", "Silver Metal", 100.0, 60)
      )
    ),
    Product(
      id = "p7",
      categoryId = "c2",
      categoryName = "Electronics & Mobiles",
      title = "20000mAh 22.5W Fast Charging Power Bank with Dual Output Type-C",
      slug = "20000mah-fast-power-bank",
      brand = "Mi Power",
      description = "High density lithium polymer batteries, 12 layers of circuit protection, power delivery PD 3.0 & Quick Charge 3.0 support for smartphones & tablets.",
      mrp = 2199.0,
      sellingPrice = 1199.0,
      stockQuantity = 115,
      isFeatured = false,
      isDealOfTheDay = true,
      isTrending = false,
      rating = 4.5f,
      ratingCount = 4310,
      badgeTag = "Flipkart Assured",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=800&q=80"
      )
    ),
    Product(
      id = "p8",
      categoryId = "c2",
      categoryName = "Electronics & Mobiles",
      title = "16W Portable Bluetooth Speaker with RGB Beat Sync Lights & Heavy Bass",
      slug = "16w-rgb-bluetooth-speaker",
      brand = "Zebronics Sound",
      description = "Dual passive radiators delivering room-filling stereo audio, FM radio, USB/microSD playback, AUX in, TWS pairing mode and 10 hours playtime.",
      mrp = 1999.0,
      sellingPrice = 699.0,
      stockQuantity = 75,
      isFeatured = false,
      isDealOfTheDay = false,
      isTrending = true,
      rating = 4.2f,
      ratingCount = 2150,
      badgeTag = "Bachat Deal",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=800&q=80"
      )
    ),

    // Category 3: Home & Kitchen
    Product(
      id = "p9",
      categoryId = "c3",
      categoryName = "Home & Kitchen",
      title = "Non-Stick Granite Induction & Gas Friendly 3-Piece Cookware Set",
      slug = "non-stick-granite-cookware-set",
      brand = "Prestige Omega",
      description = "Virgin aluminum body with 5-layer German granite non-stick coating. Includes 24cm Dosa Tawa, 24cm Fry Pan and 24cm Kadhai with tempered glass lid.",
      mrp = 3590.0,
      sellingPrice = 1299.0,
      stockQuantity = 60,
      isFeatured = true,
      isDealOfTheDay = true,
      isTrending = false,
      rating = 4.4f,
      ratingCount = 3890,
      badgeTag = "Kitchen Must-Have",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?auto=format&fit=crop&w=800&q=80"
      )
    ),
    Product(
      id = "p10",
      categoryId = "c3",
      categoryName = "Home & Kitchen",
      title = "Pure Cotton 210 TC Glace Cotton Double Bedsheet with 2 Pillow Covers",
      slug = "cotton-double-bedsheet-set",
      brand = "Bombay Dyeing Art",
      description = "Skin-friendly, fade-resistant king-size double bedsheet (90x100 inch) with matching pillow cases in ethnic Rajasthani floral prints.",
      mrp = 1499.0,
      sellingPrice = 499.0,
      stockQuantity = 150,
      isFeatured = false,
      isDealOfTheDay = false,
      isTrending = true,
      rating = 4.3f,
      ratingCount = 5200,
      badgeTag = "Best Value",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1629949009765-40fc74c95018?auto=format&fit=crop&w=800&q=80"
      )
    ),
    Product(
      id = "p11",
      categoryId = "c3",
      categoryName = "Home & Kitchen",
      title = "1000W Heavy Duty Mixer Grinder with 3 Stainless Steel Jars",
      slug = "1000w-heavy-mixer-grinder",
      brand = "Bajaj Rex Pro",
      description = "Copper wound powerful motor for smooth chutney, dry masala and wet batter grinding. Equipped with overload protection switch and ergonomically designed handles.",
      mrp = 3899.0,
      sellingPrice = 1899.0,
      stockQuantity = 40,
      isFeatured = true,
      isDealOfTheDay = false,
      isTrending = false,
      rating = 4.3f,
      ratingCount = 1920,
      badgeTag = "2 Year Warranty",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1570222094114-d054a817e56b?auto=format&fit=crop&w=800&q=80"
      )
    ),
    Product(
      id = "p12",
      categoryId = "c3",
      categoryName = "Home & Kitchen",
      title = "Unbreakable Airtight Kitchen Storage Container Jars Set of 12 (1000ml)",
      slug = "airtight-storage-container-12pc",
      brand = "Cello Checkers",
      description = "100% food grade BPA-free clear plastic canisters with airtight silicone rim lids to keep spices, dal, and dry fruits fresh for months.",
      mrp = 1299.0,
      sellingPrice = 449.0,
      stockQuantity = 200,
      isFeatured = false,
      isDealOfTheDay = true,
      isTrending = true,
      rating = 4.5f,
      ratingCount = 6700,
      badgeTag = "Top Utility",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1590736969955-71cc94801759?auto=format&fit=crop&w=800&q=80"
      )
    ),

    // Category 4: Beauty & Personal Care
    Product(
      id = "p13",
      categoryId = "c4",
      categoryName = "Beauty & Care",
      title = "Vitamin C 10% Face Serum with Ferulic Acid for Glowing Skin (30ml)",
      slug = "vitamin-c-face-serum-30ml",
      brand = "The Derma Co",
      description = "Clinically formulated brightening facial serum that reduces dark spots, pigmentation and protects against sun damage. Fragrance free and dermatologically tested.",
      mrp = 649.0,
      sellingPrice = 399.0,
      stockQuantity = 180,
      isFeatured = true,
      isDealOfTheDay = true,
      isTrending = true,
      rating = 4.5f,
      ratingCount = 8430,
      badgeTag = "Trending Glow",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=800&q=80"
      )
    ),
    Product(
      id = "p14",
      categoryId = "c4",
      categoryName = "Beauty & Care",
      title = "Pure Onion Hair Oil with Redensyl for Hair Fall Control (200ml)",
      slug = "pure-onion-hair-oil-200ml",
      brand = "Mamaearth Nature",
      description = "Enriched with cold-pressed onion seed oil, bhringraj, and almond oil to nourish hair follicles, boost scalp circulation and strengthen roots.",
      mrp = 499.0,
      sellingPrice = 289.0,
      stockQuantity = 250,
      isFeatured = false,
      isDealOfTheDay = false,
      isTrending = true,
      rating = 4.2f,
      ratingCount = 11200,
      badgeTag = "Natural Herbal",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1608248597359-598d1a16630f?auto=format&fit=crop&w=800&q=80"
      )
    ),
    Product(
      id = "p15",
      categoryId = "c4",
      categoryName = "Beauty & Care",
      title = "Matte Long-Lasting Waterproof Liquid Lipstick (Pack of 4 Nude Shades)",
      slug = "matte-waterproof-liquid-lipstick",
      brand = "Insight Beauty",
      description = "Non-drying velvet matte texture, smudge-proof up to 12 hours. Vitamin E infused formula that keeps lips soft and pigmented throughout the day.",
      mrp = 799.0,
      sellingPrice = 299.0,
      stockQuantity = 160,
      isFeatured = true,
      isDealOfTheDay = false,
      isTrending = true,
      rating = 4.4f,
      ratingCount = 4310,
      badgeTag = "Pack of 4",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1586495777744-4413f21062fa?auto=format&fit=crop&w=800&q=80"
      )
    ),
    Product(
      id = "p16",
      categoryId = "c4",
      categoryName = "Beauty & Care",
      title = "Ultra Light Gel Sunscreen SPF 50+ PA++++ with Hyaluronic Acid (50g)",
      slug = "ultra-light-gel-sunscreen-spf50",
      brand = "Aqualogica Dew",
      description = "Water-light non-sticky gel sunscreen providing broad spectrum protection against UVA & UVB rays without leaving any white cast.",
      mrp = 599.0,
      sellingPrice = 349.0,
      stockQuantity = 190,
      isFeatured = false,
      isDealOfTheDay = true,
      isTrending = false,
      rating = 4.6f,
      ratingCount = 9200,
      badgeTag = "Summer Essential",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80"
      )
    ),

    // Category 5: Footwear & Bags
    Product(
      id = "p17",
      categoryId = "c5",
      categoryName = "Footwear & Bags",
      title = "Men Lightweight Breathable Mesh Sports Running & Walking Shoes",
      slug = "men-lightweight-running-shoes",
      brand = "Asian Shoes",
      description = "Orthopedic memory foam cushion insole, anti-skid EVA phylon outsole with air cushion heel support. Ultra-lightweight and durable for all-day comfort.",
      mrp = 1999.0,
      sellingPrice = 599.0,
      stockQuantity = 130,
      isFeatured = true,
      isDealOfTheDay = true,
      isTrending = true,
      rating = 4.3f,
      ratingCount = 14200,
      badgeTag = "Super Saver",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80"
      ),
      variants = listOf(
        Variant("v17_1", "p17", "size", "UK 7", 0.0, 30),
        Variant("v17_2", "p17", "size", "UK 8", 0.0, 40),
        Variant("v17_3", "p17", "size", "UK 9", 0.0, 40),
        Variant("v17_4", "p17", "size", "UK 10", 0.0, 20)
      )
    ),
    Product(
      id = "p18",
      categoryId = "c5",
      categoryName = "Footwear & Bags",
      title = "Women Ethnic Embellished Handcrafted Mojari Juttis",
      slug = "women-ethnic-mojari-juttis",
      brand = "Kundan Heritage",
      description = "Traditional handmade Rajasthani jutti with intricate zari embroidery and cushioned insole. Flat sole made from soft synthetic leather for painless wear.",
      mrp = 999.0,
      sellingPrice = 399.0,
      stockQuantity = 90,
      isFeatured = false,
      isDealOfTheDay = false,
      isTrending = true,
      rating = 4.4f,
      ratingCount = 2100,
      badgeTag = "Wedding Choice",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1535043934128-cf0b28d52f95?auto=format&fit=crop&w=800&q=80"
      )
    ),
    Product(
      id = "p19",
      categoryId = "c5",
      categoryName = "Footwear & Bags",
      title = "Waterproof Laptop Backpack with USB Port & Anti-Theft Pocket (32L)",
      slug = "waterproof-laptop-backpack-32l",
      brand = "F Gear Armor",
      description = "Multi-compartment organizer fitting laptops up to 15.6 inches, rain cover included in bottom pouch, padded S-shaped shoulder straps for lumbar support.",
      mrp = 2499.0,
      sellingPrice = 799.0,
      stockQuantity = 85,
      isFeatured = true,
      isDealOfTheDay = false,
      isTrending = false,
      rating = 4.5f,
      ratingCount = 5400,
      badgeTag = "68% OFF",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80"
      )
    ),
    Product(
      id = "p20",
      categoryId = "c5",
      categoryName = "Footwear & Bags",
      title = "Women Vegan Leather Structured Handbag with Detachable Sling Strap",
      slug = "women-vegan-leather-handbag",
      brand = "Lavie Elegance",
      description = "Spacious dual compartment designer shoulder bag with premium gold-tone metal hardware, smooth zips, and inner utility pockets.",
      mrp = 2990.0,
      sellingPrice = 899.0,
      stockQuantity = 70,
      isFeatured = false,
      isDealOfTheDay = true,
      isTrending = true,
      rating = 4.3f,
      ratingCount = 3100,
      badgeTag = "Trendy Pick",
      imageUrls = listOf(
        "https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80"
      )
    )
  )

  val sampleReviews = listOf(
    Review("r1", "p1", "Priya Sharma", 5, "Bahut hi sundar saree!", "Quality is top notch for ₹899! Zari work bilkul wedding look deta hai. Highly recommended!", true, "2 days ago"),
    Review("r2", "p1", "Anjali Gupta", 4, "Great value for money", "Fabric soft hai aur pallu ka design bohat rich hai. Fast delivery in Delhi.", true, "5 days ago"),
    Review("r3", "p5", "Rahul Verma", 5, "Dhamakedaar Bass!", "Battery life easily 4-5 days chal jati hai. Calling quality is crisp. Best TWS under 1000.", true, "1 week ago")
  )

  val initialOrders = listOf(
    Order(
      id = "ord_101",
      orderNumber = "OD3094829104",
      items = listOf(
        OrderItem(
          productId = "p5",
          productTitle = "True Wireless Earbuds with 60H Playtime",
          productImage = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=800&q=80",
          variantInfo = "Carbon Black",
          quantity = 1,
          unitPrice = 899.0,
          totalPrice = 899.0
        )
      ),
      subtotal = 899.0,
      discountAmount = 100.0,
      deliveryFee = 0.0,
      totalAmount = 799.0,
      couponCode = "WELCOME100",
      deliveryAddress = initialAddress,
      paymentMethod = PaymentMethod.ONLINE,
      paymentStatus = "Paid (UPI)",
      status = OrderStatus.SHIPPED,
      trackingId = "DEL982348IN",
      courierPartner = "Ekart Logistics",
      estimatedDeliveryDate = "Tomorrow, by 8 PM",
      createdAt = "Yesterday, 3:30 PM"
    ),
    Order(
      id = "ord_102",
      orderNumber = "OD1948572019",
      items = listOf(
        OrderItem(
          productId = "p1",
          productTitle = "Banarasi Soft Silk Kanjivaram Zari Saree",
          productImage = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80",
          variantInfo = "Royal Crimson Red",
          quantity = 1,
          unitPrice = 899.0,
          totalPrice = 899.0
        )
      ),
      subtotal = 899.0,
      discountAmount = 0.0,
      deliveryFee = 0.0,
      totalAmount = 899.0,
      deliveryAddress = initialAddress,
      paymentMethod = PaymentMethod.COD,
      paymentStatus = "Cash on Delivery",
      status = OrderStatus.DELIVERED,
      trackingId = "EKART48194IN",
      courierPartner = "Ekart Express",
      estimatedDeliveryDate = "Delivered on 2 Oct",
      createdAt = "28 Sep, 11:20 AM"
    )
  )
}
