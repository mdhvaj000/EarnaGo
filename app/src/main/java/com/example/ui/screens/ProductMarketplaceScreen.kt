package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProductCategory
import com.example.data.model.ProductEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductMarketplaceScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val cartTotalAmount by viewModel.cartTotalAmount.collectAsState()
    val cartTotalBv by viewModel.cartTotalBv.collectAsState()
    val activeUser by viewModel.activeUser.collectAsState()

    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showCartDialog by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("WALLET") }

    val filteredProducts = products.filter { product ->
        (selectedCategory == null || product.category == selectedCategory) &&
                (searchQuery.isEmpty() || product.name.contains(searchQuery, ignoreCase = true) || product.sku.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Marketplace", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                },
                actions = {
                    // Floating Cart Badge
                    BadgedBox(
                        badge = {
                            if (cart.isNotEmpty()) {
                                Badge(containerColor = AccentGold) {
                                    Text(cart.sumOf { it.quantity }.toString(), color = NavyDeep, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        IconButton(onClick = { showCartDialog = true }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = TextPrimaryDark)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDeep)
            )
        },
        containerColor = NavyDeep
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search products, SKUs, kits...", color = TextSecondaryDark) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGold,
                    unfocusedBorderColor = SurfaceBorderDark,
                    focusedContainerColor = CardBackgroundDark,
                    unfocusedContainerColor = CardBackgroundDark,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All Products") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentGold,
                            selectedLabelColor = NavyDeep
                        )
                    )
                }
                items(ProductCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.name.replace("_", " ")) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentGold,
                            selectedLabelColor = NavyDeep
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product List Grid / Cards
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = { viewModel.addToCart(product) }
                    )
                }
            }
        }
    }

    // Checkout & Cart Modal Sheet
    if (showCartDialog) {
        AlertDialog(
            onDismissRequest = { showCartDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Shopping Cart", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    IconButton(onClick = { showCartDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondaryDark)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (cart.isEmpty()) {
                        Text("Your cart is empty. Add items from the catalog!", color = TextSecondaryDark)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(cart) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSlate)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                                        Text("$${item.product.price} • ${item.product.bvWeight} BV each", fontSize = 11.sp, color = TextSecondaryDark)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { viewModel.removeFromCart(item.product.id) }) {
                                            Icon(Icons.Default.Remove, contentDescription = null, tint = CrimsonError)
                                        }
                                        Text("${item.quantity}", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                        IconButton(onClick = { viewModel.addToCart(item.product) }) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = EmeraldSuccess)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Totals Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NavyDeep),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Price:", color = TextSecondaryDark)
                                    Text("$${String.format("%.2f", cartTotalAmount)}", fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Commission BV Weight:", color = TextSecondaryDark)
                                    Text("${cartTotalBv} BV", fontWeight = FontWeight.Bold, color = AccentGold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Payment Method:", fontSize = 12.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedPaymentMethod == "WALLET",
                                onClick = { selectedPaymentMethod = "WALLET" },
                                label = { Text("Internal Wallet ($${String.format("%.2f", activeUser?.walletBalance ?: 0.0)})") }
                            )
                            FilterChip(
                                selected = selectedPaymentMethod == "CARD",
                                onClick = { selectedPaymentMethod = "CARD" },
                                label = { Text("Credit Card") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (cart.isNotEmpty()) {
                    Button(
                        onClick = {
                            viewModel.placeOrder(selectedPaymentMethod)
                            showCartDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep)
                    ) {
                        Text("Confirm Order & Distribute BV", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showCartDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            },
            containerColor = CardBackgroundDark
        )
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorderDark, AccentGold.copy(alpha = 0.3f))))
    ) {
        Column {
            if (product.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = RoyalBlue.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = product.category.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        )
                    }

                    // BV Weight Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "+${product.bvWeight.toInt()} BV Weight",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Price", fontSize = 10.sp, color = TextSecondaryDark)
                        Text("$${product.price}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldSuccess)
                    }

                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
