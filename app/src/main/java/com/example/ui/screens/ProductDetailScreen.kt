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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.AppScreen
import com.example.model.Product
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.LightDiscountGreen
import com.example.viewmodel.ECommerceViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
  viewModel: ECommerceViewModel,
  modifier: Modifier = Modifier
) {
  val product = viewModel.selectedProduct.collectAsState().value
  val selectedVariant by viewModel.selectedVariant.collectAsState()
  val wishlistIds by viewModel.wishlistIds.collectAsState()
  val pincode by viewModel.pincode.collectAsState()
  val pincodeMessage by viewModel.pincodeMessage.collectAsState()
  val reviews by viewModel.reviews.collectAsState()
  val isHinglish by viewModel.isHinglish.collectAsState()

  if (product == null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("No product selected")
    }
    return
  }

  var currentImageIndex by remember { mutableStateOf(0) }
  var quantity by remember { mutableStateOf(1) }
  var pinInput by remember { mutableStateOf(pincode) }

  val isWishlisted = wishlistIds.contains(product.id)
  val currentPrice = product.sellingPrice + (selectedVariant?.priceDelta ?: 0.0)
  val currentMrp = product.mrp + (selectedVariant?.priceDelta ?: 0.0)
  val savings = currentMrp - currentPrice
  val productReviews = reviews.filter { it.productId == product.id }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .testTag("product_detail_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(bottom = 76.dp)
    ) {
      // 1. Top Bar inside detail screen
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.navigateBack() },
          modifier = Modifier.testTag("detail_back_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Row {
          IconButton(onClick = { viewModel.showToast("Product link copied!") }) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.DarkGray)
          }

          IconButton(
            onClick = { viewModel.toggleWishlist(product.id) },
            modifier = Modifier.testTag("detail_wishlist_button")
          ) {
            Icon(
              imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Wishlist",
              tint = if (isWishlisted) Color(0xFFFF3366) else Color.DarkGray
            )
          }
        }
      }

      // 2. Product Image Gallery
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(340.dp)
          .background(Color.White)
      ) {
        val currentImageUrl = product.imageUrls.getOrElse(currentImageIndex) {
          product.imageUrls.firstOrNull() ?: ""
        }

        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(currentImageUrl)
            .crossfade(true)
            .build(),
          contentDescription = product.title,
          contentScale = ContentScale.Fit,
          modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        )

        // Thumbnail Dots / Switchers
        if (product.imageUrls.size > 1) {
          Row(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            product.imageUrls.forEachIndexed { index, url ->
              Box(
                modifier = Modifier
                  .size(if (index == currentImageIndex) 10.dp else 8.dp)
                  .clip(CircleShape)
                  .background(if (index == currentImageIndex) FlipkartBlue else Color.LightGray)
                  .clickable { currentImageIndex = index }
              )
            }
          }
        }
      }

      // 3. Title, Brand & Rating
      Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = product.brand.uppercase(),
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = product.title,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF212121),
            lineHeight = 22.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Rating & Assured Badge
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .background(DiscountGreen, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${product.rating}",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "${product.ratingCount} Ratings & 450+ Reviews",
              fontSize = 12.sp,
              color = Color.Gray
            )

            Spacer(modifier = Modifier.width(10.dp))
            // Assured badge
            Box(
              modifier = Modifier
                .background(FlipkartBlue.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "✦ Assured",
                color = FlipkartBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Price Row
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "₹${currentPrice.toInt()}",
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              color = Color.Black
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "₹${currentMrp.toInt()}",
              fontSize = 15.sp,
              color = Color.Gray,
              textDecoration = TextDecoration.LineThrough
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "${product.discountPercent}% OFF",
              fontSize = 15.sp,
              color = DiscountGreen,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Bachat: You save ₹${savings.toInt()} on this product!",
            fontSize = 12.sp,
            color = DiscountGreen,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 4. Variant Selector (Size / Color)
      if (product.variants.isNotEmpty()) {
        Surface(
          color = Color.White,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            val variantType = product.variants.first().variantType.replaceFirstChar { it.uppercase() }
            Text(
              text = "Select $variantType:",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              product.variants.forEach { variant ->
                FilterChip(
                  selected = selectedVariant?.id == variant.id,
                  onClick = { viewModel.selectVariant(variant) },
                  label = {
                    Text(
                      text = variant.variantValue + if (variant.priceDelta > 0) " (+₹${variant.priceDelta.toInt()})" else "",
                      fontSize = 12.sp
                    )
                  },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = FlipkartBlue,
                    selectedLabelColor = Color.White
                  ),
                  modifier = Modifier.testTag("variant_${variant.id}")
                )
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      // 5. Quantity Selector
      Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Select Quantity:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "−",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clickable { if (quantity > 1) quantity-- }
                .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Text(
              text = "$quantity",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
              text = "+",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clickable { if (quantity < 10) quantity++ }
                .padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 6. Indian Delivery Pincode Checker
      Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Delivery & Services:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = pinInput,
              onValueChange = { if (it.length <= 6) pinInput = it },
              placeholder = { Text("Enter 6-digit Pincode") },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("pincode_detail_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
              onClick = { viewModel.checkPincode(pinInput) },
              colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(52.dp)
            ) {
              Text("Check")
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocalShipping,
              contentDescription = null,
              tint = DiscountGreen,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = pincodeMessage,
              fontSize = 12.sp,
              color = Color.DarkGray,
              fontWeight = FontWeight.Medium
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Cash on Delivery Available", fontSize = 11.sp, color = Color.DarkGray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("7 Days Replacement", fontSize = 11.sp, color = Color.DarkGray)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 7. Product Description & Specifications
      Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Product Details & Description:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = product.description,
            fontSize = 13.sp,
            color = Color(0xFF424242),
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Highlights:",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text("• Brand: ${product.brand}", fontSize = 12.sp, color = Color.DarkGray)
          Text("• Category: ${product.categoryName}", fontSize = 12.sp, color = Color.DarkGray)
          Text("• Quality: 100% Genuine Certified", fontSize = 12.sp, color = Color.DarkGray)
          Text("• Country of Origin: India", fontSize = 12.sp, color = Color.DarkGray)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 8. Ratings & Reviews
      Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Ratings & Reviews (${productReviews.size + 450}):",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          productReviews.forEach { review ->
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .background(DiscountGreen, RoundedCornerShape(3.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text("${review.rating} ★", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(review.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
              Spacer(modifier = Modifier.height(3.dp))
              Text(review.comment, fontSize = 12.sp, color = Color.DarkGray)
              Spacer(modifier = Modifier.height(2.dp))
              Text("${review.userName} • Verified Buyer • ${review.createdAt}", fontSize = 10.sp, color = Color.Gray)
              Divider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFEEEEEE))
            }
          }
        }
      }
    }

    // 9. Sticky Bottom Bar: Add to Cart (Yellow) & Buy Now (Orange)
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
          .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Add to Cart
        Button(
          onClick = {
            viewModel.addToCart(product, selectedVariant, quantity)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = FlipkartYellow,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("add_to_cart_button")
        ) {
          Text(
            text = if (isHinglish) "Add to Cart 🛒" else "Add to Cart",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }

        // Buy Now
        Button(
          onClick = {
            viewModel.addToCart(product, selectedVariant, quantity)
            viewModel.navigateTo(AppScreen.CHECKOUT)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = DealAmber,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("buy_now_button")
        ) {
          Text(
            text = if (isHinglish) "Buy Now ⚡" else "Buy Now",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }
  }
}
