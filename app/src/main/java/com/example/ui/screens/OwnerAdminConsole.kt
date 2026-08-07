package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    val ownerProfileState by viewModel.ownerProfile.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Overview, 1: Owner Bank & Royalty, 2: Products, 3: KYC Review, 4: RBAC Users

    // Owner Bank Data State
    val ownerProfile = ownerProfileState ?: OwnerProfileEntity()
    var ownerName by remember(ownerProfile) { mutableStateOf(ownerProfile.ownerName) }
    var bankAccName by remember(ownerProfile) { mutableStateOf(ownerProfile.bankAccountName) }
    var bankAccNo by remember(ownerProfile) { mutableStateOf(ownerProfile.bankAccountNumber) }
    var bankIfsc by remember(ownerProfile) { mutableStateOf(ownerProfile.bankIfscCode) }
    var bankName by remember(ownerProfile) { mutableStateOf(ownerProfile.bankName) }
    var upiVpa by remember(ownerProfile) { mutableStateOf(ownerProfile.upiVpa) }
    var panNumber by remember(ownerProfile) { mutableStateOf(ownerProfile.panNumber) }
    var gstinNumber by remember(ownerProfile) { mutableStateOf(ownerProfile.gstinNumber) }
    var royaltyPctText by remember(ownerProfile) { mutableStateOf(ownerProfile.platformRoyaltyPct.toString()) }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
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
                    Text("⚡ Planck AI Internet Control", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("Owner Bank & 5% Royalty", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                    Text("15 Modules Tasks (${allTasks.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 4, onClick = { activeTab = 4 }) {
                    Text("Products Inventory (${products.size})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 5, onClick = { activeTab = 5 }) {
                    Text("KYC Queue (${kycSubmissions.count { it.status == KycStatus.PENDING }})", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 6, onClick = { activeTab = 6 }) {
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
                                    Text("Global Platform & Sole Owner Royalty Metrics", fontSize = 12.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Gross Product Sales", fontSize = 11.sp, color = TextSecondaryDark)
                                            Text("₹${String.format("%.2f", totalGrossSales)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                        }
                                        Column {
                                            Text("Commissions Disbursed", fontSize = 11.sp, color = TextSecondaryDark)
                                            Text("₹${String.format("%.2f", totalCommissionsPaid)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Divider(color = SurfaceBorderDark)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text("Owner 5% Royalty Earnings", fontSize = 11.sp, color = TextSecondaryDark)
                                            Text("₹${String.format("%.2f", ownerProfile.totalRoyaltyEarnedInr)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                        }
                                        Surface(shape = RoundedCornerShape(8.dp), color = EmeraldSuccess.copy(alpha = 0.2f)) {
                                            Text("5% Auto-Credit Active", color = EmeraldSuccess, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                                        Text("Total: ₹${String.format("%.2f", order.totalAmount)} • ${order.totalBv} BV", fontSize = 11.sp, color = TextSecondaryDark)
                                    }
                                    Text(order.status.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Planck AI Live Internet Management & Control Tab
                    val isInternetConnected by viewModel.isLiveInternetConnected.collectAsState()
                    val isAutonomousActive by viewModel.isAutonomousManagementActive.collectAsState()
                    val planckLogs by viewModel.planckActivityStream.collectAsState()
                    val isScanning by viewModel.isScanningPlanck.collectAsState()
                    val lastScanResult by viewModel.lastPlanckScanResult.collectAsState()

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSlate),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldSuccess, AccentGold)))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Autonomous App AI Manager", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                            Text("Planck-Time Scale Internet Infrastructure", fontSize = 11.sp, color = AccentGold)
                                        }

                                        Surface(shape = RoundedCornerShape(8.dp), color = EmeraldSuccess.copy(alpha = 0.2f)) {
                                            Text("0.038ms Latency", color = EmeraldSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Real-Time Internet Edge Node Sync", fontSize = 13.sp, color = TextPrimaryDark)
                                        Switch(
                                            checked = isInternetConnected,
                                            onCheckedChange = { viewModel.toggleInternetConnection() }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Autonomous App Management", fontSize = 13.sp, color = TextPrimaryDark)
                                        Switch(
                                            checked = isAutonomousActive,
                                            onCheckedChange = { viewModel.toggleAutonomousManagement() }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { viewModel.runPlanckTimeAIScan() },
                                        enabled = !isScanning,
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        if (isScanning) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NavyDeep)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Scanning App Infrastructure...")
                                        } else {
                                            Icon(Icons.Default.Bolt, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Execute Instant Planck AI Optimization Scan", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        lastScanResult?.let { res ->
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Latest Scan Output (${res.scanTimestamp})", fontWeight = FontWeight.Bold, color = AccentGold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(res.summaryReport, fontSize = 12.sp, color = TextPrimaryDark, lineHeight = 18.sp)
                                    }
                                }
                            }
                        }

                        item {
                            Text("Recent Live Planck Telemetry Logs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        }

                        items(planckLogs) { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(log.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VibrantCyan)
                                        Text(log.planckTimeScale, fontSize = 10.sp, color = TextSecondaryDark)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(log.activity, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                                    Text(log.aiAutomatedDecision, fontSize = 11.sp, color = EmeraldSuccess)
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Owner Profile, Bank Account & Royalty Control Tab
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSlate),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AccentGold, RoyalBlue)))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = AccentGold, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Owner Bank Account & Royalty Settlement", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    OutlinedTextField(
                                        value = ownerName,
                                        onValueChange = { ownerName = it },
                                        label = { Text("Sole Founder / Owner Full Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = bankAccName,
                                        onValueChange = { bankAccName = it },
                                        label = { Text("Bank Account Holder Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = bankAccNo,
                                            onValueChange = { bankAccNo = it },
                                            label = { Text("Account Number") },
                                            modifier = Modifier.weight(1.2f),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                                        )
                                        OutlinedTextField(
                                            value = bankIfsc,
                                            onValueChange = { bankIfsc = it },
                                            label = { Text("IFSC Code") },
                                            modifier = Modifier.weight(0.8f),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = bankName,
                                            onValueChange = { bankName = it },
                                            label = { Text("Bank Name & Branch") },
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                                        )
                                        OutlinedTextField(
                                            value = upiVpa,
                                            onValueChange = { upiVpa = it },
                                            label = { Text("UPI VPA ID") },
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = panNumber,
                                            onValueChange = { panNumber = it },
                                            label = { Text("PAN Number") },
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                                        )
                                        OutlinedTextField(
                                            value = gstinNumber,
                                            onValueChange = { gstinNumber = it },
                                            label = { Text("GSTIN (Optional)") },
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = royaltyPctText,
                                        onValueChange = { royaltyPctText = it },
                                        label = { Text("Owner Platform Royalty Rate (%)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            val rPct = royaltyPctText.toDoubleOrNull() ?: 5.0
                                            val updatedProfile = ownerProfile.copy(
                                                ownerName = ownerName,
                                                bankAccountName = bankAccName,
                                                bankAccountNumber = bankAccNo,
                                                bankIfscCode = bankIfsc,
                                                bankName = bankName,
                                                upiVpa = upiVpa,
                                                panNumber = panNumber,
                                                gstinNumber = gstinNumber,
                                                platformRoyaltyPct = rPct
                                            )
                                            viewModel.updateOwnerProfile(updatedProfile)
                                        },
                                        modifier = Modifier.fillMaxWidth().height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Save Owner Bank & Royalty Settings", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // 15 Modules Task Manager Tab
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Text("Active Tasks across 15 Earning Modules", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        }

                        items(allTasks) { task ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(task.module.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                        Text(task.partnerName, fontSize = 11.sp, color = TextSecondaryDark)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(task.title, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
                                    Text("Reward: ₹${task.rewardAmount} (5% Royalty: ₹${task.rewardAmount * 0.05})", fontSize = 12.sp, color = EmeraldSuccess)
                                }
                            }
                        }
                    }
                }

                4 -> {
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
                                            Text("SKU: ${prod.sku} • Price: ₹${String.format("%.2f", prod.price)} • BV: ${prod.bvWeight}", fontSize = 11.sp, color = TextSecondaryDark)
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

                5 -> {
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

                6 -> {
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
                                        Text("Wallet: ₹${String.format("%.2f", usr.walletBalance)} • TV: ${usr.teamVolume} BV", fontSize = 11.sp, color = AccentGold)
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
                    OutlinedTextField(value = prodPrice, onValueChange = { prodPrice = it }, label = { Text("Price (₹ INR)") }, modifier = Modifier.fillMaxWidth())
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
