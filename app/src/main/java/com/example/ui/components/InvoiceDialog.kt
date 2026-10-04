package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Order
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue

@Composable
fun InvoiceDialog(
  order: Order,
  onDismiss: () -> Unit,
  onDownload: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("invoice_dialog"),
      shape = RoundedCornerShape(12.dp),
      color = Color.White
    ) {
      Column(
        modifier = Modifier
          .padding(16.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Receipt,
              contentDescription = "Invoice",
              tint = FlipkartBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Tax Invoice / Bill of Supply",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color.Black
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close invoice")
          }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Seller & Buyer details
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "SOLD BY:",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Gray
            )
            Text(
              text = "BharatBazaar India Pvt Ltd",
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )
            Text(
              text = "Plot 42, Okhla Phase 3, New Delhi - 110020",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
            Text(
              text = "GSTIN: 07AAECB9182F1Z4",
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = FlipkartBlue
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "BILLING DETAILS:",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Gray
            )
            Text(
              text = order.deliveryAddress.fullName,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )
            Text(
              text = "${order.deliveryAddress.addressLine1}, ${order.deliveryAddress.city} - ${order.deliveryAddress.pincode}",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
            Text(
              text = "Phone: ${order.deliveryAddress.phone}",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Order metadata
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FA)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Order ID: ${order.orderNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              Text("Date: ${order.createdAt}", fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Invoice No: INV-2026-${order.orderNumber.takeLast(4)}", fontSize = 11.sp)
              Text("Payment: ${order.paymentStatus}", fontSize = 11.sp, color = DiscountGreen, fontWeight = FontWeight.Medium)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Items table header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8F0FE), RoundedCornerShape(4.dp))
            .padding(8.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Item Description", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
          Text("Qty", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
          Text("Price", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }

        order.items.forEach { item ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(2f)) {
              Text(item.productTitle, fontSize = 12.sp, fontWeight = FontWeight.Medium)
              if (item.variantInfo.isNotEmpty()) {
                Text("Variant: ${item.variantInfo}", fontSize = 10.sp, color = Color.Gray)
              }
            }
            Text("${item.quantity}", fontSize = 12.sp, modifier = Modifier.weight(0.5f))
            Text("₹${item.totalPrice.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
          }
          Divider(color = Color(0xFFEEEEEE))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Price breakdown
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", fontSize = 12.sp)
            Text("₹${order.subtotal.toInt()}", fontSize = 12.sp)
          }
          if (order.discountAmount > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Discount (${order.couponCode ?: "Offer"})", fontSize = 12.sp, color = DiscountGreen)
              Text("-₹${order.discountAmount.toInt()}", fontSize = 12.sp, color = DiscountGreen)
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Delivery Fee", fontSize = 12.sp)
            Text(if (order.deliveryFee == 0.0) "FREE" else "₹${order.deliveryFee.toInt()}", fontSize = 12.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("IGST (18% Included)", fontSize = 11.sp, color = Color.Gray)
            Text("₹${(order.totalAmount * 0.18 / 1.18).toInt()}", fontSize = 11.sp, color = Color.Gray)
          }
          Divider(modifier = Modifier.padding(vertical = 6.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Amount Paid", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("₹${order.totalAmount.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = FlipkartBlue)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Download / Share buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
          ) {
            Text("Close")
          }

          Button(
            onClick = onDownload,
            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Download, contentDescription = "Download")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Download")
          }
        }
      }
    }
  }
}
