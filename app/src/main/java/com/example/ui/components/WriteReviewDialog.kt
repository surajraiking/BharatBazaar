package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.model.Product
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WriteReviewDialog(
  product: Product,
  onDismiss: () -> Unit,
  onSubmitReview: (rating: Int, title: String, comment: String, reviewerName: String) -> Unit
) {
  var selectedRating by remember { mutableIntStateOf(5) }
  var reviewTitle by remember { mutableStateOf("") }
  var reviewComment by remember { mutableStateOf("") }
  var reviewerName by remember { mutableStateOf("") }

  val ratingLabels = mapOf(
    5 to "Terrific! 😍 Excellent purchase",
    4 to "Very Good 😊 Value for money",
    3 to "Good / Average 🙂 Decent quality",
    2 to "Disappointed 😐 Below expectation",
    1 to "Very Poor 😞 Not recommended"
  )

  val quickTags = listOf(
    "Superb Quality ✨",
    "Loved the Color 🎨",
    "Worth every Rupee 💰",
    "Fast Delivery 🚀",
    "100% Genuine 💯",
    "Perfect Fit 👕"
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color.White,
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)
        .testTag("write_review_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Rate & Review Product",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF212121)
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Product Snippet
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F8FA), RoundedCornerShape(8.dp))
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(product.imageUrls.firstOrNull())
              .crossfade(true)
              .build(),
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(6.dp))
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = product.title,
              fontWeight = FontWeight.Medium,
              fontSize = 12.sp,
              maxLines = 1,
              color = Color.DarkGray
            )
            Text(
              text = "₹${product.sellingPrice.toInt()}",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = FlipkartBlue
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Star Rating Selector
        Text(
          text = "Your Overall Rating:",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          (1..5).forEach { starIndex ->
            val isSelected = starIndex <= selectedRating
            val scale by animateFloatAsState(targetValue = if (starIndex == selectedRating) 1.25f else 1.0f, label = "star_scale")
            val starColor by animateColorAsState(
              targetValue = if (isSelected) Color(0xFFFFB300) else Color(0xFFE0E0E0),
              label = "star_color"
            )

            Box(
              modifier = Modifier
                .padding(horizontal = 6.dp)
                .scale(scale)
                .clickable { selectedRating = starIndex }
                .testTag("star_rating_$starIndex")
            ) {
              Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "$starIndex Stars",
                tint = starColor,
                modifier = Modifier.size(36.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Rating Feedback Label
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              when (selectedRating) {
                5, 4 -> Color(0xFFE8F5E9)
                3 -> Color(0xFFFFF8E1)
                else -> Color(0xFFFFEBEE)
              },
              RoundedCornerShape(6.dp)
            )
            .padding(vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = ratingLabels[selectedRating] ?: "",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = when (selectedRating) {
              5, 4 -> DiscountGreen
              3 -> Color(0xFFF57F17)
              else -> Color(0xFFC62828)
            }
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Feedback Chips
        Text(
          text = "Quick Highlights (Tap to include):",
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = Color.Gray
        )
        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          quickTags.forEach { tag ->
            val isSelected = reviewComment.contains(tag)
            FilterChip(
              selected = isSelected,
              onClick = {
                reviewComment = if (isSelected) {
                  reviewComment.replace(tag, "").trim()
                } else {
                  if (reviewComment.isBlank()) tag else "$reviewComment. $tag"
                }
              },
              label = { Text(tag, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = FlipkartBlue.copy(alpha = 0.15f),
                selectedLabelColor = FlipkartBlue
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Review Headline Input
        OutlinedTextField(
          value = reviewTitle,
          onValueChange = { reviewTitle = it },
          label = { Text("Headline / Title (e.g., Fabulous Saree!)", fontSize = 12.sp) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("review_title_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Review Text Input
        OutlinedTextField(
          value = reviewComment,
          onValueChange = { reviewComment = it },
          label = { Text("Detailed Review / Comments", fontSize = 12.sp) },
          placeholder = { Text("How was the quality, fit, and packaging? Would you recommend this?", fontSize = 12.sp) },
          minLines = 3,
          maxLines = 6,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("review_comment_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Reviewer Name Input
        OutlinedTextField(
          value = reviewerName,
          onValueChange = { reviewerName = it },
          label = { Text("Your Name (Optional)", fontSize = 12.sp) },
          placeholder = { Text("Verified Customer", fontSize = 12.sp) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("reviewer_name_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
          ) {
            Text("Cancel", fontSize = 13.sp)
          }

          Button(
            onClick = {
              val commentText = reviewComment.ifBlank { "Great product and experience!" }
              onSubmitReview(selectedRating, reviewTitle, commentText, reviewerName)
            },
            colors = ButtonDefaults.buttonColors(containerColor = DealAmber),
            modifier = Modifier
              .weight(1f)
              .testTag("submit_review_button")
          ) {
            Text("Submit Review", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
          }
        }
      }
    }
  }
}
