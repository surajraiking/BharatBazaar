package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Banner
import com.example.ui.theme.DealAmber
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import kotlinx.coroutines.delay

@Composable
fun BannerCarousel(
  banners: List<Banner>,
  onBannerClick: (Banner) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedIndex by remember { mutableStateOf(0) }

  // Auto-scroll loop
  LaunchedEffect(banners.size) {
    if (banners.isNotEmpty()) {
      while (true) {
        delay(4000)
        selectedIndex = (selectedIndex + 1) % banners.size
      }
    }
  }

  Column(modifier = modifier.fillMaxWidth()) {
    if (banners.isNotEmpty()) {
      val currentBanner = banners[selectedIndex]

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .clickable { onBannerClick(currentBanner) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(currentBanner.imageUrl)
              .crossfade(true)
              .build(),
            contentDescription = currentBanner.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )

          // Gradient overlay for readability
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.8f)
                  ),
                  startY = 50f
                )
              )
          )

          // Badge
          Box(
            modifier = Modifier
              .padding(12.dp)
              .align(Alignment.TopStart)
              .background(FlipkartYellow, RoundedCornerShape(4.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = currentBanner.badge,
              color = Color.Black,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Text content
          Column(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(12.dp)
          ) {
            Text(
              text = currentBanner.title,
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = currentBanner.subtitle,
              color = FlipkartYellow,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )
          }
        }
      }

      // Indicator dots
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        banners.forEachIndexed { index, _ ->
          Box(
            modifier = Modifier
              .padding(horizontal = 3.dp)
              .size(if (index == selectedIndex) 8.dp else 6.dp)
              .clip(CircleShape)
              .background(if (index == selectedIndex) FlipkartBlue else Color.LightGray)
          )
        }
      }
    }
  }
}

@Composable
fun DealsCountdownHeader(
  title: String = "Deals of the Day",
  subtitle: String = "Special 24-Hour Discounts",
  modifier: Modifier = Modifier
) {
  var secondsRemaining by remember { mutableStateOf(19420) }

  LaunchedEffect(Unit) {
    while (secondsRemaining > 0) {
      delay(1000)
      secondsRemaining--
    }
  }

  val hours = secondsRemaining / 3600
  val minutes = (secondsRemaining % 3600) / 60
  val seconds = secondsRemaining % 60

  val formattedTimer = String.format("%02dh : %02dm : %02ds", hours, minutes, seconds)

  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.White)
      .padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = title,
        color = Color(0xFF212121),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = subtitle,
        color = Color.Gray,
        fontSize = 11.sp
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Timer,
        contentDescription = "Timer",
        tint = DealAmber,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = formattedTimer,
        color = DealAmber,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
