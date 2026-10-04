package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.viewmodel.ECommerceViewModel
import com.example.viewmodel.SortOption

@Composable
fun ProductListScreen(
  viewModel: ECommerceViewModel,
  modifier: Modifier = Modifier
) {
  val products by viewModel.products.collectAsState()
  val filterState by viewModel.filterState.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val wishlistIds by viewModel.wishlistIds.collectAsState()
  val isHinglish by viewModel.isHinglish.collectAsState()

  var showSortMenu by remember { mutableStateOf(false) }

  // Filter products
  val filteredProducts = products.filter { product ->
    // Active only
    if (!product.isActive) return@filter false

    // Search query
    if (searchQuery.isNotBlank()) {
      val q = searchQuery.trim().lowercase()
      val matches = product.title.lowercase().contains(q) ||
        product.brand.lowercase().contains(q) ||
        product.description.lowercase().contains(q) ||
        product.categoryName.lowercase().contains(q)
      if (!matches) return@filter false
    }

    // Category filter
    if (filterState.categoryId != null && product.categoryId != filterState.categoryId) {
      return@filter false
    }

    // Min Rating filter
    if (filterState.minRating != null && product.rating < filterState.minRating!!) {
      return@filter false
    }

    // Price range
    if (filterState.minPrice != null && product.sellingPrice < filterState.minPrice!!) {
      return@filter false
    }
    if (filterState.maxPrice != null && product.sellingPrice > filterState.maxPrice!!) {
      return@filter false
    }

    true
  }.let { list ->
    when (filterState.sortOption) {
      SortOption.POPULARITY -> list.sortedByDescending { it.ratingCount }
      SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.sellingPrice }
      SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.sellingPrice }
      SortOption.NEWEST -> list
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .testTag("product_list_screen")
  ) {
    // Top Filter & Sort Bar
    Card(
      shape = RoundedCornerShape(0.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        // Categories Horizontal Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 4.dp)
        ) {
          item {
            FilterChip(
              selected = filterState.categoryId == null,
              onClick = { viewModel.setFilterCategory(null) },
              label = { Text("All", fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = FlipkartBlue,
                selectedLabelColor = Color.White
              )
            )
          }

          items(viewModel.categories) { category ->
            FilterChip(
              selected = filterState.categoryId == category.id,
              onClick = { viewModel.setFilterCategory(category.id) },
              label = { Text(category.name.split("&").first().trim(), fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = FlipkartBlue,
                selectedLabelColor = Color.White
              )
            )
          }
        }

        // Secondary Filters: Sort, 4★+, Under ₹500
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Sort Dropdown button
          Box {
            OutlinedButton(
              onClick = { showSortMenu = true },
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Icon(Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(16.dp), tint = FlipkartBlue)
              Spacer(modifier = Modifier.width(4.dp))
              Text(filterState.sortOption.title, fontSize = 11.sp, color = FlipkartBlue, fontWeight = FontWeight.SemiBold)
            }

            DropdownMenu(
              expanded = showSortMenu,
              onDismissRequest = { showSortMenu = false }
            ) {
              SortOption.values().forEach { option ->
                DropdownMenuItem(
                  text = { Text(option.title, fontSize = 13.sp) },
                  onClick = {
                    viewModel.setSortOption(option)
                    showSortMenu = false
                  }
                )
              }
            }
          }

          // Rating Chip: 4★ & above
          FilterChip(
            selected = filterState.minRating != null,
            onClick = {
              viewModel.setRatingFilter(if (filterState.minRating == null) 4.0f else null)
            },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("4.0★+", fontSize = 11.sp)
              }
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = FlipkartBlue,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.height(34.dp)
          )

          // Budget Chip: Under ₹500
          FilterChip(
            selected = filterState.maxPrice == 500.0,
            onClick = {
              if (filterState.maxPrice == 500.0) {
                viewModel.setPriceFilter(null, null)
              } else {
                viewModel.setPriceFilter(null, 500.0)
              }
            },
            label = { Text("Under ₹500", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = FlipkartBlue,
              selectedLabelColor = Color.White
            ),
            modifier = Modifier.height(34.dp)
          )
        }

        // Search tag or active filters summary
        if (searchQuery.isNotBlank() || filterState.categoryId != null || filterState.minRating != null || filterState.maxPrice != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Showing ${filteredProducts.size} results" + if (searchQuery.isNotBlank()) " for \"$searchQuery\"" else "",
              fontSize = 11.sp,
              color = Color.DarkGray,
              fontWeight = FontWeight.Medium
            )

            Text(
              text = "Clear All",
              fontSize = 11.sp,
              color = Color.Red,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.clickable { viewModel.resetFilters() }
            )
          }
        }
      }
    }

    // Products 2-Column Grid
    if (filteredProducts.isEmpty()) {
      // Empty state
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.SearchOff,
          contentDescription = null,
          tint = Color.Gray,
          modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = if (isHinglish) "Koi product nahi mila!" else "No products found",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Try clearing filters or search for something else like Saree, Earbuds or Shoes.",
          fontSize = 12.sp,
          color = Color.Gray,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
          onClick = { viewModel.resetFilters() },
          colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue)
        ) {
          Text("Reset All Filters")
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        val pairs = filteredProducts.chunked(2)
        items(pairs) { pair ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            ProductCard(
              product = pair[0],
              isWishlisted = wishlistIds.contains(pair[0].id),
              onProductClick = { viewModel.openProductDetail(it) },
              onWishlistToggle = { viewModel.toggleWishlist(it.id) },
              onQuickAdd = { viewModel.addToCart(it) },
              modifier = Modifier.weight(1f)
            )

            if (pair.size > 1) {
              ProductCard(
                product = pair[1],
                isWishlisted = wishlistIds.contains(pair[1].id),
                onProductClick = { viewModel.openProductDetail(it) },
                onWishlistToggle = { viewModel.toggleWishlist(it.id) },
                onQuickAdd = { viewModel.addToCart(it) },
                modifier = Modifier.weight(1f)
              )
            } else {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }
  }
}
