package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Product
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow

@Composable
fun ProductCard(
  product: Product,
  isWishlisted: Boolean,
  onProductClick: (Product) -> Unit,
  onWishlistToggle: (Product) -> Unit,
  onQuickAdd: (Product) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onProductClick(product) }
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
  ) {
    Column {
      // Product Image + Wishlist & Discount overlay
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(175.dp)
          .background(Color(0xFFF8F9FA))
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(product.imageUrls.firstOrNull())
            .crossfade(true)
            .build(),
          contentDescription = product.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )

        // Wishlist Button
        IconButton(
          onClick = { onWishlistToggle(product) },
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(4.dp)
            .size(36.dp)
            .background(Color.White.copy(alpha = 0.85f), CircleShape)
            .testTag("wishlist_toggle_${product.id}")
        ) {
          Icon(
            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Toggle wishlist",
            tint = if (isWishlisted) Color(0xFFFF3366) else Color.Gray,
            modifier = Modifier.size(20.dp)
          )
        }

        // Discount / Tag Badge
        if (product.discountPercent > 0) {
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .background(
                color = DiscountGreen,
                shape = RoundedCornerShape(topEnd = 6.dp)
              )
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "${product.discountPercent}% OFF",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          }
        }
      }

      // Details
      Column(modifier = Modifier.padding(10.dp)) {
        // Brand & Category
        Text(
          text = product.brand.uppercase(),
          color = Color.Gray,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Title
        Text(
          text = product.title,
          color = Color(0xFF212121),
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Rating Pill (Flipkart green rating)
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .background(DiscountGreen, RoundedCornerShape(4.dp))
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${product.rating}",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(2.dp))
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "(${product.ratingCount})",
            color = Color.Gray,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Price Row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "₹${product.sellingPrice.toInt()}",
              color = Color.Black,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "₹${product.mrp.toInt()}",
              color = Color.Gray,
              fontSize = 12.sp,
              textDecoration = TextDecoration.LineThrough
            )
          }

          // Quick Add to Cart mini button
          IconButton(
            onClick = { onQuickAdd(product) },
            modifier = Modifier
              .size(32.dp)
              .background(FlipkartBlue.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
              .testTag("quick_add_${product.id}")
          ) {
            Icon(
              imageVector = Icons.Default.AddShoppingCart,
              contentDescription = "Quick add to cart",
              tint = FlipkartBlue,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Free Delivery or Assured Tag
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Free Delivery",
            color = DiscountGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "• In Stock",
            color = Color.Gray,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}
