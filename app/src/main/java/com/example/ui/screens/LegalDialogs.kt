package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.FlipkartBlue

@Composable
fun LegalDialog(
  topic: String,
  onDismiss: () -> Unit
) {
  val (title, content) = when (topic) {
    "returns" -> "7-Day Return & Refund Policy" to """
      1. Return Window: We offer a hassle-free 7-day replacement or return policy from the date of package delivery.
      2. Eligible Items: Sarees, Kurtis, Electronics, Footwear and Home essentials with all original tags, boxes, and warranty cards intact.
      3. Instant Refund: Once picked up by our courier partner (Ekart / Delhivery), refund is initiated within 2 hours to your original payment method (Google Pay, PhonePe, Cards) or bank account for Cash on Delivery orders.
      4. Damaged or Defective Items: Free immediate exchange doorstep service available with zero additional courier fees.
    """.trimIndent()

    "privacy" -> "Privacy Policy" to """
      1. Information Collection: We respect your privacy. We only collect essential information required to deliver your orders (Name, Delivery Address, Phone Number, Email).
      2. Payment Security: All online transactions are processed through RBI-approved PCI-DSS Level 1 certified gateways (Razorpay). BharatBazaar never stores your credit card CVV or banking netbanking passwords.
      3. Data Confidentiality: Your personal address and contact details will never be sold or rented to third-party marketing companies.
      4. Communication: We only send transactional order tracking SMS, WhatsApp updates and delivery notifications.
    """.trimIndent()

    "terms" -> "Terms of Service" to """
      1. Marketplace Model: BharatBazaar is a single-seller e-commerce marketplace platform providing verified consumer goods directly from Indian manufacturers at wholesale rates.
      2. Pricing & Currency: All product prices are quoted in Indian Rupees (INR ₹) inclusive of all applicable Goods and Services Tax (GST).
      3. Delivery Timelines: Standard delivery across all major Indian pincodes takes 2 to 4 business days. Remote locations may take up to 6 business days.
      4. Order Cancellation: Orders can be cancelled free of charge at any time before dispatch from the fulfillment center.
    """.trimIndent()

    "about" -> "About BharatBazaar" to """
      BharatBazaar was founded with a single mission: To bring the vibrant energy, wholesale pricing, and trust of India's largest retail bazaars directly to every smartphone screen in India.
      
      Inspired by the simplicity of Meesho and the speed of Flipkart, BharatBazaar delivers authentic regional weaves, trending electronic accessories, and durable kitchenware to tier-1, tier-2, and rural pin codes with equal dedication.
      
      Headquarters: New Delhi, India.
    """.trimIndent()

    else -> "24x7 Customer Support" to """
      Toll-Free Customer Care: 1800-200-9999 (Available 24x7 in Hindi and English)
      Email Support: support@bharatbazaar.in
      Head Office: Plot 42, Okhla Industrial Area Phase 3, New Delhi - 110020
      WhatsApp Tracking Support: +91 98765 00000
    """.trimIndent()
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
        .testTag("legal_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(16.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Policy, contentDescription = null, tint = FlipkartBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
          text = content,
          fontSize = 12.sp,
          color = Color(0xFF333333),
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Understood / Theek Hai")
        }
      }
    }
  }
}
