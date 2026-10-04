package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.AppScreen
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.LightDiscountGreen
import com.example.viewmodel.ECommerceViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartScreen(
  viewModel: ECommerceViewModel,
  onChangeAddressClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cartItems by viewModel.cartItems.collectAsState()
  val appliedCoupon by viewModel.appliedCoupon.collectAsState()
  val selectedAddress by viewModel.selectedAddress.collectAsState()
  val isHinglish by viewModel.isHinglish.collectAsState()

  var couponInput by remember { mutableStateOf("") }
  var couponError by remember { mutableStateOf<String?>(null) }

  val subtotal = cartItems.sumOf { it.totalPrice }
  val totalMrp = cartItems.sumOf { it.totalMrp }
  val mrpDiscount = totalMrp - subtotal

  val couponDiscount = if (appliedCoupon != null) {
    if (appliedCoupon!!.discountType == "fixed") {
      appliedCoupon!!.discountValue
    } else {
      val calc = (subtotal * appliedCoupon!!.discountValue) / 100.0
      if (appliedCoupon!!.maxDiscountAmount != null) minOf(calc, appliedCoupon!!.maxDiscountAmount!!) else calc
    }
  } else 0.0

  val deliveryFee = if (subtotal >= 499.0 || subtotal == 0.0) 0.0 else 40.0
  val finalTotal = maxOf(0.0, subtotal - couponDiscount + deliveryFee)
  val totalSavings = mrpDiscount + couponDiscount

  if (cartItems.isEmpty()) {
    // Empty Cart UI
    Column(
      modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFF1F2F4))
        .padding(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(100.dp)
          .background(Color.White, RoundedCornerShape(50.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.ShoppingCart,
          contentDescription = null,
          tint = FlipkartBlue,
          modifier = Modifier.size(52.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = if (isHinglish) "Aapka Cart Khaali Hai! 🛍️" else "Your Cart is Empty! 🛍️",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF212121)
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (isHinglish) "Abhi shandaar deals aur saste rates par shopping karein." else "Explore top deals and add items to your cart.",
        fontSize = 13.sp,
        color = Color.Gray,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      Button(
        onClick = { viewModel.navigateTo(AppScreen.HOME) },
        colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("shop_now_button")
      ) {
        Text("Shop Now / Samaan Dekhein", fontWeight = FontWeight.Bold)
      }
    }
    return
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .testTag("cart_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(bottom = 76.dp)
    ) {
      // 1. Delivery Address Card
      Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Deliver to: ${selectedAddress.fullName}, ${selectedAddress.pincode}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${selectedAddress.addressLine1}, ${selectedAddress.city}",
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1
              )
            }
          }

          Text(
            text = "Change",
            color = FlipkartBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .clickable { onChangeAddressClick() }
              .padding(4.dp)
              .testTag("change_address_button")
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 2. Free Delivery progress notice
      if (subtotal < 499.0) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .background(Color(0xFFFFF8E1), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.LocalOffer, contentDescription = null, tint = DealAmber, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Add ₹${(499.0 - subtotal).toInt()} more to get FREE Delivery!",
            fontSize = 12.sp,
            color = Color(0xFFE65100),
            fontWeight = FontWeight.Medium
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      // 3. Cart Items List
      cartItems.forEach { item ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row {
              AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                  .data(item.product.imageUrls.firstOrNull())
                  .crossfade(true)
                  .build(),
                contentDescription = item.product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(80.dp)
                  .clip(RoundedCornerShape(6.dp))
              )

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.product.title,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  maxLines = 2
                )

                if (item.selectedVariant != null) {
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "${item.selectedVariant.variantType.uppercase()}: ${item.selectedVariant.variantValue}",
                    fontSize = 11.sp,
                    color = Color.Gray
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "₹${item.unitPrice.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "₹${(item.product.mrp + (item.selectedVariant?.priceDelta ?: 0.0)).toInt()}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${item.product.discountPercent}% off",
                    fontSize = 12.sp,
                    color = DiscountGreen,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            // Action row: Quantity +/- and Remove
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Quantity Stepper
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                  .padding(horizontal = 4.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "−",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier
                    .clickable { viewModel.updateCartQuantity(item.id, -1) }
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Text(
                  text = "${item.quantity}",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                  text = "+",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier
                    .clickable { viewModel.updateCartQuantity(item.id, 1) }
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }

              // Save to Wishlist & Remove
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Save for Later",
                  fontSize = 12.sp,
                  color = Color.DarkGray,
                  fontWeight = FontWeight.Medium,
                  modifier = Modifier
                    .clickable {
                      viewModel.toggleWishlist(item.product.id)
                      viewModel.removeFromCart(item.id)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Text(
                  text = "Remove",
                  fontSize = 12.sp,
                  color = Color.Red,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier
                    .clickable { viewModel.removeFromCart(item.id) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("remove_cart_item_${item.id}")
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 4. Coupons & Offers Card
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Apply Coupons & Bachat Codes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          if (appliedCoupon != null) {
            // Applied Coupon banner
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(LightDiscountGreen, RoundedCornerShape(6.dp))
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Code: ${appliedCoupon!!.code} Applied!",
                  fontWeight = FontWeight.Bold,
                  color = DiscountGreen,
                  fontSize = 13.sp
                )
                Text(
                  text = appliedCoupon!!.description,
                  fontSize = 11.sp,
                  color = Color.DarkGray
                )
              }

              IconButton(onClick = { viewModel.removeCoupon() }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Remove coupon", tint = Color.Red)
              }
            }
          } else {
            // Coupon input
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = couponInput,
                onValueChange = {
                  couponInput = it.uppercase()
                  couponError = null
                },
                placeholder = { Text("Enter coupon code", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("coupon_input")
              )

              Spacer(modifier = Modifier.width(8.dp))

              Button(
                onClick = {
                  val result = viewModel.applyCoupon(couponInput)
                  if (!result.first) {
                    couponError = result.second
                  } else {
                    couponInput = ""
                    couponError = null
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                  .height(48.dp)
                  .testTag("apply_coupon_button")
              ) {
                Text("APPLY", fontWeight = FontWeight.Bold)
              }
            }

            if (couponError != null) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(couponError!!, color = Color.Red, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Click to apply code:", fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              listOf("WELCOME100", "DIWALI20", "FLIP50").forEach { code ->
                AssistChip(
                  onClick = {
                    val result = viewModel.applyCoupon(code)
                    if (!result.first) couponError = result.second
                  },
                  label = { Text(code, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 5. Price Details Summary Card
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "PRICE DETAILS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Subtotal
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Price (${cartItems.size} items)", fontSize = 13.sp)
            Text("₹${totalMrp.toInt()}", fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(6.dp))

          // MRP Discount
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Discount on MRP", fontSize = 13.sp)
            Text("-₹${mrpDiscount.toInt()}", fontSize = 13.sp, color = DiscountGreen, fontWeight = FontWeight.SemiBold)
          }

          if (couponDiscount > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Coupon Discount (${appliedCoupon?.code})", fontSize = 13.sp)
              Text("-₹${couponDiscount.toInt()}", fontSize = 13.sp, color = DiscountGreen, fontWeight = FontWeight.SemiBold)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Delivery Charges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Delivery Charges", fontSize = 13.sp)
            Text(
              text = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
              fontSize = 13.sp,
              color = if (deliveryFee == 0.0) DiscountGreen else Color.Black,
              fontWeight = FontWeight.SemiBold
            )
          }

          Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

          // Total Amount
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Total Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("₹${finalTotal.toInt()}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = Color.Black)
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Savings highlight
          Text(
            text = "You will save ₹${totalSavings.toInt()} on this order 🎉",
            color = DiscountGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Safe checkout badge
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Security, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Safe and Secure Payments • Easy returns", fontSize = 11.sp, color = Color.Gray)
      }
    }

    // 6. Sticky Bottom Checkout Bar
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .navigationBarsPadding(),
      shadowElevation = 8.dp,
      color = Color.White
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "₹${finalTotal.toInt()}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
          )
          Text(
            text = "View Price Details",
            fontSize = 11.sp,
            color = FlipkartBlue,
            fontWeight = FontWeight.SemiBold
          )
        }

        Button(
          onClick = { viewModel.navigateTo(AppScreen.CHECKOUT) },
          colors = ButtonDefaults.buttonColors(containerColor = DealAmber),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .width(180.dp)
            .height(48.dp)
            .testTag("place_order_button")
        ) {
          Text("Place Order", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    }
  }
}
