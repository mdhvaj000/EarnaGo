package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KycStatus
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@Composable
fun MemberHomeScreen(
    viewModel: OmniViewModel,
    onNavigateTo: (String) -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    val orders by viewModel.userOrders.collectAsState()
    val commissions by viewModel.userCommissions.collectAsState()

    val user = activeUser ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. User Header & Profile Overview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSlate),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(AccentGold, RoyalBlue))
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(AccentGold, VibrantCyan))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.fullName.take(1),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDeep
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.fullName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Code: ${user.referralCode} • ${user.role.name}",
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }

                        // Rank Chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentGold.copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.linearGradient(listOf(AccentGold, AccentGoldLight))
                            )
                        ) {
                            Text(
                                text = "🏆 ${user.rank.title}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Wallet Balance Summary Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardBackgroundDark)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Available Wallet", fontSize = 12.sp, color = TextSecondaryDark)
                            Text(
                                text = "$${String.format("%.2f", user.walletBalance)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldSuccess
                            )
                        }

                        Button(
                            onClick = { onNavigateTo("wallet") },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Wallet")
                        }
                    }
                }
            }
        }

        // 2. Rank Roadmap Progress
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rank Qualification Progress", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Text("${user.rank.title} -> Director", fontSize = 12.sp, color = AccentGold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val targetBv = 10000.0
                    val progressPct = (user.teamVolume / targetBv).coerceIn(0.0, 1.0).toFloat()

                    LinearProgressIndicator(
                        progress = { progressPct },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AccentGold,
                        trackColor = SurfaceBorderDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${String.format("%.0f", user.teamVolume)} BV Accumulated", fontSize = 11.sp, color = TextSecondaryDark)
                        Text("$${targetBv.toInt()} BV Target", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                }
            }
        }

        // 3. KPI Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Performance Indicators", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Personal Volume (PV)",
                        value = "${user.personalVolume.toInt()} BV",
                        icon = Icons.Default.ShoppingBag,
                        accentColor = VibrantCyan,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Team Volume (TV)",
                        value = "${user.teamVolume.toInt()} BV",
                        icon = Icons.Default.Group,
                        accentColor = AccentGold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Direct Downlines",
                        value = "${user.directDownlineCount} Members",
                        icon = Icons.Default.AccountTree,
                        accentColor = RoyalBlue,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Total Paid Out",
                        value = "$${String.format("%.2f", user.totalPaidOut)}",
                        icon = Icons.Default.MonetizationOn,
                        accentColor = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Quick Action Launcher Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Quick Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(
                        title = "Product Market",
                        subtitle = "Shop & Earn BV",
                        icon = Icons.Default.Storefront,
                        color = AccentGold,
                        onClick = { onNavigateTo("marketplace") },
                        modifier = Modifier.weight(1f)
                    )
                    ActionTile(
                        title = "Referral Tree",
                        subtitle = "Team Network",
                        icon = Icons.Default.AccountTree,
                        color = RoyalBlue,
                        onClick = { onNavigateTo("network_tree") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(
                        title = "AI Coach",
                        subtitle = "Strategy & Posts",
                        icon = Icons.Default.Psychology,
                        color = VibrantCyan,
                        onClick = { onNavigateTo("ai_assistant") },
                        modifier = Modifier.weight(1f)
                    )
                    ActionTile(
                        title = "Commissions",
                        subtitle = "Audit Trail",
                        icon = Icons.Default.ReceiptLong,
                        color = EmeraldSuccess,
                        onClick = { onNavigateTo("commissions") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(
                        title = "KYC Status",
                        subtitle = if (user.kycStatus == KycStatus.APPROVED) "Verified" else "Review Needed",
                        icon = Icons.Default.VerifiedUser,
                        color = if (user.kycStatus == KycStatus.APPROVED) EmeraldSuccess else AmberWarning,
                        onClick = { onNavigateTo("kyc") },
                        modifier = Modifier.weight(1f)
                    )

                    if (user.role == UserRole.OWNER || user.role == UserRole.ADMIN) {
                        ActionTile(
                            title = "Owner Panel",
                            subtitle = "Admin System",
                            icon = Icons.Default.AdminPanelSettings,
                            color = CrimsonError,
                            onClick = { onNavigateTo("admin_console") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 5. Recent Activity / Orders
        item {
            Text("Recent Product Orders", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                ) {
                    Text(
                        text = "No product orders yet. Visit the Marketplace to place your first order!",
                        modifier = Modifier.padding(16.dp),
                        color = TextSecondaryDark,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(orders.take(3)) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Order #${order.orderNumber}", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text("Total: $${order.totalAmount} • ${order.totalBv} BV", fontSize = 12.sp, color = TextSecondaryDark)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldSuccess.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = order.status.name,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 11.sp, color = TextSecondaryDark)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        }
    }
}

@Composable
fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(SurfaceBorderDark, color.copy(alpha = 0.5f))))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Text(subtitle, fontSize = 10.sp, color = TextSecondaryDark)
            }
        }
    }
}
