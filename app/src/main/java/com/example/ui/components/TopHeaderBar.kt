package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartDarkBlue
import com.example.ui.theme.FlipkartYellow

@Composable
fun TopHeaderBar(
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  onSearchSubmit: () -> Unit,
  cartCount: Int,
  wishlistCount: Int,
  pincode: String,
  onChangePincodeClick: () -> Unit,
  onCartClick: () -> Unit,
  onWishlistClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(FlipkartBlue)
      .statusBarsPadding()
      .testTag("top_header_bar")
  ) {
    // Brand row + Wishlist & Cart Icons
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Logo
      Row(verticalAlignment = Alignment.CenterVertically) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Bharat",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )
            Text(
              text = "Bazaar",
              color = FlipkartYellow,
              fontWeight = FontWeight.Black,
              fontSize = 20.sp
            )
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Explore",
              color = Color.White.copy(alpha = 0.8f),
              fontSize = 10.sp,
              fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Plus ✦",
              color = FlipkartYellow,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              fontStyle = FontStyle.Italic
            )
          }
        }
      }

      // Actions
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onWishlistClick,
          modifier = Modifier.testTag("wishlist_button")
        ) {
          BadgedBox(
            badge = {
              if (wishlistCount > 0) {
                Badge(
                  containerColor = FlipkartYellow,
                  contentColor = Color.Black
                ) {
                  Text(wishlistCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          ) {
            Icon(
              imageVector = if (wishlistCount > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Wishlist",
              tint = if (wishlistCount > 0) Color(0xFFFF5252) else Color.White
            )
          }
        }

        IconButton(
          onClick = onCartClick,
          modifier = Modifier.testTag("cart_button")
        ) {
          BadgedBox(
            badge = {
              if (cartCount > 0) {
                Badge(
                  containerColor = FlipkartYellow,
                  contentColor = Color.Black
                ) {
                  Text(cartCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingCart,
              contentDescription = "Shopping Cart",
              tint = Color.White
            )
          }
        }
      }
    }

    // Search Bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = {
          Text(
            text = "Search for Sarees, Earbuds, Kurtis, Shoes...",
            fontSize = 13.sp,
            color = Color.Gray
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = FlipkartBlue
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchChange("") }) {
              Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color.Gray)
            }
          } else {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "Voice search",
              tint = FlipkartBlue,
              modifier = Modifier.padding(end = 8.dp)
            )
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White,
          focusedBorderColor = FlipkartYellow,
          unfocusedBorderColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("search_text_field")
      )
    }

    // Indian Delivery Pincode Strip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(FlipkartDarkBlue)
        .clickable { onChangePincodeClick() }
        .padding(horizontal = 16.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.LocationOn,
        contentDescription = "Pincode location",
        tint = FlipkartYellow,
        modifier = Modifier.size(15.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Deliver to - $pincode",
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "• Change",
        color = FlipkartYellow,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
