package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.model.Review
import com.example.ui.components.WriteReviewDialog
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

  var currentImageIndex by remember { mutableIntStateOf(0) }
  var quantity by remember { mutableIntStateOf(1) }
  var pinInput by remember { mutableStateOf(pincode) }
  var showWriteReviewDialog by remember { mutableStateOf(false) }
  var selectedReviewFilter by remember { mutableStateOf("All") }
  val helpfulLikes = remember { mutableStateMapOf<String, Int>() }

  val isWishlisted = wishlistIds.contains(product.id)
  val currentPrice = product.sellingPrice + (selectedVariant?.priceDelta ?: 0.0)
  val currentMrp = product.mrp + (selectedVariant?.priceDelta ?: 0.0)
  val savings = currentMrp - currentPrice
  val productReviews = reviews.filter { it.productId == product.id }

  // Rating metrics
  val totalRatingsCount = product.ratingCount + productReviews.size
  val totalReviewsCount = productReviews.size + (product.ratingCount / 4).coerceAtLeast(1)

  // Star distribution simulation based on reviews & rating
  val fiveStarPct = 0.68f
  val fourStarPct = 0.20f
  val threeStarPct = 0.07f
  val twoStarPct = 0.03f
  val oneStarPct = 0.02f

  // Filtered reviews list
  val filteredReviews = when (selectedReviewFilter) {
    "5 ★" -> productReviews.filter { it.rating == 5 }
    "4 ★" -> productReviews.filter { it.rating == 4 }
    "3 ★ & Below" -> productReviews.filter { it.rating <= 3 }
    "Verified" -> productReviews.filter { it.isVerifiedPurchase }
    else -> productReviews
  }

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
          IconButton(
            onClick = { viewModel.startCompareWithSimilar(product) },
            modifier = Modifier.testTag("detail_compare_button")
          ) {
            Icon(Icons.Default.CompareArrows, contentDescription = "Compare", tint = FlipkartBlue)
          }

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
              tint = if (isWishlisted) Color.Red else Color.DarkGray
            )
          }
        }
      }

      // 2. Product Gallery with Badge
      Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(340.dp)
          ) {
            val displayImages = if (product.imageUrls.isNotEmpty()) product.imageUrls else listOf("https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80")
            val activeImageUrl = displayImages.getOrElse(currentImageIndex) { displayImages.first() }

            AsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data(activeImageUrl)
                .crossfade(true)
                .build(),
              contentDescription = product.title,
              contentScale = ContentScale.Fit,
              modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
            )

            // Deal Tag Badge
            Box(
              modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart)
                .background(Color(0xFFE53935), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = product.badgeTag,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          // Image Thumbnails row
          if (product.imageUrls.size > 1) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
              horizontalArrangement = Arrangement.Center
            ) {
              product.imageUrls.forEachIndexed { index, url ->
                Box(
                  modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(50.dp)
                    .border(
                      width = if (currentImageIndex == index) 2.dp else 1.dp,
                      color = if (currentImageIndex == index) FlipkartBlue else Color.LightGray,
                      shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { currentImageIndex = index }
                ) {
                  AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                      .data(url)
                      .crossfade(true)
                      .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                      .fillMaxSize()
                      .clip(RoundedCornerShape(6.dp))
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 3. Product Title, Rating & Pricing
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
              text = "$totalRatingsCount Ratings & $totalReviewsCount Reviews",
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
                text = "✓ Assured",
                color = FlipkartBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Price Row
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "₹${currentPrice.toInt()}",
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
              text = "₹${currentMrp.toInt()}",
              fontSize = 14.sp,
              color = Color.Gray,
              textDecoration = TextDecoration.LineThrough
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
              text = "${product.discountPercent}% off",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = DiscountGreen
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Bachat: You save ₹${savings.toInt()} on this product!",
            fontSize = 12.sp,
            color = DiscountGreen,
            fontWeight = FontWeight.Medium
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedButton(
            onClick = { viewModel.startCompareWithSimilar(product) },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = FlipkartBlue),
            border = androidx.compose.foundation.BorderStroke(1.dp, FlipkartBlue),
            modifier = Modifier
              .fillMaxWidth()
              .height(38.dp)
              .testTag("compare_product_button")
          ) {
            Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp), tint = FlipkartBlue)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Compare with Similar Products ⚖️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
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
          Text("Quantity:", fontWeight = FontWeight.Bold, fontSize = 14.sp)

          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .background(Color(0xFFEEEEEE), RoundedCornerShape(4.dp))
                .clickable { if (quantity > 1) quantity-- },
              contentAlignment = Alignment.Center
            ) {
              Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Text(
              text = "$quantity",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 16.dp)
            )

            Box(
              modifier = Modifier
                .size(34.dp)
                .background(Color(0xFFEEEEEE), RoundedCornerShape(4.dp))
                .clickable { if (quantity < 10) quantity++ },
              contentAlignment = Alignment.Center
            ) {
              Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 6. Available Offers & Delivery PIN Checker
      Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = DiscountGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Available Offers:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text("• Use code WELCOME100 for Flat ₹100 Off on orders above ₹499", fontSize = 12.sp, color = Color.DarkGray)
          Text("• Use DIWALI20 for 20% instant festive discount", fontSize = 12.sp, color = Color.DarkGray)
          Text("• Free Delivery on orders over ₹499 across all Indian PIN codes", fontSize = 12.sp, color = Color.DarkGray)

          Spacer(modifier = Modifier.height(12.dp))

          Text("Check Delivery & Cash on Delivery:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = pinInput,
              onValueChange = { if (it.length <= 6) pinInput = it },
              placeholder = { Text("Enter 6-digit Pincode", fontSize = 12.sp) },
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("detail_pincode_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
              onClick = { viewModel.checkPincode(pinInput) },
              colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier
                .height(48.dp)
                .testTag("detail_check_pincode_button")
            ) {
              Text("Check", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

      // 8. RATINGS & REVIEWS SUMMARY AND SUBMISSION SYSTEM
      Surface(
        color = Color.White,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ratings_and_reviews_section")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Section Title & "Rate Product" button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Ratings & Reviews",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF212121)
              )
              Text(
                text = "$totalRatingsCount ratings • $totalReviewsCount verified reviews",
                fontSize = 11.sp,
                color = Color.Gray
              )
            }

            OutlinedButton(
              onClick = { showWriteReviewDialog = true },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = FlipkartBlue),
              modifier = Modifier.testTag("write_review_button")
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Rate Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // SUMMARY DISPLAY CARD: Big Average Score + Star Distribution Bars
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Left: Big Rating Badge
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "${product.rating}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF212121)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(26.dp)
                  )
                }

                Text(
                  text = "$totalRatingsCount Ratings",
                  fontSize = 11.sp,
                  color = Color.Gray,
                  fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                  modifier = Modifier
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "94% Satisfied",
                    fontSize = 10.sp,
                    color = DiscountGreen,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              // Divider
              Box(
                modifier = Modifier
                  .width(1.dp)
                  .height(90.dp)
                  .background(Color(0xFFE0E0E0))
              )

              Spacer(modifier = Modifier.width(16.dp))

              // Right: Rating Progress Distribution Bars
              Column(modifier = Modifier.weight(1f)) {
                listOf(
                  Triple("5 ★", fiveStarPct, DiscountGreen),
                  Triple("4 ★", fourStarPct, Color(0xFF66BB6A)),
                  Triple("3 ★", threeStarPct, Color(0xFFFFA726)),
                  Triple("2 ★", twoStarPct, Color(0xFFFF7043)),
                  Triple("1 ★", oneStarPct, Color(0xFFEF5350))
                ).forEach { (starLabel, pct, color) ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = starLabel,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.DarkGray,
                      modifier = Modifier.width(24.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    LinearProgressIndicator(
                      progress = { pct },
                      modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                      color = color,
                      trackColor = Color(0xFFE0E0E0),
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                      text = "${(pct * 100).toInt()}%",
                      fontSize = 10.sp,
                      color = Color.Gray,
                      modifier = Modifier.width(26.dp)
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Feature satisfaction highlights
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            listOf(
              "Quality" to "4.6 ★",
              "Value" to "4.5 ★",
              "Delivery" to "4.7 ★"
            ).forEach { (feature, score) ->
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(score, fontWeight = FontWeight.Black, fontSize = 12.sp, color = DiscountGreen)
                Text(feature, fontSize = 11.sp, color = Color.Gray)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          HorizontalDivider(color = Color(0xFFEEEEEE))
          Spacer(modifier = Modifier.height(12.dp))

          // Review Filter Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("All", "5 ★", "4 ★", "3 ★ & Below", "Verified").forEach { filterTag ->
              FilterChip(
                selected = selectedReviewFilter == filterTag,
                onClick = { selectedReviewFilter = filterTag },
                label = { Text(filterTag, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = FlipkartBlue,
                  selectedLabelColor = Color.White
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Customer Reviews List
          if (filteredReviews.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "No reviews found for this filter.",
                  fontSize = 13.sp,
                  color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = { showWriteReviewDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = DealAmber),
                  modifier = Modifier.height(36.dp)
                ) {
                  Text("Write the First Review", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          } else {
            filteredReviews.forEach { review ->
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 8.dp)
              ) {
                // Rating badge & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .background(
                        if (review.rating >= 4) DiscountGreen else if (review.rating == 3) Color(0xFFF57F17) else Color(0xFFD32F2F),
                        RoundedCornerShape(4.dp)
                      )
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "${review.rating}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                      Spacer(modifier = Modifier.width(2.dp))
                      Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                    }
                  }

                  Spacer(modifier = Modifier.width(8.dp))

                  Text(
                    text = review.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF212121)
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = review.comment,
                  fontSize = 12.sp,
                  color = Color(0xFF424242),
                  lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Reviewer Info & Helpful Button
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = review.userName,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Medium,
                      color = Color.Gray
                    )

                    if (review.isVerifiedPurchase) {
                      Spacer(modifier = Modifier.width(6.dp))
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Buyer",
                        tint = DiscountGreen,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(2.dp))
                      Text(
                        text = "Verified Buyer",
                        fontSize = 10.sp,
                        color = DiscountGreen,
                        fontWeight = FontWeight.Bold
                      )
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Text("• ${review.createdAt}", fontSize = 10.sp, color = Color.LightGray)
                  }

                  // Helpful Button
                  val likes = helpfulLikes[review.id] ?: 12
                  Row(
                    modifier = Modifier
                      .clickable {
                        helpfulLikes[review.id] = likes + 1
                        viewModel.showToast("Thank you for your feedback!")
                      }
                      .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.ThumbUp,
                      contentDescription = "Helpful",
                      tint = Color.Gray,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "$likes",
                      fontSize = 11.sp,
                      color = Color.Gray
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFF1F1F1))
              }
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

    // Write Review Dialog
    if (showWriteReviewDialog) {
      WriteReviewDialog(
        product = product,
        onDismiss = { showWriteReviewDialog = false },
        onSubmitReview = { rating, title, comment, name ->
          viewModel.addReview(
            productId = product.id,
            rating = rating,
            title = title,
            comment = comment,
            reviewerName = name
          )
          showWriteReviewDialog = false
        }
      )
    }
  }
}
