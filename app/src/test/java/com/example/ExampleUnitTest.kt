package com.example

import com.example.model.PaymentMethod
import com.example.viewmodel.ECommerceViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

  private lateinit var viewModel: ECommerceViewModel

  @Before
  fun setup() {
    viewModel = ECommerceViewModel()
  }

  @Test
  fun testInitialCatalogLoaded() {
    val products = viewModel.products.value
    assertTrue("Initial product catalog should not be empty", products.isNotEmpty())
    assertEquals(20, products.size)
  }

  @Test
  fun testAddToCartAndQuantityUpdate() {
    val firstProduct = viewModel.products.value[0]
    val variant = firstProduct.variants.firstOrNull()

    viewModel.addToCart(firstProduct, variant, 2)
    val updatedCart = viewModel.cartItems.value

    val foundItem = updatedCart.firstOrNull { it.product.id == firstProduct.id && it.selectedVariant?.id == variant?.id }
    assertNotNull(foundItem)
    assertTrue(foundItem!!.quantity >= 2)
  }

  @Test
  fun testCouponApplication() {
    // Add product to exceed minOrderAmount
    val p = viewModel.products.value[0] // price is 899
    viewModel.addToCart(p, null, 1)

    val validResult = viewModel.applyCoupon("WELCOME100")
    assertTrue(validResult.first)
    assertEquals("WELCOME100", viewModel.appliedCoupon.value?.code)

    val invalidResult = viewModel.applyCoupon("INVALID_CODE_999")
    assertFalse(invalidResult.first)
  }

  @Test
  fun testPincodeValidation() {
    assertTrue(viewModel.checkPincode("110001"))
    assertTrue(viewModel.checkPincode("560001"))
    assertFalse(viewModel.checkPincode("123"))
    assertFalse(viewModel.checkPincode("ABCDEF"))
  }

  @Test
  fun testPlaceOrderFlow() {
    val p = viewModel.products.value[0]
    viewModel.addToCart(p, null, 1)

    // Test COD order clears cart immediately
    val initialOrderCount = viewModel.orders.value.size
    val codOrder = viewModel.createPendingOrder(PaymentMethod.COD)

    assertNotNull(codOrder)
    assertEquals(initialOrderCount + 1, viewModel.orders.value.size)
    assertTrue("Cart should be cleared after COD order placement", viewModel.cartItems.value.isEmpty())

    // Test Online order creates pending payment, and confirms only after verification
    viewModel.addToCart(p, null, 1)
    val onlineOrder = viewModel.createPendingOrder(PaymentMethod.ONLINE)
    assertEquals(com.example.model.OrderStatus.PENDING_PAYMENT, onlineOrder.status)
    assertFalse("Cart should NOT be cleared yet for pending online payment", viewModel.cartItems.value.isEmpty())

    // Server verification confirms order and clears cart
    val confirmedOrder = viewModel.confirmOrderPaid(onlineOrder.id, "rzp_order_123", "rzp_pay_123")
    assertNotNull(confirmedOrder)
    assertEquals(com.example.model.OrderStatus.PLACED, confirmedOrder!!.status)
    assertTrue("Cart should be cleared after payment verification", viewModel.cartItems.value.isEmpty())
  }

  @Test
  fun testAddReviewAndRatingSystem() {
    val initialReviewCount = viewModel.reviews.value.size
    val firstProduct = viewModel.products.value[0]
    val initialRatingCount = firstProduct.ratingCount

    viewModel.addReview(
      productId = firstProduct.id,
      rating = 5,
      title = "Exceptional Quality!",
      comment = "The fabric and zari work are truly authentic. 10/10 purchase.",
      reviewerName = "Rohan Verma"
    )

    val updatedReviews = viewModel.reviews.value
    assertEquals(initialReviewCount + 1, updatedReviews.size)

    val addedReview = updatedReviews.first()
    assertEquals(firstProduct.id, addedReview.productId)
    assertEquals(5, addedReview.rating)
    assertEquals("Exceptional Quality!", addedReview.title)
    assertEquals("Rohan Verma", addedReview.userName)
    assertTrue(addedReview.isVerifiedPurchase)

    // Verify product rating count updated
    val updatedProduct = viewModel.products.value.first { it.id == firstProduct.id }
    assertEquals(initialRatingCount + 1, updatedProduct.ratingCount)
  }

  @Test
  fun testOrderShippingStatusAndTracking() {
    val p = viewModel.products.value[0]
    viewModel.addToCart(p, null, 1)

    // Place a new order
    val order = viewModel.createPendingOrder(PaymentMethod.COD)
    assertEquals(com.example.model.OrderStatus.PLACED, order.status)
    assertEquals(0, order.status.stepIndex)

    // Advance status to PACKED
    viewModel.updateOrderStatus(order.id, com.example.model.OrderStatus.PACKED)
    val packedOrder = viewModel.orders.value.first { it.id == order.id }
    assertEquals(com.example.model.OrderStatus.PACKED, packedOrder.status)
    assertEquals(1, packedOrder.status.stepIndex)

    // Advance status to SHIPPED
    viewModel.updateOrderStatus(order.id, com.example.model.OrderStatus.SHIPPED)
    val shippedOrder = viewModel.orders.value.first { it.id == order.id }
    assertEquals(com.example.model.OrderStatus.SHIPPED, shippedOrder.status)
    assertEquals(2, shippedOrder.status.stepIndex)

    // Advance status to OUT_FOR_DELIVERY
    viewModel.updateOrderStatus(order.id, com.example.model.OrderStatus.OUT_FOR_DELIVERY)
    val outOrder = viewModel.orders.value.first { it.id == order.id }
    assertEquals(com.example.model.OrderStatus.OUT_FOR_DELIVERY, outOrder.status)
    assertEquals(3, outOrder.status.stepIndex)

    // Advance status to DELIVERED
    viewModel.updateOrderStatus(order.id, com.example.model.OrderStatus.DELIVERED)
    val deliveredOrder = viewModel.orders.value.first { it.id == order.id }
    assertEquals(com.example.model.OrderStatus.DELIVERED, deliveredOrder.status)
    assertEquals(4, deliveredOrder.status.stepIndex)
  }

  @Test
  fun testProductComparisonFeature() {
    val p1 = viewModel.products.value[0]
    val p2 = viewModel.products.value[1]

    viewModel.clearCompare()
    assertTrue(viewModel.compareProducts.value.isEmpty())

    // Add first product to compare
    viewModel.toggleCompare(p1)
    assertEquals(1, viewModel.compareProducts.value.size)
    assertEquals(p1.id, viewModel.compareProducts.value[0].id)

    // Add second product to compare
    viewModel.toggleCompare(p2)
    assertEquals(2, viewModel.compareProducts.value.size)
    assertEquals(p2.id, viewModel.compareProducts.value[1].id)

    // Start compare with similar
    viewModel.startCompareWithSimilar(p1)
    assertEquals(2, viewModel.compareProducts.value.size)
    assertEquals(com.example.model.AppScreen.COMPARE, viewModel.screen.value)

    // Remove one product
    viewModel.removeFromCompare(p2.id)
    assertEquals(1, viewModel.compareProducts.value.size)

    // Clear compare
    viewModel.clearCompare()
    assertTrue(viewModel.compareProducts.value.isEmpty())
  }
}
