package com.example

import com.example.model.PaymentMethod
import com.example.viewmodel.ECommerceViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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

    val initialOrderCount = viewModel.orders.value.size
    val placedOrder = viewModel.placeOrder(PaymentMethod.RAZORPAY)

    assertNotNull(placedOrder)
    assertEquals(initialOrderCount + 1, viewModel.orders.value.size)
    assertTrue("Cart should be cleared after order placement", viewModel.cartItems.value.isEmpty())
  }
}
