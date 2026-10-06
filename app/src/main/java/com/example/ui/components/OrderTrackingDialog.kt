package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow

data class TrackingMilestone(
  val stage: OrderStatus,
  val title: String,
  val description: String,
  val timestamp: String,
  val location: String,
  val icon: ImageVector
)

@Composable
fun OrderTrackingDialog(
  order: Order,
  onDismiss: () -> Unit,
  onUpdateStatus: ((OrderStatus) -> Unit)? = null
) {
  val context = LocalContext.current
  var showDemoStatusSwitcher by remember { mutableStateOf(false) }

  val milestones = listOf(
    TrackingMilestone(
      stage = OrderStatus.PLACED,
      title = "Order Confirmed",
      description = "Order placed and payment verified successfully.",
      timestamp = order.createdAt,
      location = "Online",
      icon = Icons.Default.CheckCircle
    ),
    TrackingMilestone(
      stage = OrderStatus.PACKED,
      title = "Packed & Ready",
      description = "Item picked and safely packed in eco-friendly bubble box.",
      timestamp = "Today, 11:30 AM",
      location = "Gurugram Fulfillment Hub",
      icon = Icons.Default.Inventory2
    ),
    TrackingMilestone(
      stage = OrderStatus.SHIPPED,
      title = "Shipped in Transit",
      description = "Handed over to ${order.courierPartner}. In transit to delivery facility.",
      timestamp = "Today, 02:45 PM",
      location = "North Delhi Transit Center",
      icon = Icons.Default.LocalShipping
    ),
    TrackingMilestone(
      stage = OrderStatus.OUT_FOR_DELIVERY,
      title = "Out for Delivery",
      description = "Package is with Delivery Hero Rajesh Kumar. Expect delivery before 8 PM.",
      timestamp = "Today, 05:10 PM",
      location = "South Delhi Local Hub",
      icon = Icons.Default.LocalShipping
    ),
    TrackingMilestone(
      stage = OrderStatus.DELIVERED,
      title = "Delivered",
      description = "Package handed over directly to recipient.",
      timestamp = order.estimatedDeliveryDate,
      location = "${order.deliveryAddress.city} - ${order.deliveryAddress.pincode}",
      icon = Icons.Default.CheckCircle
    )
  )

  val currentStepIndex = order.status.stepIndex

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 16.dp)
        .testTag("order_tracking_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // 1. Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFE8F0FE), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                tint = FlipkartBlue,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Live Order Tracking",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF212121)
              )
              Text(
                text = "Order #${order.orderNumber}",
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. High-Level Status Banner Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = when (order.status) {
              OrderStatus.DELIVERED -> Color(0xFFE8F5E9)
              OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFFFF8E1)
              OrderStatus.CANCELLED -> Color(0xFFFFEBEE)
              else -> Color(0xFFF0F4FF)
            }
          ),
          modifier = Modifier.fillMaxWidth()
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
                    .size(10.dp)
                    .background(
                      when (order.status) {
                        OrderStatus.DELIVERED -> DiscountGreen
                        OrderStatus.OUT_FOR_DELIVERY -> DealAmber
                        OrderStatus.CANCELLED -> Color.Red
                        else -> FlipkartBlue
                      },
                      CircleShape
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = order.status.label.uppercase(),
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  color = when (order.status) {
                    OrderStatus.DELIVERED -> DiscountGreen
                    OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFE65100)
                    OrderStatus.CANCELLED -> Color.Red
                    else -> FlipkartBlue
                  }
                )
              }

              Text(
                text = order.estimatedDeliveryDate,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Courier and AWB snippet
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Partner: ${order.courierPartner}",
                  fontSize = 11.sp,
                  color = Color.DarkGray
                )
                Text(
                  text = "AWB: ${order.trackingId}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = FlipkartBlue
                )
              }

              if (order.status == OrderStatus.OUT_FOR_DELIVERY) {
                Box(
                  modifier = Modifier
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .border(1.dp, DealAmber, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "Delivery OTP: 4892",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Detailed Vertical Stepper Tracking Timeline
        Text(
          text = "Shipment Journey",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
          milestones.forEachIndexed { index, milestone ->
            val isCompleted = currentStepIndex >= milestone.stage.stepIndex
            val isCurrent = currentStepIndex == milestone.stage.stepIndex
            val isLast = index == milestones.lastIndex

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.Top
            ) {
              // Icon + Connector Column
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(36.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                      when {
                        isCurrent -> FlipkartBlue
                        isCompleted -> DiscountGreen
                        else -> Color(0xFFE0E0E0)
                      }
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (isCompleted && !isCurrent) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                  } else if (isCurrent) {
                    Icon(
                      imageVector = milestone.icon,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }

                if (!isLast) {
                  Box(
                    modifier = Modifier
                      .width(2.dp)
                      .height(46.dp)
                      .background(
                        if (currentStepIndex > milestone.stage.stepIndex) DiscountGreen else Color(0xFFE0E0E0)
                      )
                  )
                }
              }

              Spacer(modifier = Modifier.width(10.dp))

              // Milestone Text Info
              Column(
                modifier = Modifier
                  .weight(1f)
                  .padding(bottom = if (!isLast) 12.dp else 0.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = milestone.title,
                    fontWeight = if (isCurrent) FontWeight.Black else if (isCompleted) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    color = if (isCurrent) FlipkartBlue else if (isCompleted) Color(0xFF212121) else Color.Gray
                  )

                  if (isCompleted) {
                    Text(
                      text = milestone.timestamp,
                      fontSize = 10.sp,
                      color = Color.Gray
                    )
                  }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = milestone.description,
                  fontSize = 11.sp,
                  color = if (isCompleted) Color.DarkGray else Color.Gray,
                  lineHeight = 15.sp
                )

                if (isCompleted) {
                  Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.LocationOn,
                      contentDescription = null,
                      tint = Color.Gray,
                      modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                      text = milestone.location,
                      fontSize = 10.sp,
                      color = Color.Gray
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(14.dp))

        // 4. Delivery Address Section
        Text(
          text = "Delivery Destination",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
            .padding(10.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = FlipkartBlue,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "${order.deliveryAddress.fullName} • ${order.deliveryAddress.phone}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${order.deliveryAddress.addressLine1}, ${order.deliveryAddress.city} - ${order.deliveryAddress.pincode}",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Interactive Demo Mode: Stage Simulator Bar
        if (onUpdateStatus != null) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Simulate Shipping Status:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.DarkGray
              )
              Text(
                text = "Tap to update live",
                fontSize = 10.sp,
                color = FlipkartBlue
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              listOf(
                OrderStatus.PLACED to "Placed",
                OrderStatus.PACKED to "Packed",
                OrderStatus.SHIPPED to "Shipped",
                OrderStatus.OUT_FOR_DELIVERY to "Out",
                OrderStatus.DELIVERED to "Delivered"
              ).forEach { (st, label) ->
                val isSelected = order.status == st
                Button(
                  onClick = { onUpdateStatus(st) },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) FlipkartBlue else Color.White,
                    contentColor = if (isSelected) Color.White else Color.DarkGray
                  ),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier
                    .weight(1f)
                    .height(30.dp)
                    .testTag("status_switch_${label.lowercase()}"),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                  Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        // Close Button
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("close_tracking_dialog_button")
        ) {
          Text("Done", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}
