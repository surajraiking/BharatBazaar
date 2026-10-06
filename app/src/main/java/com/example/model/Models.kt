package com.example.model

enum class OrderStatus(val label: String, val stepIndex: Int) {
  PENDING_PAYMENT("Payment Pending", -2),
  PLACED("Order Placed", 0),
  PACKED("Packed & Ready", 1),
  SHIPPED("Shipped", 2),
  OUT_FOR_DELIVERY("Out for Delivery", 3),
  DELIVERED("Delivered", 4),
  CANCELLED("Cancelled", -1)
}

enum class PaymentMethod(val title: String) {
  ONLINE("Pay Online (UPI / Card / Netbanking)"),
  COD("Cash on Delivery")
}

data class Category(
  val id: String,
  val name: String,
  val slug: String,
  val iconName: String,
  val displayOrder: Int = 0
)

data class Variant(
  val id: String,
  val productId: String,
  val variantType: String, // "size", "color"
  val variantValue: String, // "M", "Red"
  val priceDelta: Double = 0.0,
  val stock: Int = 50
)

data class Product(
  val id: String,
  val categoryId: String,
  val categoryName: String,
  val title: String,
  val slug: String,
  val brand: String,
  val description: String,
  val mrp: Double,
  val sellingPrice: Double,
  val stockQuantity: Int,
  val isActive: Boolean = true,
  val isFeatured: Boolean = false,
  val isDealOfTheDay: Boolean = false,
  val isTrending: Boolean = false,
  val rating: Float = 4.3f,
  val ratingCount: Int = 1200,
  val badgeTag: String = "Top Seller",
  val imageUrls: List<String> = emptyList(),
  val variants: List<Variant> = emptyList()
) {
  val discountPercent: Int
    get() = if (mrp > sellingPrice) (((mrp - sellingPrice) / mrp) * 100).toInt() else 0
}

data class CartItem(
  val id: String,
  val product: Product,
  val selectedVariant: Variant? = null,
  val quantity: Int = 1
) {
  val unitPrice: Double
    get() = product.sellingPrice + (selectedVariant?.priceDelta ?: 0.0)

  val totalPrice: Double
    get() = unitPrice * quantity

  val totalMrp: Double
    get() = (product.mrp + (selectedVariant?.priceDelta ?: 0.0)) * quantity
}

data class Address(
  val id: String,
  val fullName: String,
  val phone: String,
  val addressLine1: String,
  val addressLine2: String = "",
  val landmark: String = "",
  val city: String,
  val state: String,
  val pincode: String,
  val addressType: String = "HOME", // "HOME", "WORK"
  val isDefault: Boolean = false
)

data class Coupon(
  val id: String,
  val code: String,
  val description: String,
  val discountType: String, // "fixed" or "percentage"
  val discountValue: Double,
  val minOrderAmount: Double = 0.0,
  val maxDiscountAmount: Double? = null
)

data class OrderItem(
  val productId: String,
  val productTitle: String,
  val productImage: String,
  val variantInfo: String = "",
  val quantity: Int,
  val unitPrice: Double,
  val totalPrice: Double
)

data class Order(
  val id: String,
  val orderNumber: String,
  val items: List<OrderItem>,
  val subtotal: Double,
  val discountAmount: Double,
  val deliveryFee: Double,
  val totalAmount: Double,
  val couponCode: String? = null,
  val deliveryAddress: Address,
  val paymentMethod: PaymentMethod,
  val paymentStatus: String = "Paid",
  val status: OrderStatus = OrderStatus.PLACED,
  val trackingId: String = "DEL982348IN",
  val courierPartner: String = "Ekart Logistics",
  val estimatedDeliveryDate: String = "Delivery by Thursday",
  val createdAt: String = "Today, 10:45 AM"
)

data class Review(
  val id: String,
  val productId: String,
  val userName: String,
  val rating: Int,
  val title: String,
  val comment: String,
  val isVerifiedPurchase: Boolean = true,
  val createdAt: String = "2 days ago"
)

data class Banner(
  val id: String,
  val title: String,
  val subtitle: String,
  val imageUrl: String,
  val badge: String = "Big Saving Days",
  val linkUrl: String = ""
)

enum class AppScreen {
  HOME,
  PRODUCT_LIST,
  PRODUCT_DETAIL,
  CART,
  CHECKOUT,
  ORDERS,
  WISHLIST,
  ACCOUNT,
  ADMIN,
  COMPARE
}
