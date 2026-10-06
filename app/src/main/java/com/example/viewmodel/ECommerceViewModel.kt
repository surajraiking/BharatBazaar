package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.SampleData
import com.example.model.Address
import com.example.model.AppScreen
import com.example.model.Banner
import com.example.model.CartItem
import com.example.model.Category
import com.example.model.Coupon
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.Review
import com.example.model.Variant
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class SortOption(val title: String) {
  POPULARITY("Popularity"),
  PRICE_LOW_HIGH("Price: Low to High"),
  PRICE_HIGH_LOW("Price: High to Low"),
  NEWEST("Newest First")
}

data class FilterState(
  val categoryId: String? = null,
  val minPrice: Double? = null,
  val maxPrice: Double? = null,
  val minRating: Float? = null,
  val sortOption: SortOption = SortOption.POPULARITY
)

class ECommerceViewModel : ViewModel() {

  // Products
  private val _products = MutableStateFlow<List<Product>>(SampleData.initialProducts)
  val products: StateFlow<List<Product>> = _products.asStateFlow()

  val categories: List<Category> = SampleData.categories
  val banners: List<Banner> = SampleData.banners

  private val _coupons = MutableStateFlow<List<Coupon>>(SampleData.coupons)
  val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

  // Navigation Stack
  private val _screenStack = MutableStateFlow<List<AppScreen>>(listOf(AppScreen.HOME))
  val currentScreen: StateFlow<AppScreen> = MutableStateFlow(AppScreen.HOME)

  private val _screen = MutableStateFlow(AppScreen.HOME)
  val screen: StateFlow<AppScreen> = _screen.asStateFlow()

  // Selected Detail Product
  private val _selectedProduct = MutableStateFlow<Product?>(null)
  val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

  private val _selectedVariant = MutableStateFlow<Variant?>(null)
  val selectedVariant: StateFlow<Variant?> = _selectedVariant.asStateFlow()

  // Pincode & Delivery
  private val _pincode = MutableStateFlow("110001")
  val pincode: StateFlow<String> = _pincode.asStateFlow()

  private val _pincodeMessage = MutableStateFlow("Express Delivery in 2 Days | Free Delivery | COD Available")
  val pincodeMessage: StateFlow<String> = _pincodeMessage.asStateFlow()

  // Search & Filter
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _filterState = MutableStateFlow(FilterState())
  val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

  // Cart & Wishlist
  private val _cartItems = MutableStateFlow<List<CartItem>>(
    listOf(
      CartItem(
        id = "cart_init_1",
        product = SampleData.initialProducts[0],
        selectedVariant = SampleData.initialProducts[0].variants.firstOrNull(),
        quantity = 1
      )
    )
  )
  val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

  private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
  val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

  private val _wishlistIds = MutableStateFlow<Set<String>>(setOf("p2", "p5", "p9"))
  val wishlistIds: StateFlow<Set<String>> = _wishlistIds.asStateFlow()

  // Addresses
  private val _addresses = MutableStateFlow<List<Address>>(listOf(SampleData.initialAddress))
  val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

  private val _selectedAddress = MutableStateFlow<Address>(SampleData.initialAddress)
  val selectedAddress: StateFlow<Address> = _selectedAddress.asStateFlow()

  // Orders
  private val _orders = MutableStateFlow<List<Order>>(SampleData.initialOrders)
  val orders: StateFlow<List<Order>> = _orders.asStateFlow()

  // Reviews
  private val _reviews = MutableStateFlow<List<Review>>(SampleData.sampleReviews)
  val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

  fun addReview(
    productId: String,
    rating: Int,
    title: String,
    comment: String,
    reviewerName: String = "Verified Customer"
  ) {
    val cleanName = reviewerName.trim().ifBlank { "Verified Customer" }
    val newReview = Review(
      id = "rev_${UUID.randomUUID()}",
      productId = productId,
      userName = cleanName,
      rating = rating.coerceIn(1, 5),
      title = title.trim().ifBlank {
        when (rating) {
          5 -> "Terrific Purchase!"
          4 -> "Very Good Product"
          3 -> "Decent, Value for Money"
          2 -> "Below Expectation"
          else -> "Did not like it"
        }
      },
      comment = comment.trim(),
      isVerifiedPurchase = true,
      createdAt = "Just now"
    )

    _reviews.value = listOf(newReview) + _reviews.value

    // Update product rating stats in catalog
    val currentProducts = _products.value.toMutableList()
    val productIdx = currentProducts.indexOfFirst { it.id == productId }
    if (productIdx >= 0) {
      val p = currentProducts[productIdx]
      val newCount = p.ratingCount + 1
      val newAvg = ((p.rating * p.ratingCount) + rating) / newCount
      val updatedProduct = p.copy(
        rating = ((newAvg * 10).toInt() / 10f),
        ratingCount = newCount
      )
      currentProducts[productIdx] = updatedProduct
      _products.value = currentProducts

      if (_selectedProduct.value?.id == productId) {
        _selectedProduct.value = updatedProduct
      }
    }

    showToast(if (_isHinglish.value) "Dhanyawad! Aapka review add ho gaya! ⭐" else "Review submitted successfully! ⭐")
  }

  // App Settings
  private val _isAdminMode = MutableStateFlow(false)
  val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

  private val _isHinglish = MutableStateFlow(true)
  val isHinglish: StateFlow<Boolean> = _isHinglish.asStateFlow()

  // Product Comparison (Tulna)
  private val _compareProducts = MutableStateFlow<List<Product>>(emptyList())
  val compareProducts: StateFlow<List<Product>> = _compareProducts.asStateFlow()

  fun toggleCompare(product: Product) {
    val current = _compareProducts.value.toMutableList()
    val existingIndex = current.indexOfFirst { it.id == product.id }
    if (existingIndex >= 0) {
      current.removeAt(existingIndex)
      _compareProducts.value = current
      showToast(if (_isHinglish.value) "${product.title.take(20)}... comparison se hata diya" else "Removed from comparison")
    } else {
      if (current.size >= 2) {
        current[1] = product
        _compareProducts.value = current
        showToast(if (_isHinglish.value) "2 products select ho gaye! Tap to compare ⚖️" else "Comparison updated with 2 products ⚖️")
      } else {
        current.add(product)
        _compareProducts.value = current
        if (current.size == 2) {
          showToast(if (_isHinglish.value) "2 products select ho gaye! Tap to compare ⚖️" else "2 products ready to compare! Tap Compare ⚖️")
        } else {
          showToast(if (_isHinglish.value) "1 product added to compare. Select 1 more! ⚖️" else "1 product added. Select 1 more to compare! ⚖️")
        }
      }
    }
  }

  fun addToCompare(product: Product) {
    val current = _compareProducts.value.toMutableList()
    if (current.none { it.id == product.id }) {
      if (current.size >= 2) {
        current[1] = product
      } else {
        current.add(product)
      }
      _compareProducts.value = current
    }
  }

  fun removeFromCompare(productId: String) {
    _compareProducts.value = _compareProducts.value.filter { it.id != productId }
  }

  fun clearCompare() {
    _compareProducts.value = emptyList()
    showToast(if (_isHinglish.value) "Comparison clear ho gaya" else "Comparison cleared")
  }

  fun startCompareWithSimilar(product: Product) {
    val similar = _products.value.firstOrNull { it.id != product.id && it.categoryId == product.categoryId }
      ?: _products.value.firstOrNull { it.id != product.id }

    val list = if (similar != null) listOf(product, similar) else listOf(product)
    _compareProducts.value = list
    navigateTo(AppScreen.COMPARE)
  }

  fun selectSecondProductForCompare(product: Product) {
    val current = _compareProducts.value.toMutableList()
    if (current.isEmpty()) {
      current.add(product)
    } else {
      if (current.size == 1) {
        if (current[0].id != product.id) {
          current.add(product)
        }
      } else {
        current[1] = product
      }
    }
    _compareProducts.value = current
    showToast(if (_isHinglish.value) "Dusra product select ho gaya! ⚖️" else "Second product selected for comparison! ⚖️")
  }

  // Toast / Status banner
  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  fun showToast(msg: String) {
    _toastMessage.value = msg
  }

  fun clearToast() {
    _toastMessage.value = null
  }

  // Navigation
  fun navigateTo(newScreen: AppScreen) {
    val current = _screenStack.value.toMutableList()
    if (newScreen == AppScreen.HOME) {
      _screenStack.value = listOf(AppScreen.HOME)
    } else {
      current.add(newScreen)
      _screenStack.value = current
    }
    _screen.value = newScreen
  }

  fun navigateBack(): Boolean {
    val current = _screenStack.value.toMutableList()
    return if (current.size > 1) {
      current.removeAt(current.size - 1)
      _screenStack.value = current
      _screen.value = current.last()
      true
    } else {
      false
    }
  }

  fun openProductDetail(product: Product) {
    _selectedProduct.value = product
    _selectedVariant.value = product.variants.firstOrNull()
    navigateTo(AppScreen.PRODUCT_DETAIL)
  }

  fun selectVariant(variant: Variant) {
    _selectedVariant.value = variant
  }

  fun openCategory(categoryId: String?) {
    _filterState.value = _filterState.value.copy(categoryId = categoryId)
    _searchQuery.value = ""
    navigateTo(AppScreen.PRODUCT_LIST)
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setFilterCategory(categoryId: String?) {
    _filterState.value = _filterState.value.copy(categoryId = categoryId)
  }

  fun setSortOption(sortOption: SortOption) {
    _filterState.value = _filterState.value.copy(sortOption = sortOption)
  }

  fun setRatingFilter(minRating: Float?) {
    _filterState.value = _filterState.value.copy(minRating = minRating)
  }

  fun setPriceFilter(min: Double?, max: Double?) {
    _filterState.value = _filterState.value.copy(minPrice = min, maxPrice = max)
  }

  fun resetFilters() {
    _filterState.value = FilterState()
    _searchQuery.value = ""
  }

  // Cart operations
  fun addToCart(product: Product, variant: Variant? = null, qty: Int = 1) {
    val current = _cartItems.value.toMutableList()
    val existingIndex = current.indexOfFirst {
      it.product.id == product.id && it.selectedVariant?.id == variant?.id
    }
    if (existingIndex >= 0) {
      val existing = current[existingIndex]
      current[existingIndex] = existing.copy(quantity = existing.quantity + qty)
    } else {
      current.add(
        CartItem(
          id = "cart_${UUID.randomUUID()}",
          product = product,
          selectedVariant = variant,
          quantity = qty
        )
      )
    }
    _cartItems.value = current
    showToast(if (_isHinglish.value) "Item Cart me add ho gaya! 🛒" else "Added to Cart! 🛒")
  }

  fun updateCartQuantity(cartItemId: String, delta: Int) {
    val current = _cartItems.value.toMutableList()
    val index = current.indexOfFirst { it.id == cartItemId }
    if (index >= 0) {
      val item = current[index]
      val newQty = item.quantity + delta
      if (newQty <= 0) {
        current.removeAt(index)
        showToast("Item removed from Cart")
      } else {
        current[index] = item.copy(quantity = newQty)
      }
      _cartItems.value = current
    }
  }

  fun removeFromCart(cartItemId: String) {
    _cartItems.value = _cartItems.value.filter { it.id != cartItemId }
    showToast("Item removed from Cart")
  }

  fun clearCart() {
    _cartItems.value = emptyList()
    _appliedCoupon.value = null
  }

  // Wishlist operations
  fun toggleWishlist(productId: String) {
    val current = _wishlistIds.value.toMutableSet()
    if (current.contains(productId)) {
      current.remove(productId)
      showToast(if (_isHinglish.value) "Wishlist se hataya gaya" else "Removed from Wishlist")
    } else {
      current.add(productId)
      showToast(if (_isHinglish.value) "Wishlist me save ho gaya ❤️" else "Added to Wishlist ❤️")
    }
    _wishlistIds.value = current
  }

  // Coupon operations
  fun applyCoupon(code: String): Pair<Boolean, String> {
    val cleanCode = code.trim().uppercase()
    val found = _coupons.value.firstOrNull { it.code.uppercase() == cleanCode }
    if (found == null) {
      return Pair(false, "Invalid coupon code. Try WELCOME100, DIWALI20 or FLIP50")
    }

    val subtotal = _cartItems.value.sumOf { it.totalPrice }
    if (subtotal < found.minOrderAmount) {
      return Pair(false, "Minimum order of ₹${found.minOrderAmount.toInt()} required for this coupon")
    }

    _appliedCoupon.value = found
    val msg = if (_isHinglish.value) "Coupon ${found.code} lag gaya! Badhiya bachat!" else "Coupon applied successfully!"
    showToast(msg)
    return Pair(true, msg)
  }

  fun removeCoupon() {
    _appliedCoupon.value = null
    showToast("Coupon removed")
  }

  // Pincode checking
  fun checkPincode(pin: String): Boolean {
    _pincode.value = pin
    return if (pin.length == 6 && pin.all { it.isDigit() }) {
      _pincodeMessage.value = "Delivery to $pin: Within 2-3 Business Days | Free Delivery | COD Available"
      showToast("Pincode $pin serviceable! 🚚")
      true
    } else {
      _pincodeMessage.value = "Please enter a valid 6-digit Indian PIN code"
      showToast("Invalid Pincode")
      false
    }
  }

  // Create order with strict payment status verification
  fun createPendingOrder(paymentMethod: PaymentMethod, note: String = ""): Order {
    val items = _cartItems.value.map {
      OrderItem(
        productId = it.product.id,
        productTitle = it.product.title,
        productImage = it.product.imageUrls.firstOrNull() ?: "",
        variantInfo = it.selectedVariant?.variantValue ?: "",
        quantity = it.quantity,
        unitPrice = it.unitPrice,
        totalPrice = it.totalPrice
      )
    }

    val subtotal = _cartItems.value.sumOf { it.totalPrice }
    val coupon = _appliedCoupon.value
    val discount = if (coupon != null) {
      if (coupon.discountType == "fixed") {
        coupon.discountValue
      } else {
        val calc = (subtotal * coupon.discountValue) / 100.0
        if (coupon.maxDiscountAmount != null) minOf(calc, coupon.maxDiscountAmount) else calc
      }
    } else 0.0

    val deliveryFee = if (subtotal >= 499.0 || subtotal == 0.0) 0.0 else 40.0
    val total = maxOf(0.0, subtotal - discount + deliveryFee)

    val randomNum = (100000..999999).random()
    val isCod = paymentMethod == PaymentMethod.COD

    val newOrder = Order(
      id = "ord_${UUID.randomUUID()}",
      orderNumber = "OD$randomNum${(10..99).random()}",
      items = items,
      subtotal = subtotal,
      discountAmount = discount,
      deliveryFee = deliveryFee,
      totalAmount = total,
      couponCode = coupon?.code,
      deliveryAddress = _selectedAddress.value,
      paymentMethod = paymentMethod,
      paymentStatus = if (isCod) "Pending (Cash on Delivery)" else "Pending Payment",
      status = if (isCod) OrderStatus.PLACED else OrderStatus.PENDING_PAYMENT,
      trackingId = "DEL${(100000..999999).random()}IN",
      courierPartner = "Ekart Logistics",
      estimatedDeliveryDate = "Delivery in 3-4 Days",
      createdAt = "Just Now"
    )

    _orders.value = listOf(newOrder) + _orders.value

    if (isCod) {
      clearCart()
      showToast(if (_isHinglish.value) "Badhaai Ho! Cash on Delivery Order confirm ho gaya! 🎉" else "Cash on Delivery Order confirmed! 🎉")
    } else {
      safeLogI("Razorpay", "Created pending order: ${newOrder.orderNumber} for ₹${total.toInt()}")
    }

    return newOrder
  }

  // Confirm paid order only after Razorpay signature verification
  fun confirmOrderPaid(orderId: String, razorpayOrderId: String, razorpayPaymentId: String): Order? {
    val current = _orders.value.toMutableList()
    val index = current.indexOfFirst { it.id == orderId }
    if (index >= 0) {
      val confirmedOrder = current[index].copy(
        status = OrderStatus.PLACED,
        paymentStatus = "Paid via Razorpay (ID: ${razorpayPaymentId.take(12)}...)"
      )
      current[index] = confirmedOrder
      _orders.value = current
      clearCart()
      safeLogI("Razorpay", "Order $orderId verified and marked as PAID. Razorpay Payment: $razorpayPaymentId")
      showToast(if (_isHinglish.value) "Payment Verified! Badhaai Ho! 🎉" else "Payment Verified! Order Confirmed! 🎉")
      return confirmedOrder
    }
    return null
  }

  // Record payment failure and show real error message
  fun recordPaymentFailed(orderId: String, errorMessage: String) {
    val current = _orders.value.toMutableList()
    val index = current.indexOfFirst { it.id == orderId }
    if (index >= 0) {
      current[index] = current[index].copy(
        paymentStatus = "Payment Failed: $errorMessage"
      )
      _orders.value = current
    }
    safeLogE("Razorpay", "Payment failed for order $orderId: $errorMessage")
    showToast("Payment Error: $errorMessage")
  }

  private fun safeLogI(tag: String, msg: String) {
    try {
      Log.i(tag, msg)
    } catch (e: Exception) {
      println("[$tag] $msg")
    }
  }

  private fun safeLogE(tag: String, msg: String) {
    try {
      Log.e(tag, msg)
    } catch (e: Exception) {
      System.err.println("[$tag ERROR] $msg")
    }
  }

  fun placeOrder(paymentMethod: PaymentMethod, note: String = ""): Order {
    return createPendingOrder(paymentMethod, note)
  }

  fun cancelOrder(orderId: String, reason: String) {
    val current = _orders.value.toMutableList()
    val index = current.indexOfFirst { it.id == orderId }
    if (index >= 0) {
      current[index] = current[index].copy(status = OrderStatus.CANCELLED)
      _orders.value = current
      showToast("Order cancelled: $reason")
    }
  }

  // Admin Actions
  fun setAdminMode(active: Boolean) {
    _isAdminMode.value = active
    if (active) {
      navigateTo(AppScreen.ADMIN)
    } else {
      navigateTo(AppScreen.HOME)
    }
  }

  fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
    val current = _orders.value.toMutableList()
    val index = current.indexOfFirst { it.id == orderId }
    if (index >= 0) {
      current[index] = current[index].copy(status = newStatus)
      _orders.value = current
      showToast("Order status updated to: ${newStatus.label}")
    }
  }

  fun toggleProductActive(productId: String) {
    val current = _products.value.toMutableList()
    val index = current.indexOfFirst { it.id == productId }
    if (index >= 0) {
      val item = current[index]
      current[index] = item.copy(isActive = !item.isActive)
      _products.value = current
      showToast("Product active state changed")
    }
  }

  fun updateProductPriceAndStock(productId: String, newPrice: Double, newStock: Int) {
    val current = _products.value.toMutableList()
    val index = current.indexOfFirst { it.id == productId }
    if (index >= 0) {
      val item = current[index]
      current[index] = item.copy(sellingPrice = newPrice, stockQuantity = newStock)
      _products.value = current
      showToast("Price and stock updated")
    }
  }

  fun addNewProduct(
    title: String,
    categoryId: String,
    brand: String,
    desc: String,
    mrp: Double,
    price: Double,
    stock: Int,
    imageUrl: String
  ) {
    val cat = categories.firstOrNull { it.id == categoryId }
    val newP = Product(
      id = "p_${UUID.randomUUID()}",
      categoryId = categoryId,
      categoryName = cat?.name ?: "Fashion",
      title = title,
      slug = title.lowercase().replace(" ", "-"),
      brand = brand.ifBlank { "BharatBazaar Choice" },
      description = desc.ifBlank { "Premium quality item for your everyday shopping needs." },
      mrp = mrp,
      sellingPrice = price,
      stockQuantity = stock,
      imageUrls = listOf(imageUrl.ifBlank { "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80" })
    )
    _products.value = listOf(newP) + _products.value
    showToast("Product added to catalog! 🏷️")
  }

  fun addReview(productId: String, rating: Int, title: String, comment: String) {
    val newRev = Review(
      id = "rev_${UUID.randomUUID()}",
      productId = productId,
      userName = "You (Verified Buyer)",
      rating = rating,
      title = title,
      comment = comment,
      isVerifiedPurchase = true,
      createdAt = "Just now"
    )
    _reviews.value = listOf(newRev) + _reviews.value
    showToast("Dhanyawad! Review submitted successfully ⭐")
  }

  fun toggleLanguage() {
    _isHinglish.value = !_isHinglish.value
  }

  fun addAddress(address: Address) {
    val current = _addresses.value.toMutableList()
    current.add(address)
    _addresses.value = current
    _selectedAddress.value = address
    showToast("New address saved! 📍")
  }

  fun selectAddress(address: Address) {
    _selectedAddress.value = address
  }
}
