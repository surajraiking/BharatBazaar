package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppScreen
import com.example.ui.components.BottomNavBar
import com.example.ui.components.ChangePincodeDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LegalDialog
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProductListScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ECommerceViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        ECommerceApp()
      }
    }
  }
}

@Composable
fun ECommerceApp(
  viewModel: ECommerceViewModel = viewModel()
) {
  val screen by viewModel.screen.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val wishlistIds by viewModel.wishlistIds.collectAsState()
  val pincode by viewModel.pincode.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val toastMessage by viewModel.toastMessage.collectAsState()

  var showPincodeDialog by remember { mutableStateOf(false) }
  var legalDialogTopic by remember { mutableStateOf<String?>(null) }

  // Auto clear toast after 2.5 seconds
  LaunchedEffect(toastMessage) {
    if (toastMessage != null) {
      delay(2500)
      viewModel.clearToast()
    }
  }

  // Handle hardware & gesture back button
  BackHandler(enabled = screen != AppScreen.HOME) {
    viewModel.navigateBack()
  }

  val isBottomBarVisible = screen != AppScreen.CHECKOUT && screen != AppScreen.ADMIN
  val isTopHeaderVisible = screen == AppScreen.HOME || screen == AppScreen.PRODUCT_LIST

  Scaffold(
    topBar = {
      if (isTopHeaderVisible) {
        TopHeaderBar(
          searchQuery = searchQuery,
          onSearchChange = { viewModel.setSearchQuery(it) },
          onSearchSubmit = { viewModel.navigateTo(AppScreen.PRODUCT_LIST) },
          cartCount = cartItems.sumOf { it.quantity },
          wishlistCount = wishlistIds.size,
          pincode = pincode,
          onChangePincodeClick = { showPincodeDialog = true },
          onCartClick = { viewModel.navigateTo(AppScreen.CART) },
          onWishlistClick = { viewModel.navigateTo(AppScreen.WISHLIST) }
        )
      }
    },
    bottomBar = {
      if (isBottomBarVisible) {
        BottomNavBar(
          currentScreen = screen,
          cartCount = cartItems.sumOf { it.quantity },
          onScreenSelected = { viewModel.navigateTo(it) }
        )
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (screen) {
        AppScreen.HOME -> HomeScreen(viewModel = viewModel)
        AppScreen.PRODUCT_LIST -> ProductListScreen(viewModel = viewModel)
        AppScreen.PRODUCT_DETAIL -> ProductDetailScreen(viewModel = viewModel)
        AppScreen.CART -> CartScreen(
          viewModel = viewModel,
          onChangeAddressClick = { showPincodeDialog = true }
        )
        AppScreen.CHECKOUT -> CheckoutScreen(viewModel = viewModel)
        AppScreen.ORDERS -> OrdersScreen(viewModel = viewModel)
        AppScreen.WISHLIST -> WishlistScreen(viewModel = viewModel)
        AppScreen.ACCOUNT -> AccountScreen(
          viewModel = viewModel,
          onLegalClick = { legalDialogTopic = it }
        )
        AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
      }

      // In-app Floating Toast Notification
      AnimatedVisibility(
        visible = toastMessage != null,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp, start = 16.dp, end = 16.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = Color(0xFF263238),
          shadowElevation = 6.dp,
          modifier = Modifier.testTag("app_toast_message")
        ) {
          Text(
            text = toastMessage ?: "",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
          )
        }
      }
    }
  }

  // Pincode Selector Modal
  if (showPincodeDialog) {
    ChangePincodeDialog(
      currentPincode = pincode,
      onDismiss = { showPincodeDialog = false },
      onPincodeSubmit = { newPin ->
        viewModel.checkPincode(newPin)
        showPincodeDialog = false
      }
    )
  }

  // Legal Pages Dialog
  if (legalDialogTopic != null) {
    LegalDialog(
      topic = legalDialogTopic!!,
      onDismiss = { legalDialogTopic = null }
    )
  }
}
