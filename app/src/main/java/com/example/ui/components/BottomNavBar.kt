package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow

@Composable
fun BottomNavBar(
  currentScreen: AppScreen,
  cartCount: Int,
  onScreenSelected: (AppScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    containerColor = Color.White,
    contentColor = Color.DarkGray,
    modifier = modifier
      .navigationBarsPadding()
      .testTag("bottom_navigation_bar")
  ) {
    // 1. Home
    NavigationBarItem(
      selected = currentScreen == AppScreen.HOME,
      onClick = { onScreenSelected(AppScreen.HOME) },
      icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
      label = { Text("Home", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.HOME) FontWeight.Bold else FontWeight.Normal) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = FlipkartBlue,
        selectedTextColor = FlipkartBlue,
        indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
      ),
      modifier = Modifier.testTag("nav_home")
    )

    // 2. Categories
    NavigationBarItem(
      selected = currentScreen == AppScreen.PRODUCT_LIST,
      onClick = { onScreenSelected(AppScreen.PRODUCT_LIST) },
      icon = { Icon(Icons.Default.GridView, contentDescription = "Categories") },
      label = { Text("Categories", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.PRODUCT_LIST) FontWeight.Bold else FontWeight.Normal) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = FlipkartBlue,
        selectedTextColor = FlipkartBlue,
        indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
      ),
      modifier = Modifier.testTag("nav_categories")
    )

    // 3. Cart
    NavigationBarItem(
      selected = currentScreen == AppScreen.CART,
      onClick = { onScreenSelected(AppScreen.CART) },
      icon = {
        BadgedBox(
          badge = {
            if (cartCount > 0) {
              Badge(containerColor = FlipkartBlue, contentColor = Color.White) {
                Text(cartCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        ) {
          Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
        }
      },
      label = { Text("Cart", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.CART) FontWeight.Bold else FontWeight.Normal) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = FlipkartBlue,
        selectedTextColor = FlipkartBlue,
        indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
      ),
      modifier = Modifier.testTag("nav_cart")
    )

    // 4. Orders
    NavigationBarItem(
      selected = currentScreen == AppScreen.ORDERS,
      onClick = { onScreenSelected(AppScreen.ORDERS) },
      icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Orders") },
      label = { Text("Orders", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.ORDERS) FontWeight.Bold else FontWeight.Normal) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = FlipkartBlue,
        selectedTextColor = FlipkartBlue,
        indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
      ),
      modifier = Modifier.testTag("nav_orders")
    )

    // 5. Account
    NavigationBarItem(
      selected = currentScreen == AppScreen.ACCOUNT,
      onClick = { onScreenSelected(AppScreen.ACCOUNT) },
      icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
      label = { Text("Account", fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.ACCOUNT) FontWeight.Bold else FontWeight.Normal) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = FlipkartBlue,
        selectedTextColor = FlipkartBlue,
        indicatorColor = FlipkartBlue.copy(alpha = 0.12f)
      ),
      modifier = Modifier.testTag("nav_account")
    )
  }
}
