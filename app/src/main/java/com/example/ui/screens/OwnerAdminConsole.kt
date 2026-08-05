package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerAdminConsole(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val allCommissions by viewModel.allCommissions.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val products by viewModel.products.collectAsState()
    val kycSubmissions by viewModel.allKycSubmissions.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Overview, 1: Products, 2: KYC Review, 3: RBAC Users

    // Dialog state for adding/editing products
    var showAddProductDialog by remember { mutableStateOf(false) }
    var prodName by remember { mutableStateOf("") }
    var prodPrice by remember { mutableStateOf("") }
    var prodBv by remember { mutableStateOf("") }
    var prodCategory by remember { mutableStateOf(ProductCategory.DIGITAL) }

    val user = activeUser ?: return

    val totalGrossSales = allOrders.sumOf { it.totalAmount }
    val totalCommissionsPaid = allCommissions.sumOf { it.commissionAmount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Owner & Admin Control Console", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
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
                .padding(16.dp)
        ) {
            // Navigation Tab Bar
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = DarkSlate,
                contentColor = AccentGold,
                edgePadding = 8.dp
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("System Overview", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("Products Inventory (${products.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("KYC Queue (${kycSubmissions.count { it.status == KycStatus.PENDING }})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                    Text("RBAC Users (${allUsers.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (activeTab) {
                0 -> {
                    // System Overview Tab
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSlate),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AccentGold, RoyalBlue)))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Global Platform Metrics", fontSize = 12.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Gross Product Sales", fontSize = 11.sp, color = TextSecondaryDark)
                                            Text("$${String.format("%.2f", totalGrossSales)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                        }
                                        Column {
                                            Text("Commissions Disbursed", fontSize = 11.sp, color = TextSecondaryDark)
                                            Text("$${String.format("%.2f", totalCommissionsPaid)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text("Recent Global Orders", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        }

                        items(allOrders) { order ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("${order.orderNumber} • ${order.buyerName}", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                        Text("Total: $${order.totalAmount} • ${order.totalBv} BV", fontSize = 11.sp, color = TextSecondaryDark)
                                    }
                                    Text(order.status.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Products Inventory Management Tab
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = { showAddProductDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New Product / Kit SKU", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(products) { prod ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(prod.name, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                            Text("SKU: ${prod.sku} • Price: $${prod.price} • BV: ${prod.bvWeight}", fontSize = 11.sp, color = TextSecondaryDark)
                                        }

                                        IconButton(onClick = { viewModel.deleteProduct(prod.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonError)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // KYC Review Queue Tab
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (kycSubmissions.isEmpty()) {
                            item {
                                Text("No KYC submissions in queue.", color = TextSecondaryDark)
                            }
                        } else {
                            items(kycSubmissions) { kyc ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(kyc.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryDark)
                                        Text("ID Number: ${kyc.idNumber} • Type: ${kyc.documentType}", fontSize = 12.sp, color = TextSecondaryDark)
                                        Text("Status: ${kyc.status.name}", fontSize = 12.sp, color = AccentGold)

                                        if (kyc.status == KycStatus.PENDING) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = { viewModel.reviewKyc(kyc.userId, true, "Approved by Admin") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                                                ) {
                                                    Text("Approve")
                                                }
                                                Button(
                                                    onClick = { viewModel.reviewKyc(kyc.userId, false, "Document unreadable") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonError)
                                                ) {
                                                    Text("Reject")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // RBAC User Management Tab
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(allUsers) { usr ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(usr.fullName, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                        Text("Role: ${usr.role.name} • Rank: ${usr.rank.title}", fontSize = 12.sp, color = TextSecondaryDark)
                                        Text("Wallet: $${usr.walletBalance} • TV: ${usr.teamVolume} BV", fontSize = 11.sp, color = AccentGold)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = RoyalBlue.copy(alpha = 0.2f)
                                    ) {
                                        Text(usr.role.name, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Add Product / Kit", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                Column {
                    OutlinedTextField(value = prodName, onValueChange = { prodName = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = prodPrice, onValueChange = { prodPrice = it }, label = { Text("Price ($)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = prodBv, onValueChange = { prodBv = it }, label = { Text("BV Weight") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = prodPrice.toDoubleOrNull() ?: 0.0
                        val bv = prodBv.toDoubleOrNull() ?: 0.0
                        if (prodName.isNotBlank() && price > 0) {
                            val newProd = ProductEntity(
                                id = "prod_" + UUID.randomUUID().toString().take(6),
                                sku = "OMNI-NEW-" + (100..999).random(),
                                name = prodName,
                                description = "New catalog item added by Owner console.",
                                price = price,
                                bvWeight = bv,
                                pvWeight = bv,
                                category = ProductCategory.DIGITAL,
                                stockQuantity = 500,
                                isFeatured = true
                            )
                            viewModel.addOrUpdateProduct(newProd)
                            showAddProductDialog = false
                            prodName = ""
                            prodPrice = ""
                            prodBv = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep)
                ) {
                    Text("Save Product", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) { Text("Cancel") }
            },
            containerColor = CardBackgroundDark
        )
    }
}
