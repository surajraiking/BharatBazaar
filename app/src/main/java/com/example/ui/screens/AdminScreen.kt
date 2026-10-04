package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.ui.theme.DealAmber
import com.example.ui.theme.DiscountGreen
import com.example.ui.theme.FlipkartBlue
import com.example.ui.theme.FlipkartYellow
import com.example.viewmodel.ECommerceViewModel

@Composable
fun AdminScreen(
  viewModel: ECommerceViewModel,
  modifier: Modifier = Modifier
) {
  val products by viewModel.products.collectAsState()
  val orders by viewModel.orders.collectAsState()
  val coupons by viewModel.coupons.collectAsState()

  var selectedTab by remember { mutableStateOf(0) }
  val adminTabs = listOf("Dashboard", "Products", "Orders", "Coupons", "Customers")

  var showAddProductDialog by remember { mutableStateOf(false) }
  var shippingLabelOrder by remember { mutableStateOf<Order?>(null) }
  var productToEdit by remember { mutableStateOf<Product?>(null) }

  val totalRevenue = orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.totalAmount }
  val lowStockCount = products.count { it.stockQuantity < 50 }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1F2F4))
      .testTag("admin_screen")
  ) {
    // Top Admin Bar
    Surface(
      color = Color(0xFF263238),
      shadowElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.setAdminMode(false) }) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Admin", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
              Text("Admin Command Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("BharatBazaar Seller Portal", color = FlipkartYellow, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
          }

          Button(
            onClick = { viewModel.setAdminMode(false) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Text("Switch to Buyer", fontSize = 11.sp, color = Color.White)
          }
        }

        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color(0xFF263238),
          contentColor = FlipkartYellow,
          edgePadding = 12.dp,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = FlipkartYellow
            )
          }
        ) {
          adminTabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  title,
                  fontSize = 12.sp,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == index) FlipkartYellow else Color.White.copy(alpha = 0.7f)
                )
              }
            )
          }
        }
      }
    }

    when (selectedTab) {
      0 -> AdminDashboardTab(
        totalRevenue = totalRevenue,
        totalOrders = orders.size,
        lowStock = lowStockCount,
        totalProducts = products.size,
        onNavigateTab = { selectedTab = it }
      )
      1 -> AdminProductsTab(
        products = products,
        onAddClick = { showAddProductDialog = true },
        onToggleActive = { viewModel.toggleProductActive(it.id) },
        onEditClick = { productToEdit = it }
      )
      2 -> AdminOrdersTab(
        orders = orders,
        onUpdateStatus = { id, status -> viewModel.updateOrderStatus(id, status) },
        onPrintLabel = { shippingLabelOrder = it }
      )
      3 -> AdminCouponsTab(
        coupons = coupons,
        onAddCoupon = { viewModel.showToast("Coupon added") }
      )
      4 -> AdminCustomersTab()
    }

    // Add Product Modal
    if (showAddProductDialog) {
      AddProductDialog(
        categories = viewModel.categories,
        onDismiss = { showAddProductDialog = false },
        onSubmit = { title, catId, brand, desc, mrp, price, stock, url ->
          viewModel.addNewProduct(title, catId, brand, desc, mrp, price, stock, url)
          showAddProductDialog = false
        }
      )
    }

    // Quick Edit Product Price & Stock Modal
    if (productToEdit != null) {
      EditProductDialog(
        product = productToEdit!!,
        onDismiss = { productToEdit = null },
        onSave = { newPrice, newStock ->
          viewModel.updateProductPriceAndStock(productToEdit!!.id, newPrice, newStock)
          productToEdit = null
        }
      )
    }

    // Shipping Label Print Preview
    if (shippingLabelOrder != null) {
      ShippingLabelDialog(
        order = shippingLabelOrder!!,
        onDismiss = { shippingLabelOrder = null }
      )
    }
  }
}

@Composable
private fun AdminDashboardTab(
  totalRevenue: Double,
  totalOrders: Int,
  lowStock: Int,
  totalProducts: Int,
  onNavigateTab: (Int) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(14.dp)
  ) {
    Text("Business Overview", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF263238))
    Spacer(modifier = Modifier.height(10.dp))

    // 4 Key Metric Cards
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      MetricCard(
        title = "Total Sales",
        value = "₹${totalRevenue.toInt()}",
        icon = Icons.Default.TrendingUp,
        color = DiscountGreen,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Total Orders",
        value = "$totalOrders",
        icon = Icons.Default.LocalShipping,
        color = FlipkartBlue,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      MetricCard(
        title = "Low Stock Alert",
        value = "$lowStock items",
        icon = Icons.Default.Warning,
        color = Color.Red,
        modifier = Modifier.weight(1f)
      )
      MetricCard(
        title = "Catalog Items",
        value = "$totalProducts",
        icon = Icons.Default.Inventory,
        color = DealAmber,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Visual Sales Graph representation
    Card(
      shape = RoundedCornerShape(10.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Sales Performance (Last 5 Days)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text("₹ In Thousands", fontSize = 11.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simple Pure Jetpack Compose Bar Chart
        val days = listOf("Mon" to 12, "Tue" to 18, "Wed" to 14, "Thu" to 28, "Today" to 34)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.Bottom
        ) {
          days.forEach { (day, amount) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("₹${amount}k", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FlipkartBlue)
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .width(28.dp)
                  .height((amount * 2.8).dp)
                  .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                  .background(if (day == "Today") FlipkartBlue else Color(0xFF90CAF9))
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(day, fontSize = 11.sp, color = Color.Gray)
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Quick Action Buttons
    Text("Quick Operations", fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      Button(
        onClick = { onNavigateTab(1) },
        colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Manage Products", fontSize = 11.sp)
      }
      Button(
        onClick = { onNavigateTab(2) },
        colors = ButtonDefaults.buttonColors(containerColor = DealAmber),
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("View Orders", fontSize = 11.sp)
      }
    }
  }
}

@Composable
private fun MetricCard(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
        Box(
          modifier = Modifier
            .size(30.dp)
            .background(color.copy(alpha = 0.12f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        }
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF212121))
    }
  }
}

@Composable
private fun AdminProductsTab(
  products: List<Product>,
  onAddClick: () -> Unit,
  onToggleActive: (Product) -> Unit,
  onEditClick: (Product) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Catalog (${products.size} Products)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
        modifier = Modifier.height(34.dp).testTag("admin_add_product_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Add Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(products) { product ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AsyncImage(
              model = ImageRequest.Builder(LocalContext.current)
                .data(product.imageUrls.firstOrNull())
                .crossfade(true)
                .build(),
              contentDescription = product.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.size(60.dp).clip(RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(product.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
              Text("₹${product.sellingPrice.toInt()} (MRP ₹${product.mrp.toInt()})", fontSize = 11.sp, color = FlipkartBlue, fontWeight = FontWeight.Bold)
              Text("Stock: ${product.stockQuantity} units • ${product.categoryName}", fontSize = 10.sp, color = if (product.stockQuantity < 50) Color.Red else Color.Gray)
            }

            IconButton(onClick = { onEditClick(product) }) {
              Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.DarkGray, modifier = Modifier.size(18.dp))
            }

            Switch(
              checked = product.isActive,
              onCheckedChange = { onToggleActive(product) },
              colors = SwitchDefaults.colors(checkedThumbColor = DiscountGreen, checkedTrackColor = DiscountGreen.copy(alpha = 0.4f))
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AdminOrdersTab(
  orders: List<Order>,
  onUpdateStatus: (String, OrderStatus) -> Unit,
  onPrintLabel: (Order) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
    Text("Customer Orders (${orders.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(orders) { order ->
        var showStatusMenu by remember { mutableStateOf(false) }

        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = FlipkartBlue)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Customer: ${order.deliveryAddress.fullName} (${order.deliveryAddress.phone})", fontSize = 11.sp, color = Color.DarkGray)
            Text("Address: ${order.deliveryAddress.addressLine1}, ${order.deliveryAddress.city} - ${order.deliveryAddress.pincode}", fontSize = 10.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Status Changer Dropdown
              Box {
                OutlinedButton(
                  onClick = { showStatusMenu = true },
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                  modifier = Modifier.height(32.dp)
                ) {
                  Text("Status: ${order.status.label}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                DropdownMenu(expanded = showStatusMenu, onDismissRequest = { showStatusMenu = false }) {
                  OrderStatus.values().forEach { st ->
                    DropdownMenuItem(
                      text = { Text(st.label, fontSize = 12.sp) },
                      onClick = {
                        onUpdateStatus(order.id, st)
                        showStatusMenu = false
                      }
                    )
                  }
                }
              }

              // Print Shipping Label
              Button(
                onClick = { onPrintLabel(order) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(32.dp)
              ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Label", fontSize = 11.sp)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AdminCouponsTab(
  coupons: List<com.example.model.Coupon>,
  onAddCoupon: () -> Unit
) {
  Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
    Text("Active Promo & Discount Coupons", fontWeight = FontWeight.Bold, fontSize = 15.sp)
    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(coupons) { coupon ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(coupon.code, fontWeight = FontWeight.Black, fontSize = 13.sp, color = DiscountGreen)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Min Order: ₹${coupon.minOrderAmount.toInt()}", fontSize = 11.sp, color = Color.Gray)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(coupon.description, fontSize = 11.sp, color = Color.DarkGray)
            }
            Text("Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DiscountGreen)
          }
        }
      }
    }
  }
}

@Composable
private fun AdminCustomersTab() {
  val sampleCustomers = listOf(
    Triple("Rahul Sharma", "rahul.sharma@gmail.com", "9876543210"),
    Triple("Priya Sharma", "priya.s@yahoo.com", "9811223344"),
    Triple("Anjali Gupta", "anjali.gupta@gmail.com", "9988776655"),
    Triple("Rahul Verma", "rahul.verma@hotmail.com", "9123456789")
  )

  Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
    Text("Registered Customers (${sampleCustomers.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(sampleCustomers) { (name, email, phone) ->
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier.size(38.dp).background(FlipkartBlue.copy(alpha = 0.1f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(name.take(1), fontWeight = FontWeight.Bold, color = FlipkartBlue, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text("$email • $phone", fontSize = 11.sp, color = Color.Gray)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AddProductDialog(
  categories: List<com.example.model.Category>,
  onDismiss: () -> Unit,
  onSubmit: (String, String, String, String, Double, Double, Int, String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var brand by remember { mutableStateOf("") }
  var desc by remember { mutableStateOf("") }
  var mrp by remember { mutableStateOf("") }
  var price by remember { mutableStateOf("") }
  var stock by remember { mutableStateOf("100") }
  var imageUrl by remember { mutableStateOf("") }
  var selectedCatId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(12.dp)) {
      Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Add New Product", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = mrp, onValueChange = { mrp = it }, label = { Text("MRP (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
          OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Selling Price (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Initial Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL (HTTPS)") }, placeholder = { Text("https://...") }, singleLine = true, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
          Button(
            onClick = {
              val p = price.toDoubleOrNull() ?: 499.0
              val m = mrp.toDoubleOrNull() ?: (p * 1.5)
              val s = stock.toIntOrNull() ?: 50
              onSubmit(title, selectedCatId, brand, desc, m, p, s, imageUrl)
            },
            enabled = title.isNotBlank() && price.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
            modifier = Modifier.weight(1f)
          ) {
            Text("Save Product")
          }
        }
      }
    }
  }
}

@Composable
private fun EditProductDialog(
  product: Product,
  onDismiss: () -> Unit,
  onSave: (Double, Int) -> Unit
) {
  var priceInput by remember { mutableStateOf(product.sellingPrice.toInt().toString()) }
  var stockInput by remember { mutableStateOf(product.stockQuantity.toString()) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.padding(16.dp)) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text("Quick Edit: ${product.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = priceInput,
          onValueChange = { priceInput = it },
          label = { Text("Selling Price (₹)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = stockInput,
          onValueChange = { stockInput = it },
          label = { Text("Stock Quantity") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
          Button(
            onClick = {
              val p = priceInput.toDoubleOrNull() ?: product.sellingPrice
              val s = stockInput.toIntOrNull() ?: product.stockQuantity
              onSave(p, s)
            },
            colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
            modifier = Modifier.weight(1f)
          ) {
            Text("Update")
          }
        }
      }
    }
  }
}

@Composable
private fun ShippingLabelDialog(
  order: Order,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color.White,
      modifier = Modifier.fillMaxWidth().padding(12.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
          Text("SHIPPING LABEL", fontWeight = FontWeight.Black, fontSize = 15.sp)
          IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
            .padding(12.dp)
        ) {
          Column {
            Text("LOGISTICS: ${order.courierPartner.uppercase()}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("TRACKING AWB: ${order.trackingId}", fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 1.sp)
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)

            Text("DELIVER TO:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(order.deliveryAddress.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("${order.deliveryAddress.addressLine1}, ${order.deliveryAddress.city} - ${order.deliveryAddress.pincode}", fontSize = 12.sp)
            Text("PHONE: ${order.deliveryAddress.phone}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Black)

            Text("SHIPPER: BharatBazaar Fulfillment Hub, New Delhi 110020", fontSize = 10.sp)
            Text("PAYMENT: ${order.paymentStatus.uppercase()} • ₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Print, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Print Shipping Slip")
        }
      }
    }
  }
}
