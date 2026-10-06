package com.example.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.BuildConfig
import com.example.model.Address
import com.example.model.AppScreen
import com.example.model.Order
import com.example.model.PaymentMethod
import com.example.ui.components.RazorpayWebViewDialog
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.LightDiscountGreen
import com.example.viewmodel.ECommerceViewModel

@Composable
fun CheckoutScreen(
  viewModel: ECommerceViewModel,
  modifier: Modifier = Modifier
) {
  val cartItems by viewModel.cartItems.collectAsState()
  val appliedCoupon by viewModel.appliedCoupon.collectAsState()
  val selectedAddress by viewModel.selectedAddress.collectAsState()
  val addresses by viewModel.addresses.collectAsState()
  val isHinglish by viewModel.isHinglish.collectAsState()

  // Payment method selector: only ONLINE or COD
  var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.ONLINE) }
  var codCaptchaInput by remember { mutableStateOf("") }
  val randomCaptcha = remember { (1000..9999).random().toString() }

  // State control for Razorpay and errors
  var paymentErrorMessage by remember { mutableStateOf<String?>(null) }
  var showRazorpayDialog by remember { mutableStateOf(false) }
  var currentPayingOrder by remember { mutableStateOf<Order?>(null) }
  var placedOrder by remember { mutableStateOf<Order?>(null) }
  var showNewAddressDialog by remember { mutableStateOf(false) }

  val subtotal = cartItems.sumOf { it.totalPrice }
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

  val razorpayKeyId = remember {
    try {
      BuildConfig::class.java.getField("VITE_RAZORPAY_KEY_ID").get(null) as? String
    } catch (e: Exception) { null } ?: "rzp_test_1DP5mmOlF5G5ag"
  }

  val supabaseUrl = remember {
    try {
      BuildConfig::class.java.getField("VITE_SUPABASE_URL").get(null) as? String
    } catch (e: Exception) { null } ?: ""
  }

  val supabaseAnonKey = remember {
    try {
      BuildConfig::class.java.getField("VITE_SUPABASE_ANON_KEY").get(null) as? String
    } catch (e: Exception) { null } ?: ""
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .testTag("checkout_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(bottom = 80.dp)
    ) {
      // Top Navigation
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = { viewModel.navigateBack() }) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Checkout & Payment",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // PROMINENT PAYMENT ERROR BANNER (shows on failure, retry available)
      if (paymentErrorMessage != null) {
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.Error,
              contentDescription = "Error",
              tint = Color(0xFFC62828),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Payment Unsuccessful",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFFC62828)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = paymentErrorMessage ?: "Unknown payment error",
                fontSize = 11.sp,
                color = Color(0xFFB71C1C)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = {
                    paymentErrorMessage = null
                    if (cartItems.isNotEmpty()) {
                      val order = viewModel.createPendingOrder(PaymentMethod.ONLINE)
                      currentPayingOrder = order
                      showRazorpayDialog = true
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.height(34.dp)
                ) {
                  Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Retry Payment", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                  onClick = {
                    selectedPaymentMethod = PaymentMethod.COD
                    paymentErrorMessage = null
                  },
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.height(34.dp)
                ) {
                  Text("Choose COD Instead", fontSize = 11.sp)
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
      }

      // 1. Delivery Address Card
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .background(FlipkartBlue, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text("1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Button(
              onClick = { showNewAddressDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue.copy(alpha = 0.1f)),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Add New", color = FlipkartBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Current selected address
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF7F8FA), RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(selectedAddress.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Box(
                modifier = Modifier
                  .background(Color.LightGray, RoundedCornerShape(3.dp))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(selectedAddress.addressType, fontSize = 9.sp, fontWeight = FontWeight.Bold)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(selectedAddress.phone, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${selectedAddress.addressLine1}, ${selectedAddress.addressLine2}",
              fontSize = 12.sp,
              color = Color.DarkGray
            )
            Text(
              text = "${selectedAddress.city}, ${selectedAddress.state} - ${selectedAddress.pincode}",
              fontSize = 12.sp,
              color = Color.DarkGray
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 2. Order Items Review Card
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
            Box(
              modifier = Modifier
                .size(24.dp)
                .background(FlipkartBlue, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("2", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Order Summary (${cartItems.size} items)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          Spacer(modifier = Modifier.height(8.dp))

          cartItems.forEach { item ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                  .data(item.product.imageUrls.firstOrNull())
                  .crossfade(true)
                  .build(),
                contentDescription = item.product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(46.dp)
                  .clip(RoundedCornerShape(4.dp))
              )

              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(item.product.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                Text("Qty: ${item.quantity} • ₹${item.totalPrice.toInt()}", fontSize = 11.sp, color = Color.Gray)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 3. Payment Method Selection (ONLINE or COD)
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
            Box(
              modifier = Modifier
                .size(24.dp)
                .background(FlipkartBlue, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text("3", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Option 1: Pay Online (UPI / Card / Netbanking)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .border(
                1.dp,
                if (selectedPaymentMethod == PaymentMethod.ONLINE) FlipkartBlue else Color.LightGray,
                RoundedCornerShape(8.dp)
              )
              .clickable { selectedPaymentMethod = PaymentMethod.ONLINE }
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = selectedPaymentMethod == PaymentMethod.ONLINE,
              onClick = { selectedPaymentMethod = PaymentMethod.ONLINE },
              colors = RadioButtonDefaults.colors(selectedColor = FlipkartBlue)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "Pay Online (UPI / Card / Netbanking)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .background(Color(0xFFE8F0FE), RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                  Text("Razorpay Secure", fontSize = 10.sp, color = FlipkartBlue, fontWeight = FontWeight.Bold)
                }
              }

              Text(
                text = "Google Pay, PhonePe, Paytm, BHIM, Cards & Netbanking",
                fontSize = 11.sp,
                color = Color.Gray
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Option 2: Cash on Delivery (COD)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .border(
                1.dp,
                if (selectedPaymentMethod == PaymentMethod.COD) FlipkartBlue else Color.LightGray,
                RoundedCornerShape(8.dp)
              )
              .clickable { selectedPaymentMethod = PaymentMethod.COD }
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = selectedPaymentMethod == PaymentMethod.COD,
              onClick = { selectedPaymentMethod = PaymentMethod.COD },
              colors = RadioButtonDefaults.colors(selectedColor = FlipkartBlue)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
              Text(
                text = "Cash on Delivery",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Text(
                text = "Pay cash or UPI to delivery agent upon receiving package",
                fontSize = 11.sp,
                color = Color.Gray
              )
            }
          }

          // If COD selected, require quick Indian captcha code
          if (selectedPaymentMethod == PaymentMethod.COD) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFFDE7), RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              Text("Order Verification Code:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .background(Color(0xFF263238), RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = randomCaptcha,
                    color = FlipkartYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 4.sp
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                  value = codCaptchaInput,
                  onValueChange = { codCaptchaInput = it },
                  placeholder = { Text("Enter Code", fontSize = 11.sp) },
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier
                    .width(130.dp)
                    .height(48.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 4. Price Breakdown Summary
      Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Final Payment Breakdown", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", fontSize = 12.sp)
            Text("₹${subtotal.toInt()}", fontSize = 12.sp)
          }
          if (couponDiscount > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Coupon Discount", fontSize = 12.sp, color = DiscountGreen)
              Text("-₹${couponDiscount.toInt()}", fontSize = 12.sp, color = DiscountGreen)
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Delivery Fee", fontSize = 12.sp)
            Text(if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}", fontSize = 12.sp)
          }
          Divider(modifier = Modifier.padding(vertical = 8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Amount Payable", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("₹${finalTotal.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = FlipkartBlue)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Security, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Safe and Secure Payments • 256-Bit SSL", fontSize = 11.sp, color = Color.Gray)
      }
    }

    // Sticky Bottom Bar
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
          Text("₹${finalTotal.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black)
          Text("Total Payable", fontSize = 11.sp, color = Color.Gray)
        }

        val isCodValid = selectedPaymentMethod != PaymentMethod.COD || codCaptchaInput == randomCaptcha

        Button(
          onClick = {
            if (selectedPaymentMethod == PaymentMethod.COD) {
              if (isCodValid) {
                // Strictly COD: creates and confirms order directly
                val order = viewModel.createPendingOrder(PaymentMethod.COD)
                placedOrder = order
              } else {
                viewModel.showToast("Please enter correct verification code")
              }
            } else {
              // Strictly Online: creates order with pending_payment, opens Razorpay Checkout
              if (cartItems.isEmpty()) {
                viewModel.showToast("Cart is empty")
                return@Button
              }
              paymentErrorMessage = null
              val pendingOrder = viewModel.createPendingOrder(PaymentMethod.ONLINE)
              currentPayingOrder = pendingOrder
              showRazorpayDialog = true
            }
          },
          enabled = isCodValid && cartItems.isNotEmpty(),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (selectedPaymentMethod == PaymentMethod.ONLINE) DealAmber else FlipkartBlue
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .width(210.dp)
            .height(48.dp)
            .testTag("confirm_order_button")
        ) {
          Text(
            text = if (selectedPaymentMethod == PaymentMethod.ONLINE) "Pay Now via Razorpay" else "Confirm COD Order",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }

    // Razorpay In-App WebView Dialog (Executes actual window.Razorpay checkout.js flow)
    if (showRazorpayDialog && currentPayingOrder != null) {
      RazorpayWebViewDialog(
        order = currentPayingOrder!!,
        supabaseUrl = supabaseUrl,
        supabaseAnonKey = supabaseAnonKey,
        razorpayKeyId = razorpayKeyId,
        onPaymentSuccess = { rzpOrderId, rzpPaymentId, signature ->
          Log.i("CheckoutScreen", "Payment verified by Razorpay: $rzpPaymentId")
          val confirmed = viewModel.confirmOrderPaid(currentPayingOrder!!.id, rzpOrderId, rzpPaymentId)
          showRazorpayDialog = false
          paymentErrorMessage = null
          placedOrder = confirmed ?: currentPayingOrder
        },
        onPaymentError = { errorMsg ->
          Log.e("CheckoutScreen", "Payment failed or rejected: $errorMsg")
          viewModel.recordPaymentFailed(currentPayingOrder!!.id, errorMsg)
          paymentErrorMessage = errorMsg
          showRazorpayDialog = false
          placedOrder = null
        },
        onDismiss = {
          Log.i("CheckoutScreen", "Razorpay popup closed without payment")
          val cancelMsg = "Payment was cancelled or window was closed. You can retry anytime."
          viewModel.recordPaymentFailed(currentPayingOrder!!.id, cancelMsg)
          paymentErrorMessage = cancelMsg
          showRazorpayDialog = false
          placedOrder = null
        }
      )
    }

    // Success Celebration Dialog - ONLY DISPLAYED when payment is verified or COD confirmed!
    if (placedOrder != null) {
      Dialog(onDismissRequest = { /* Must click view orders */ }) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("order_success_dialog")
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(64.dp)
                .background(LightDiscountGreen, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = DiscountGreen,
                modifier = Modifier.size(44.dp)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = if (isHinglish) "Badhaai Ho! Order Confirmed! 🎉" else "Order Confirmed! 🎉",
              fontWeight = FontWeight.Black,
              fontSize = 18.sp,
              color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Order ID: ${placedOrder!!.orderNumber}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = FlipkartBlue
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Payment: ${placedOrder!!.paymentStatus}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = DiscountGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = "Expected delivery to ${placedOrder!!.deliveryAddress.city} in 3-4 business days.",
              fontSize = 12.sp,
              color = Color.DarkGray,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                placedOrder = null
                viewModel.navigateTo(AppScreen.ORDERS)
              },
              colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("view_orders_after_place_button")
            ) {
              Text("Track in My Orders", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Add New Address Dialog
    if (showNewAddressDialog) {
      var name by remember { mutableStateOf("") }
      var phone by remember { mutableStateOf("") }
      var line1 by remember { mutableStateOf("") }
      var city by remember { mutableStateOf("") }
      var state by remember { mutableStateOf("") }
      var pin by remember { mutableStateOf("") }

      Dialog(onDismissRequest = { showNewAddressDialog = false }) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color.White,
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(
            modifier = Modifier
              .padding(16.dp)
              .verticalScroll(rememberScrollState())
          ) {
            Text("Add Delivery Address", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Full Name") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("10-Digit Mobile Number") },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = line1,
              onValueChange = { line1 = it },
              label = { Text("Flat, House No., Building") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
              OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.weight(1f)
              )
              Spacer(modifier = Modifier.width(8.dp))
              OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("PIN Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
              )
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = state,
              onValueChange = { state = it },
              label = { Text("State") },
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { showNewAddressDialog = false },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                modifier = Modifier.weight(1f)
              ) {
                Text("Cancel", color = Color.Black)
              }

              Button(
                onClick = {
                  if (name.isNotBlank() && phone.isNotBlank() && line1.isNotBlank()) {
                    val newAddr = Address(
                      id = "addr_${System.currentTimeMillis()}",
                      fullName = name,
                      phone = phone,
                      addressLine1 = line1,
                      city = city.ifBlank { "New Delhi" },
                      state = state.ifBlank { "Delhi" },
                      pincode = pin.ifBlank { "110001" },
                      addressType = "HOME"
                    )
                    viewModel.addAddress(newAddr)
                    showNewAddressDialog = false
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                modifier = Modifier.weight(1f)
              ) {
                Text("Save")
              }
            }
          }
        }
      }
    }
  }
}
