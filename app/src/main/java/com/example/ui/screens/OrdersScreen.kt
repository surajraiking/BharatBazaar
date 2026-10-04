package com.example.ui.screens

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
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.InvoiceDialog
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

  var selectedTab by remember { mutableStateOf(0) }
  var invoiceOrderToView by remember { mutableStateOf<Order?>(null) }
  var orderToCancel by remember { mutableStateOf<Order?>(null) }
  var orderToReview by remember { mutableStateOf<Order?>(null) }

  val tabs = listOf("All Orders", "On the Way", "Delivered", "Cancelled")

  val filteredOrders = orders.filter { order ->
    when (selectedTab) {
      1 -> order.status in listOf(OrderStatus.PLACED, OrderStatus.PACKED, OrderStatus.SHIPPED, OrderStatus.OUT_FOR_DELIVERY)
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
          text = if (isHinglish) "Mere Orders & Tracking 📦" else "My Orders & Tracking 📦",
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
            text = if (isHinglish) "Koi order nahi hai yahan" else "No orders found",
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
            onViewInvoice = { invoiceOrderToView = order },
            onCancelOrder = { orderToCancel = order },
            onReview = { orderToReview = order }
          )
        }
      }
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
                androidx.compose.material3.RadioButton(
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
      var rating by remember { mutableStateOf(5) }
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
  onViewInvoice: () -> Unit,
  onCancelOrder: () -> Unit,
  onReview: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
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

      Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

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

      // Interactive Order Status Timeline
      if (order.status != OrderStatus.CANCELLED) {
        OrderStatusTimelineView(status = order.status, trackingId = order.trackingId, courier = order.courierPartner)
      } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Cancel, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("This order was cancelled. Refund credited to original source.", fontSize = 11.sp, color = Color.Red)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Divider(color = Color(0xFFEEEEEE))
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
          OutlinedButton(
            onClick = onViewInvoice,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Invoice", fontSize = 11.sp)
          }

          if (order.status == OrderStatus.DELIVERED) {
            Button(
              onClick = onReview,
              colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text("Review", fontSize = 11.sp)
            }
          } else if (order.status != OrderStatus.CANCELLED) {
            OutlinedButton(
              onClick = onCancelOrder,
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
              modifier = Modifier.height(32.dp)
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
fun OrderStatusTimelineView(status: OrderStatus, trackingId: String, courier: String) {
  val steps = listOf("Placed", "Packed", "Shipped", "Out for Delivery", "Delivered")
  val currentIdx = status.stepIndex

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
      .padding(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      steps.forEachIndexed { index, stepName ->
        val isDone = index <= currentIdx
        val isCurrent = index == currentIdx

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(CircleShape)
              .background(if (isDone) DiscountGreen else Color.LightGray),
            contentAlignment = Alignment.Center
          ) {
            if (isDone) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = stepName,
            fontSize = 9.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = if (isDone) Color.Black else Color.Gray,
            maxLines = 1
          )
        }
      }
    }

    if (currentIdx >= 2) {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Courier: $courier • Tracking ID: $trackingId",
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = FlipkartBlue
      )
    }
  }
}
