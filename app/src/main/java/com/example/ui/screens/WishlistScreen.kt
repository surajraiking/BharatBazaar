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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.AppScreen
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.viewmodel.ECommerceViewModel

@Composable
fun WishlistScreen(
  viewModel: ECommerceViewModel,
  modifier: Modifier = Modifier
) {
  val wishlistIds by viewModel.wishlistIds.collectAsState()
  val products by viewModel.products.collectAsState()
  val isHinglish by viewModel.isHinglish.collectAsState()

  val wishlistedProducts = products.filter { wishlistIds.contains(it.id) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .testTag("wishlist_screen")
  ) {
    Surface(color = Color.White, shadowElevation = 1.dp) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isHinglish) "Aapki Wishlist ❤️ (${wishlistedProducts.size})" else "My Wishlist ❤️ (${wishlistedProducts.size})",
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
      }
    }

    if (wishlistedProducts.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = if (isHinglish) "Wishlist me koi samaan nahi hai" else "Your Wishlist is Empty",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Browse products and tap the heart icon to save your favourite deals.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { viewModel.navigateTo(AppScreen.HOME) },
            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue)
          ) {
            Text("Browse Items")
          }
        }
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(wishlistedProducts) { product ->
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.openProductDetail(product) }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
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
                  .size(70.dp)
                  .clip(RoundedCornerShape(6.dp))
              )

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(product.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("₹${product.sellingPrice.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("₹${product.mrp.toInt()}", fontSize = 12.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("${product.discountPercent}% off", fontSize = 11.sp, color = DiscountGreen, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                  onClick = {
                    viewModel.addToCart(product)
                    viewModel.toggleWishlist(product.id)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = FlipkartYellow, contentColor = Color.Black),
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                  modifier = Modifier.height(30.dp)
                ) {
                  Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(12.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Move to Cart", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }

              IconButton(onClick = { viewModel.toggleWishlist(product.id) }) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = Color.Gray)
              }
            }
          }
        }
      }
    }
  }
}
