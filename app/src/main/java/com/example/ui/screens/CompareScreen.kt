package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.AppScreen
import com.example.model.Product
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.viewmodel.ECommerceViewModel

@Composable
fun CompareScreen(
  viewModel: ECommerceViewModel,
  modifier: Modifier = Modifier
) {
  val compareList by viewModel.compareProducts.collectAsState()
  val allProducts by viewModel.products.collectAsState()
  val isHinglish by viewModel.isHinglish.collectAsState()

  var showProductPickerDialog by remember { mutableStateOf(false) }

  val product1 = compareList.getOrNull(0)
  val product2 = compareList.getOrNull(1)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .testTag("compare_screen")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Top Bar
      Surface(color = Color.White, shadowElevation = 2.dp) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { viewModel.navigateBack() },
              modifier = Modifier.testTag("compare_back_button")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
              Text(
                text = if (isHinglish) "Product Tulna (Compare) ⚖️" else "Product Comparison ⚖️",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = "Side-by-side specs to decide faster",
                fontSize = 11.sp,
                color = Color.Gray
              )
            }
          }

          Row {
            if (compareList.size == 2) {
              IconButton(
                onClick = {
                  viewModel.clearCompare()
                  viewModel.addToCompare(product2!!)
                  viewModel.addToCompare(product1!!)
                  viewModel.showToast("Columns swapped!")
                }
              ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = "Swap", tint = FlipkartBlue)
              }
            }

            if (compareList.isNotEmpty()) {
              IconButton(onClick = { viewModel.clearCompare() }) {
                Icon(Icons.Default.Delete, contentDescription = "Clear All", tint = Color.Gray)
              }
            }
          }
        }
      }

      if (product1 == null && product2 == null) {
        // Empty State
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .background(Color(0xFFE8F0FE), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.CompareArrows, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(36.dp))
              }
              Spacer(modifier = Modifier.height(16.dp))
              Text("No Products Selected to Compare", fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Select two products from the store or product details page to compare specifications, prices, and customer ratings side-by-side.",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
              Spacer(modifier = Modifier.height(18.dp))
              Button(
                onClick = {
                  if (allProducts.size >= 2) {
                    viewModel.addToCompare(allProducts[0])
                    viewModel.addToCompare(allProducts[1])
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("load_sample_comparison_button")
              ) {
                Text("Compare Popular Products")
              }
            }
          }
        }
      } else {
        // Main Comparison Scroll Content
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
        ) {
          // 2. Buyer Decision Verdict Banner (if 2 products)
          if (product1 != null && product2 != null) {
            ComparisonVerdictBanner(p1 = product1, p2 = product2)
            Spacer(modifier = Modifier.height(10.dp))
          }

          // 3. Side-by-Side Product Header Cards
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Left Column: Product 1
            Box(modifier = Modifier.weight(1f)) {
              if (product1 != null) {
                ComparedProductHeaderCard(
                  product = product1,
                  badge = "Product A",
                  onAddToCart = { viewModel.addToCart(product1) },
                  onViewDetail = { viewModel.openProductDetail(product1) },
                  onChangeProduct = { showProductPickerDialog = true },
                  onRemove = { viewModel.removeFromCompare(product1.id) }
                )
              } else {
                EmptySlotCard(
                  slotNumber = 1,
                  onSelect = { showProductPickerDialog = true }
                )
              }
            }

            // Right Column: Product 2
            Box(modifier = Modifier.weight(1f)) {
              if (product2 != null) {
                ComparedProductHeaderCard(
                  product = product2,
                  badge = "Product B",
                  onAddToCart = { viewModel.addToCart(product2) },
                  onViewDetail = { viewModel.openProductDetail(product2) },
                  onChangeProduct = { showProductPickerDialog = true },
                  onRemove = { viewModel.removeFromCompare(product2.id) }
                )
              } else {
                EmptySlotCard(
                  slotNumber = 2,
                  onSelect = { showProductPickerDialog = true }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4. Specifications Comparison Matrix Table
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("comparison_matrix_table")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "Key Specifications Comparison",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF212121)
              )
              Spacer(modifier = Modifier.height(10.dp))

              // Section A: Pricing & Savings
              SpecSectionHeader("Pricing & Savings")

              SpecComparisonRow(
                label = "Selling Price",
                val1 = product1?.let { "₹${it.sellingPrice.toInt()}" } ?: "-",
                val2 = product2?.let { "₹${it.sellingPrice.toInt()}" } ?: "-",
                val1IsWinner = product1 != null && product2 != null && product1.sellingPrice < product2.sellingPrice,
                val2IsWinner = product1 != null && product2 != null && product2.sellingPrice < product1.sellingPrice,
                winnerReason = "Lower Price"
              )

              SpecComparisonRow(
                label = "Original MRP",
                val1 = product1?.let { "₹${it.mrp.toInt()}" } ?: "-",
                val2 = product2?.let { "₹${it.mrp.toInt()}" } ?: "-"
              )

              SpecComparisonRow(
                label = "Discount",
                val1 = product1?.let { "${it.discountPercent}% Off" } ?: "-",
                val2 = product2?.let { "${it.discountPercent}% Off" } ?: "-",
                val1IsWinner = product1 != null && product2 != null && product1.discountPercent > product2.discountPercent,
                val2IsWinner = product1 != null && product2 != null && product2.discountPercent > product1.discountPercent,
                winnerReason = "Bigger Saving"
              )

              SpecComparisonRow(
                label = "Delivery Charges",
                val1 = product1?.let { if (it.sellingPrice >= 499) "FREE Delivery" else "₹40" } ?: "-",
                val2 = product2?.let { if (it.sellingPrice >= 499) "FREE Delivery" else "₹40" } ?: "-"
              )

              // Section B: Ratings & Trust
              SpecSectionHeader("Ratings & Reviews")

              SpecComparisonRow(
                label = "Customer Rating",
                val1 = product1?.let { "${it.rating} ★" } ?: "-",
                val2 = product2?.let { "${it.rating} ★" } ?: "-",
                val1IsWinner = product1 != null && product2 != null && product1.rating > product2.rating,
                val2IsWinner = product1 != null && product2 != null && product2.rating > product1.rating,
                winnerReason = "Top Rated"
              )

              SpecComparisonRow(
                label = "Total Ratings",
                val1 = product1?.let { "${it.ratingCount} Ratings" } ?: "-",
                val2 = product2?.let { "${it.ratingCount} Ratings" } ?: "-",
                val1IsWinner = product1 != null && product2 != null && product1.ratingCount > product2.ratingCount,
                val2IsWinner = product1 != null && product2 != null && product2.ratingCount > product1.ratingCount,
                winnerReason = "More Popular"
              )

              // Section C: Product Information & Quality
              SpecSectionHeader("Brand & Specifications")

              SpecComparisonRow(
                label = "Brand",
                val1 = product1?.brand ?: "-",
                val2 = product2?.brand ?: "-"
              )

              SpecComparisonRow(
                label = "Category",
                val1 = product1?.categoryName ?: "-",
                val2 = product2?.categoryName ?: "-"
              )

              SpecComparisonRow(
                label = "Description & Features",
                val1 = product1?.description ?: "-",
                val2 = product2?.description ?: "-"
              )

              SpecComparisonRow(
                label = "Available Variants",
                val1 = product1?.let { if (it.variants.isNotEmpty()) "${it.variants.size} Options" else "Standard" } ?: "-",
                val2 = product2?.let { if (it.variants.isNotEmpty()) "${it.variants.size} Options" else "Standard" } ?: "-"
              )

              SpecComparisonRow(
                label = "Stock Status",
                val1 = product1?.let { if (it.stockQuantity > 0) "In Stock (${it.stockQuantity})" else "Out of Stock" } ?: "-",
                val2 = product2?.let { if (it.stockQuantity > 0) "In Stock (${it.stockQuantity})" else "Out of Stock" } ?: "-"
              )

              // Section D: Policies & Delivery
              SpecSectionHeader("Trust & Customer Policies")

              SpecComparisonRow(
                label = "Cash on Delivery",
                val1 = "Supported ✓",
                val2 = "Supported ✓"
              )

              SpecComparisonRow(
                label = "Replacement Policy",
                val1 = "7 Days Free Replacement",
                val2 = "7 Days Free Replacement"
              )

              SpecComparisonRow(
                label = "Quality Certification",
                val1 = "100% Genuine Assured",
                val2 = "100% Genuine Assured"
              )
            }
          }

          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }

    // Product Picker Dialog
    if (showProductPickerDialog) {
      ProductPickerDialog(
        allProducts = allProducts,
        excludeId = product1?.id,
        onDismiss = { showProductPickerDialog = false },
        onSelectProduct = { selected ->
          viewModel.selectSecondProductForCompare(selected)
          showProductPickerDialog = false
        }
      )
    }
  }
}

@Composable
private fun ComparisonVerdictBanner(p1: Product, p2: Product) {
  val priceDiff = kotlin.math.abs(p1.sellingPrice - p2.sellingPrice).toInt()
  val cheaper = if (p1.sellingPrice < p2.sellingPrice) p1 else if (p2.sellingPrice < p1.sellingPrice) p2 else null
  val topRated = if (p1.rating > p2.rating) p1 else if (p2.rating > p1.rating) p2 else null

  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
    border = androidx.compose.foundation.BorderStroke(1.dp, DiscountGreen.copy(alpha = 0.4f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .background(DiscountGreen, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text("Smart Buyer Recommendation:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DiscountGreen)
        Spacer(modifier = Modifier.height(2.dp))
        if (cheaper != null && priceDiff > 0) {
          Text(
            text = "• ₹${priceDiff} Bachat on ${cheaper.brand} (${cheaper.title.take(24)}...)",
            fontSize = 11.sp,
            color = Color(0xFF1B5E20)
          )
        }
        if (topRated != null) {
          Text(
            text = "• Better Rated: ${topRated.brand} with ${topRated.rating} ★ (${topRated.ratingCount} reviews)",
            fontSize = 11.sp,
            color = Color(0xFF1B5E20)
          )
        }
      }
    }
  }
}

@Composable
private fun ComparedProductHeaderCard(
  product: Product,
  badge: String,
  onAddToCart: () -> Unit,
  onViewDetail: () -> Unit,
  onChangeProduct: () -> Unit,
  onRemove: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .background(FlipkartBlue.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FlipkartBlue)
        }

        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .clickable { onViewDetail() }
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(product.imageUrls.firstOrNull())
            .crossfade(true)
            .build(),
          contentDescription = product.title,
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = product.brand.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray
      )

      Text(
        text = product.title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 16.sp,
        modifier = Modifier.height(34.dp)
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .background(DiscountGreen, RoundedCornerShape(3.dp))
            .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${product.rating}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(2.dp))
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(9.dp))
          }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text("(${product.ratingCount})", fontSize = 10.sp, color = Color.Gray)
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("₹${product.sellingPrice.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.width(4.dp))
        Text("₹${product.mrp.toInt()}", fontSize = 11.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Button(
        onClick = onAddToCart,
        colors = ButtonDefaults.buttonColors(containerColor = DealAmber),
        shape = RoundedCornerShape(6.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(32.dp)
      ) {
        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Add to Cart", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(4.dp))

      OutlinedButton(
        onClick = onChangeProduct,
        shape = RoundedCornerShape(6.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(28.dp)
      ) {
        Text("Change Product", fontSize = 10.sp)
      }
    }
  }
}

@Composable
private fun EmptySlotCard(slotNumber: Int, onSelect: () -> Unit) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = androidx.compose.foundation.BorderStroke(1.5.dp, FlipkartBlue.copy(alpha = 0.3f)),
    modifier = Modifier
      .fillMaxWidth()
      .height(280.dp)
      .clickable { onSelect() }
      .testTag("select_second_product_button")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .background(Color(0xFFE8F0FE), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(28.dp))
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text("Product $slotNumber Slot", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      Spacer(modifier = Modifier.height(4.dp))
      Text("Tap to pick a product to compare", fontSize = 11.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
      Spacer(modifier = Modifier.height(14.dp))
      Button(
        onClick = onSelect,
        colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.height(34.dp)
      ) {
        Text("+ Select Product", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun SpecSectionHeader(title: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Text(
      text = title.uppercase(),
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF475569)
    )
  }
  Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun SpecComparisonRow(
  label: String,
  val1: String,
  val2: String,
  val1IsWinner: Boolean = false,
  val2IsWinner: Boolean = false,
  winnerReason: String = ""
) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Text(
      text = label,
      fontSize = 11.sp,
      color = Color.Gray,
      fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(2.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Val 1
      Box(
        modifier = Modifier
          .weight(1f)
          .background(if (val1IsWinner) Color(0xFFE8F5E9) else Color(0xFFF8F9FA), RoundedCornerShape(6.dp))
          .padding(8.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = val1,
              fontSize = 12.sp,
              fontWeight = if (val1IsWinner) FontWeight.Bold else FontWeight.Normal,
              color = if (val1IsWinner) DiscountGreen else Color(0xFF212121),
              maxLines = 3,
              overflow = TextOverflow.Ellipsis
            )
            if (val1IsWinner) {
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DiscountGreen, modifier = Modifier.size(13.dp))
            }
          }
          if (val1IsWinner && winnerReason.isNotBlank()) {
            Text(winnerReason, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DiscountGreen)
          }
        }
      }

      // Val 2
      Box(
        modifier = Modifier
          .weight(1f)
          .background(if (val2IsWinner) Color(0xFFE8F5E9) else Color(0xFFF8F9FA), RoundedCornerShape(6.dp))
          .padding(8.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = val2,
              fontSize = 12.sp,
              fontWeight = if (val2IsWinner) FontWeight.Bold else FontWeight.Normal,
              color = if (val2IsWinner) DiscountGreen else Color(0xFF212121),
              maxLines = 3,
              overflow = TextOverflow.Ellipsis
            )
            if (val2IsWinner) {
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DiscountGreen, modifier = Modifier.size(13.dp))
            }
          }
          if (val2IsWinner && winnerReason.isNotBlank()) {
            Text(winnerReason, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DiscountGreen)
          }
        }
      }
    }
    HorizontalDivider(modifier = Modifier.padding(top = 6.dp), color = Color(0xFFF1F5F9))
  }
}

@Composable
private fun ProductPickerDialog(
  allProducts: List<Product>,
  excludeId: String?,
  onDismiss: () -> Unit,
  onSelectProduct: (Product) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  val available = allProducts.filter { it.id != excludeId && (searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true)) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth()
        .height(500.dp)
        .testTag("product_picker_dialog")
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Select Product to Compare", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by name, brand...", fontSize = 12.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          items(available) { product ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                .clickable { onSelectProduct(product) }
                .padding(8.dp),
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
                  .size(48.dp)
                  .clip(RoundedCornerShape(6.dp))
              )

              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(product.brand.uppercase(), fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(product.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("₹${product.sellingPrice.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FlipkartBlue)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("${product.rating} ★", fontSize = 10.sp, color = DiscountGreen, fontWeight = FontWeight.Bold)
                }
              }

              Button(
                onClick = { onSelectProduct(product) },
                colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
                shape = RoundedCornerShape(6.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                modifier = Modifier.height(30.dp)
              ) {
                Text("Select", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }
  }
}
