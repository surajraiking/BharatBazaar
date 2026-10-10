package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.viewmodel.ECommerceViewModel

@Composable
fun AccountScreen(
  viewModel: ECommerceViewModel,
  onLegalClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val orders by viewModel.orders.collectAsState()
  val wishlistIds by viewModel.wishlistIds.collectAsState()
  val isHinglish by viewModel.isHinglish.collectAsState()
  val isAdminMode by viewModel.isAdminMode.collectAsState()
  val loggedInPhone by viewModel.loggedInUserPhone.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .verticalScroll(rememberScrollState())
      .testTag("account_screen")
  ) {
    // 1. User Header Profile Card
    Surface(
      color = FlipkartBlue,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(56.dp)
            .background(Color.White, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = FlipkartBlue,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        if (loggedInPhone != null) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "BharatBazaar User",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Text(
              text = loggedInPhone ?: "",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
              modifier = Modifier
                .background(FlipkartYellow, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("BharatBazaar Verified Member ✦", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }
          Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
          ) {
            Text("Logout", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        } else {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Welcome Guest",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
            Text(
              text = "Sign in to access your orders & wishlist",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 12.sp
            )
          }
          Button(
            onClick = { viewModel.navigateTo(AppScreen.LOGIN) },
            colors = ButtonDefaults.buttonColors(containerColor = FlipkartYellow),
            shape = RoundedCornerShape(18.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp).testTag("account_login_button")
          ) {
            Text("Login", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Quick Action Grid (Orders, Wishlist, Coupons, Addresses)
    Card(
      shape = RoundedCornerShape(8.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
    ) {
      Column(modifier = Modifier.padding(vertical = 4.dp)) {
        AccountRowItem(
          icon = Icons.Default.LocalShipping,
          title = if (isHinglish) "Mere Orders (${orders.size})" else "My Orders (${orders.size})",
          subtitle = "Order status, tracking timeline & invoice",
          onClick = { viewModel.navigateTo(AppScreen.ORDERS) }
        )
        Divider(color = Color(0xFFEEEEEE))
        AccountRowItem(
          icon = Icons.Default.Favorite,
          title = if (isHinglish) "Wishlist (${wishlistIds.size})" else "My Wishlist (${wishlistIds.size})",
          subtitle = "Saved products and quick move to cart",
          onClick = { viewModel.navigateTo(AppScreen.WISHLIST) }
        )
        Divider(color = Color(0xFFEEEEEE))
        AccountRowItem(
          icon = Icons.Default.LocalOffer,
          title = "Coupons & Offers",
          subtitle = "WELCOME100, DIWALI20, FLIP50 and more",
          onClick = { viewModel.showToast("All coupons active! Use at checkout.") }
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 3. Admin Switch Banner
    Card(
      shape = RoundedCornerShape(8.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF512DA8), modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text("Admin Panel Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF311B92))
            Text("Manage products, orders, stock & sales", fontSize = 11.sp, color = Color.DarkGray)
          }
        }

        Button(
          onClick = { viewModel.setAdminMode(true) },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
          shape = RoundedCornerShape(6.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
          modifier = Modifier.height(34.dp)
        ) {
          Text("Open Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 4. App Preferences (Language Hinglish / English)
    Card(
      shape = RoundedCornerShape(8.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Language, contentDescription = null, tint = FlipkartBlue)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Language: ${if (isHinglish) "Hinglish (हिंदी + Eng)" else "English"}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
              Text("Toggle friendly Hinglish phrasing", fontSize = 11.sp, color = Color.Gray)
            }
          }

          Switch(
            checked = isHinglish,
            onCheckedChange = { viewModel.toggleLanguage() },
            colors = SwitchDefaults.colors(checkedThumbColor = FlipkartBlue, checkedTrackColor = FlipkartBlue.copy(alpha = 0.5f))
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 5. Legal & Policies Section (Flipkart / Meesho Compliance)
    Card(
      shape = RoundedCornerShape(8.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
    ) {
      Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("LEGAL & POLICIES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        AccountRowItem(icon = Icons.Default.SwapHoriz, title = "7-Day Return and Refund Policy", subtitle = "Instant refund to UPI / bank source", onClick = { onLegalClick("returns") })
        Divider(color = Color(0xFFEEEEEE))
        AccountRowItem(icon = Icons.Default.Policy, title = "Terms and Conditions", subtitle = "Single seller marketplace guidelines", onClick = { onLegalClick("terms") })
        Divider(color = Color(0xFFEEEEEE))
        AccountRowItem(icon = Icons.Default.Lock, title = "Privacy Policy", subtitle = "Data protection & user information", onClick = { onLegalClick("privacy") })
        Divider(color = Color(0xFFEEEEEE))
        AccountRowItem(icon = Icons.Default.Description, title = "About BharatBazaar", subtitle = "Our vision for the Indian retail revolution", onClick = { onLegalClick("about") })
        Divider(color = Color(0xFFEEEEEE))
        AccountRowItem(icon = Icons.AutoMirrored.Filled.HelpOutline, title = "24x7 Customer Support", subtitle = "support@bharatbazaar.in • 1800-200-9999", onClick = { onLegalClick("contact") })
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Footer
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("BharatBazaar Mobile App v1.0.0 (Production Ready)", fontSize = 11.sp, color = Color.Gray)
      Text("Proudly Built in India 🇮🇳", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
    }
  }
}

@Composable
private fun AccountRowItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
      Icon(icon, contentDescription = null, tint = FlipkartBlue, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(subtitle, fontSize = 11.sp, color = Color.Gray)
      }
    }

    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
  }
}
