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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.model.Product
import com.example.ui.components.BannerCarousel
import com.example.ui.components.DealsCountdownHeader
import com.example.ui.components.ProductCard
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartLightBlue
import com.example.ui.theme.FlipkartYellow
import com.example.ui.theme.MeeshoPink
import com.example.viewmodel.ECommerceViewModel

@Composable
fun HomeScreen(
  viewModel: ECommerceViewModel,
  modifier: Modifier = Modifier
) {
  val products by viewModel.products.collectAsState()
  val wishlistIds by viewModel.wishlistIds.collectAsState()
  val isHinglish by viewModel.isHinglish.collectAsState()

  val dealsOfTheDay = products.filter { it.isDealOfTheDay && it.isActive }
  val trendingProducts = products.filter { it.isTrending && it.isActive }
  val budgetBachat = products.filter { it.sellingPrice <= 500 && it.isActive }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .testTag("home_screen"),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // 1. Horizontal Categories Circular Strip
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(vertical = 12.dp)
      ) {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // "All Categories" chip
          item {
            CategoryIconItem(
              name = if (isHinglish) "All Samaan" else "All Items",
              icon = Icons.Default.Category,
              bgColor = FlipkartLightBlue,
              tintColor = FlipkartBlue,
              onClick = { viewModel.openCategory(null) }
            )
          }

          items(viewModel.categories) { category ->
            val iconColor = when (category.slug) {
              "fashion-sarees" -> MeeshoPink
              "electronics-mobiles" -> FlipkartBlue
              "home-kitchen" -> DealAmber
              "beauty-personal-care" -> Color(0xFF9C27B0)
              else -> Color(0xFF009688)
            }

            CategoryIconItem(
              name = category.name.split("&").first().trim(),
              icon = Icons.Default.Bolt,
              bgColor = iconColor.copy(alpha = 0.12f),
              tintColor = iconColor,
              onClick = { viewModel.openCategory(category.id) }
            )
          }
        }
      }
    }

    // 2. Banner Carousel
    item {
      BannerCarousel(
        banners = viewModel.banners,
        onBannerClick = { banner ->
          viewModel.openCategory(null)
        },
        modifier = Modifier.padding(top = 4.dp)
      )
    }

    // 3. Indian Trust Guarantee Badges (Flipkart / Meesho style)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .background(Color.White, RoundedCornerShape(8.dp))
          .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TrustPill(icon = Icons.Default.CheckCircle, title = "Lowest Prices", subtitle = "Direct Wholesale")
        TrustPill(icon = Icons.Default.LocalShipping, title = "Cash on Delivery", subtitle = "Pay on Delivery")
        TrustPill(icon = Icons.Default.SwapHoriz, title = "Easy Returns", subtitle = "7 Days Hassle-Free")
      }
    }

    // 4. "Deals of the Day" with Countdown Timer
    item {
      Spacer(modifier = Modifier.height(6.dp))
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp)
      ) {
        Column {
          DealsCountdownHeader(
            title = if (isHinglish) "Dhamaka Deals of the Day ⚡" else "Deals of the Day ⚡",
            subtitle = if (isHinglish) "Sabse sasta, sabse accha" else "Limited time festive offers"
          )

          LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(dealsOfTheDay) { product ->
              ProductCard(
                product = product,
                isWishlisted = wishlistIds.contains(product.id),
                onProductClick = { viewModel.openProductDetail(it) },
                onWishlistToggle = { viewModel.toggleWishlist(it.id) },
                onQuickAdd = { viewModel.addToCart(it) },
                modifier = Modifier.width(170.dp)
              )
            }
          }
        }
      }
    }

    // 5. Special Promotional Festive Ribbon
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp)
          .background(
            color = Color(0xFFFFECE5),
            shape = RoundedCornerShape(8.dp)
          )
          .clickable { viewModel.openCategory(null) }
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Whatshot,
            contentDescription = null,
            tint = Color(0xFFFF3D00)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = if (isHinglish) "Bachat Bazaar: Under ₹499" else "Super Saver Store: Under ₹499",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color(0xFFBF360C)
            )
            Text(
              text = "Bedsheets, T-Shirts, Earbuds & more with Free Shipping",
              fontSize = 11.sp,
              color = Color.DarkGray
            )
          }
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "View all",
          tint = Color(0xFFFF3D00),
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // 6. "Trending Now" Section
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isHinglish) "Trending Right Now 🔥" else "Trending Products 🔥",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = Color(0xFF212121)
        )
        Text(
          text = "View All",
          color = FlipkartBlue,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.clickable { viewModel.openCategory(null) }
        )
      }
    }

    // 7. Grid for Trending Items (pairs of 2 products)
    val chunkedTrending = trendingProducts.chunked(2)
    items(chunkedTrending) { pair ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 4.dp),
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

    // 8. Footer Safe Shopping Banner
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = DiscountGreen,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "100% Safe & Secure Payments",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "UPI • Credit/Debit Cards • Netbanking • Cash on Delivery",
          fontSize = 11.sp,
          color = Color.Gray
        )
      }
    }
  }
}

@Composable
private fun CategoryIconItem(
  name: String,
  icon: ImageVector,
  bgColor: Color,
  tintColor: Color,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable { onClick() }
      .width(68.dp)
  ) {
    Box(
      modifier = Modifier
        .size(54.dp)
        .background(bgColor, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = name,
        tint = tintColor,
        modifier = Modifier.size(26.dp)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = name,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      color = Color(0xFF333333)
    )
  }
}

@Composable
private fun TrustPill(icon: ImageVector, title: String, subtitle: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = FlipkartBlue,
      modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Column {
      Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold)
      Text(subtitle, fontSize = 9.sp, color = Color.Gray)
    }
  }
}
