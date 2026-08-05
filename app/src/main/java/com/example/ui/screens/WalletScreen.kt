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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KycStatus
import com.example.data.model.TransactionType
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()

    val user = activeUser ?: return

    var showDepositModal by remember { mutableStateOf(false) }
    var showWithdrawModal by remember { mutableStateOf(false) }
    var inputAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multi-Currency Ledger & Wallet", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Balance Cards Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldSuccess, AccentGold)))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Available Wallet Balance", fontSize = 12.sp, color = TextSecondaryDark)
                        Text(
                            text = "$${String.format("%.2f", user.walletBalance)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldSuccess
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showDepositModal = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Deposit", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showWithdrawModal = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Outbox, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Withdraw", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Secondary Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Pending Commissions", fontSize = 11.sp, color = TextSecondaryDark)
                            Text("$${String.format("%.2f", user.pendingCommission)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Paid Out", fontSize = 11.sp, color = TextSecondaryDark)
                            Text("$${String.format("%.2f", user.totalPaidOut)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        }
                    }
                }
            }

            // Ledger Transaction History Section Header
            item {
                Text("Ledger Audit Log", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            }

            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                    ) {
                        Text("No wallet transactions recorded yet.", modifier = Modifier.padding(16.dp), color = TextSecondaryDark, fontSize = 13.sp)
                    }
                }
            } else {
                items(transactions) { tx ->
                    val isCredit = tx.type in listOf(TransactionType.COMMISSION_CREDIT, TransactionType.REFERRAL_BONUS, TransactionType.MATCHING_BONUS, TransactionType.WALLET_DEPOSIT)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isCredit) EmeraldSuccess.copy(alpha = 0.2f) else CrimsonError.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (isCredit) EmeraldSuccess else CrimsonError,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(tx.description, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                    Text(tx.type.name.replace("_", " "), fontSize = 10.sp, color = TextSecondaryDark)
                                }
                            }

                            Text(
                                text = "${if (isCredit) "+" else "-"}$${String.format("%.2f", tx.amount)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isCredit) EmeraldSuccess else CrimsonError
                            )
                        }
                    }
                }
            }
        }
    }

    // Deposit Modal
    if (showDepositModal) {
        AlertDialog(
            onDismissRequest = { showDepositModal = false },
            title = { Text("Deposit Wallet Funds", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                Column {
                    Text("Enter deposit amount to top up your internal wallet:", fontSize = 12.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputAmount,
                        onValueChange = { inputAmount = it },
                        label = { Text("Amount ($ USD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = inputAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.depositWallet(amt)
                            inputAmount = ""
                            showDepositModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text("Confirm Deposit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositModal = false }) { Text("Cancel") }
            },
            containerColor = CardBackgroundDark
        )
    }

    // Withdraw Modal
    if (showWithdrawModal) {
        AlertDialog(
            onDismissRequest = { showWithdrawModal = false },
            title = { Text("Request Bank Withdrawal", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                Column {
                    if (user.kycStatus != KycStatus.APPROVED) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CrimsonError.copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚠️ KYC Verification required before withdrawal. Please complete KYC submission first.",
                                modifier = Modifier.padding(10.dp),
                                color = CrimsonError,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Text("Available for payout: $${String.format("%.2f", user.walletBalance)}", fontSize = 12.sp, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = inputAmount,
                            onValueChange = { inputAmount = it },
                            label = { Text("Withdrawal Amount ($ USD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (user.kycStatus == KycStatus.APPROVED) {
                    Button(
                        onClick = {
                            val amt = inputAmount.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                viewModel.requestWithdrawal(amt)
                                inputAmount = ""
                                showWithdrawModal = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep)
                    ) {
                        Text("Request Wire Payout", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawModal = false }) { Text("Cancel") }
            },
            containerColor = CardBackgroundDark
        )
    }
}
