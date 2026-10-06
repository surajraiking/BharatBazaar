package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.InvoiceDialog
import com.example.ui.components.OrderTrackingDialog
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.viewmodel.ECommerceViewModel

@Composable
fun OrdersScreen(
  viewModel: ECommerceViewModel,
  modifier: Modifier = Modifier
) {
  val orders by viewModel.orders.collectAsState()
  val isHinglish by viewModel.isHinglish.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) }
  var invoiceOrderToView by remember { mutableStateOf<Order?>(null) }
  var orderToCancel by remember { mutableStateOf<Order?>(null) }
  var orderToReview by remember { mutableStateOf<Order?>(null) }
  var orderToTrack by remember { mutableStateOf<Order?>(null) }

  val tabs = listOf("All Orders", "On the Way", "Delivered", "Cancelled")

  val filteredOrders = orders.filter { order ->
    when (selectedTab) {
      1 -> order.status in listOf(
        OrderStatus.PLACED,
        OrderStatus.PACKED,
        OrderStatus.SHIPPED,
        OrderStatus.OUT_FOR_DELIVERY
      )
      2 -> order.status == OrderStatus.DELIVERED
      3 -> order.status == OrderStatus.CANCELLED
      else -> true
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .testTag("orders_screen")
  ) {
    // Header
    Surface(color = Color.White, shadowElevation = 1.dp) {
      Column {
        Text(
          text = if (isHinglish) "Mere Orders & Live Tracking 📦" else "My Orders & Tracking 📦",
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.White,
          contentColor = FlipkartBlue,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = FlipkartBlue
            )
          }
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  title,
                  fontSize = 12.sp,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                )
              }
            )
          }
        }
      }
    }

    if (filteredOrders.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = if (isHinglish) "Koi order nahi hai yahan" else "No orders found in this section",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
          )
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredOrders) { order ->
          OrderCard(
            order = order,
            onTrackOrder = { orderToTrack = order },
            onViewInvoice = { invoiceOrderToView = order },
            onCancelOrder = { orderToCancel = order },
            onReview = { orderToReview = order }
          )
        }
      }
    }

    // Detailed Live Order Tracking Dialog
    if (orderToTrack != null) {
      OrderTrackingDialog(
        order = orderToTrack!!,
        onDismiss = { orderToTrack = null },
        onUpdateStatus = { newStatus ->
          viewModel.updateOrderStatus(orderToTrack!!.id, newStatus)
          orderToTrack = orderToTrack!!.copy(status = newStatus)
        }
      )
    }

    // Invoice Dialog
    if (invoiceOrderToView != null) {
      InvoiceDialog(
        order = invoiceOrderToView!!,
        onDismiss = { invoiceOrderToView = null },
        onDownload = {
          viewModel.showToast("Invoice downloaded to device! 📄")
          invoiceOrderToView = null
        }
      )
    }

    // Cancel Order Dialog
    if (orderToCancel != null) {
      var cancelReason by remember { mutableStateOf("Changed my mind") }
      val reasons = listOf("Ordered by mistake", "Found cheaper elsewhere", "Delivery time too long", "Changed my mind")

      Dialog(onDismissRequest = { orderToCancel = null }) {
        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.padding(16.dp)) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Cancel Order", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Please select reason for cancellation:", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            reasons.forEach { reason ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { cancelReason = reason }
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = cancelReason == reason,
                  onClick = { cancelReason = reason }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(reason, fontSize = 13.sp)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedButton(onClick = { orderToCancel = null }, modifier = Modifier.weight(1f)) {
                Text("Back")
              }
              Button(
                onClick = {
                  viewModel.cancelOrder(orderToCancel!!.id, cancelReason)
                  orderToCancel = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.weight(1f)
              ) {
                Text("Confirm Cancel")
              }
            }
          }
        }
      }
    }

    // Rate & Review Dialog
    if (orderToReview != null) {
      var rating by remember { mutableIntStateOf(5) }
      var title by remember { mutableStateOf("") }
      var comment by remember { mutableStateOf("") }
      val firstItem = orderToReview!!.items.firstOrNull()

      Dialog(onDismissRequest = { orderToReview = null }) {
        Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.padding(16.dp)) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Rate & Review Product", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(firstItem?.productTitle ?: "Delivered Item", fontSize = 12.sp, color = Color.DarkGray, maxLines = 1)
            Spacer(modifier = Modifier.height(12.dp))

            // Star picker
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
              (1..5).forEach { star ->
                IconButton(onClick = { rating = star }) {
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "$star Stars",
                    tint = if (star <= rating) DealAmber else Color.LightGray,
                    modifier = Modifier.size(32.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = title,
              onValueChange = { title = it },
              label = { Text("Title (e.g. Excellent Product!)") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = comment,
              onValueChange = { comment = it },
              label = { Text("Your detailed feedback") },
              modifier = Modifier.fillMaxWidth(),
              minLines = 3
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedButton(onClick = { orderToReview = null }, modifier = Modifier.weight(1f)) {
                Text("Close")
              }
              Button(
                onClick = {
                  if (firstItem != null && comment.isNotBlank()) {
                    viewModel.addReview(firstItem.productId, rating, title.ifBlank { "Great purchase!" }, comment)
                    orderToReview = null
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                modifier = Modifier.weight(1f)
              ) {
                Text("Submit Review")
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun OrderCard(
  order: Order,
  onTrackOrder: () -> Unit,
  onViewInvoice: () -> Unit,
  onCancelOrder: () -> Unit,
  onReview: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Order ID & Status Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = "Order: ${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(text = order.createdAt, fontSize = 11.sp, color = Color.Gray)
        }

        val statusBg = when (order.status) {
          OrderStatus.DELIVERED -> DiscountGreen
          OrderStatus.CANCELLED -> Color.Red
          OrderStatus.SHIPPED, OrderStatus.OUT_FOR_DELIVERY -> FlipkartBlue
          else -> DealAmber
        }

        Box(
          modifier = Modifier
            .background(statusBg.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = order.status.label,
            color = statusBg,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

      // Items Thumbnail & Title
      order.items.forEach { item ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(item.productImage)
              .crossfade(true)
              .build(),
            contentDescription = item.productTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(6.dp))
          )

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(item.productTitle, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
            Text("Qty: ${item.quantity} • ₹${item.totalPrice.toInt()}", fontSize = 11.sp, color = Color.Gray)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // ORDER SHIPPING TRACKING PROGRESS TIMELINE
      if (order.status != OrderStatus.CANCELLED) {
        OrderShippingTracker(
          status = order.status,
          trackingId = order.trackingId,
          courier = order.courierPartner,
          estimatedDate = order.estimatedDeliveryDate,
          onTrackClick = onTrackOrder
        )
      } else {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFEBEE), RoundedCornerShape(6.dp))
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Cancel, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Order cancelled. Refund initiated to original source.", fontSize = 11.sp, color = Color.Red)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = Color(0xFFEEEEEE))
      Spacer(modifier = Modifier.height(8.dp))

      // Footer: Total & Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Total: ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(order.paymentStatus, fontSize = 10.sp, color = Color.Gray)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Track Order Button
          if (order.status != OrderStatus.CANCELLED) {
            Button(
              onClick = onTrackOrder,
              colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
              modifier = Modifier
                .height(34.dp)
                .testTag("track_order_${order.orderNumber}")
            ) {
              Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Track Order", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          OutlinedButton(
            onClick = onViewInvoice,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Invoice", fontSize = 11.sp)
          }

          if (order.status == OrderStatus.DELIVERED) {
            Button(
              onClick = onReview,
              colors = ButtonDefaults.buttonColors(containerColor = DealAmber),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text("Review", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          } else if (order.status != OrderStatus.CANCELLED && order.status != OrderStatus.OUT_FOR_DELIVERY) {
            OutlinedButton(
              onClick = onCancelOrder,
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text("Cancel", fontSize = 11.sp, color = Color.Red)
            }
          }
        }
      }
    }
  }
}

@Composable
fun OrderShippingTracker(
  status: OrderStatus,
  trackingId: String,
  courier: String,
  estimatedDate: String,
  onTrackClick: () -> Unit
) {
  val steps = listOf("Placed", "Packed", "Shipped", "Out for Delivery", "Delivered")
  val currentIdx = status.stepIndex

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
      .padding(12.dp)
  ) {
    // Current shipping stage headline
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = when (status) {
            OrderStatus.DELIVERED -> Icons.Default.CheckCircle
            OrderStatus.OUT_FOR_DELIVERY -> Icons.Default.LocalShipping
            OrderStatus.SHIPPED -> Icons.Default.Navigation
            OrderStatus.PACKED -> Icons.Default.Inventory2
            else -> Icons.Default.CheckCircle
          },
          contentDescription = null,
          tint = if (status == OrderStatus.DELIVERED) DiscountGreen else FlipkartBlue,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = status.label,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = if (status == OrderStatus.DELIVERED) DiscountGreen else FlipkartBlue
        )
      }

      Text(
        text = estimatedDate,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF475569)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive Stepper Bar with connecting lines
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      steps.forEachIndexed { index, stepName ->
        val isDone = index <= currentIdx
        val isCurrent = index == currentIdx

        // Step dot / icon
        Box(
          modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(
              when {
                isCurrent -> FlipkartBlue
                isDone -> DiscountGreen
                else -> Color(0xFFCBD5E1)
              }
            ),
          contentAlignment = Alignment.Center
        ) {
          if (isDone && !isCurrent) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
          } else if (isCurrent) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color.White)
            )
          }
        }

        // Connector line between dots
        if (index < steps.lastIndex) {
          Box(
            modifier = Modifier
              .weight(1f)
              .height(3.dp)
              .background(
                if (currentIdx > index) DiscountGreen else Color(0xFFE2E8F0)
              )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Step Labels
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "Placed",
        fontSize = 9.sp,
        fontWeight = if (currentIdx == 0) FontWeight.Bold else FontWeight.Normal,
        color = if (currentIdx >= 0) Color.DarkGray else Color.Gray
      )
      Text(
        text = "Packed",
        fontSize = 9.sp,
        fontWeight = if (currentIdx == 1) FontWeight.Bold else FontWeight.Normal,
        color = if (currentIdx >= 1) Color.DarkGray else Color.Gray
      )
      Text(
        text = "Shipped",
        fontSize = 9.sp,
        fontWeight = if (currentIdx == 2) FontWeight.Bold else FontWeight.Normal,
        color = if (currentIdx >= 2) Color.DarkGray else Color.Gray
      )
      Text(
        text = "Out for Delivery",
        fontSize = 9.sp,
        fontWeight = if (currentIdx == 3) FontWeight.Bold else FontWeight.Normal,
        color = if (currentIdx >= 3) Color.DarkGray else Color.Gray
      )
      Text(
        text = "Delivered",
        fontSize = 9.sp,
        fontWeight = if (currentIdx == 4) FontWeight.Bold else FontWeight.Normal,
        color = if (currentIdx >= 4) Color.DarkGray else Color.Gray
      )
    }

    if (currentIdx >= 2) {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Courier: $courier (AWB: $trackingId)",
          fontSize = 10.sp,
          color = Color(0xFF64748B),
          fontWeight = FontWeight.Medium
        )

        Text(
          text = "Live Map & Details ›",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = FlipkartBlue,
          modifier = Modifier.clickable { onTrackClick() }
        )
      }
    }
  }
}
