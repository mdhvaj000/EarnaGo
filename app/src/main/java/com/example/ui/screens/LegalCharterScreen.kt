package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalCharterScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Direct Selling Legal Charter & Rules", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Government Compliance Header Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(EmeraldSuccess, AccentGold))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Consumer Protection (Direct Selling) Rules, 2021",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Ministry of Consumer Affairs, Food & Public Distribution, Govt of India",
                                    fontSize = 11.sp,
                                    color = EmeraldSuccess
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "EarnaGo Platform India operates strictly in accordance with statutory direct selling rules and legal commerce directives. Pyramid schemes, headhunting recruitment fees, and forced inventory loading are strictly prohibited.",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            // Pillar 1: Zero Joining Fee & Non-Pyramid Architecture
            item {
                LegalPrincipleCard(
                    title = "1. Zero Joining / Registration Fee",
                    subtitle = "Statutory Anti-Pyramid & Anti-Ponzi Compliance",
                    description = "Joining EarnaGo Direct Selling Network is completely free of cost. No member is required to pay any entrance, renewal, or training fee to register or receive commissions.",
                    icon = Icons.Default.MonetizationOn,
                    badgeText = "Rule 5(1) Compliant"
                )
            }

            // Pillar 2: 30-Day Cooling-Off & Buy-Back Guarantee
            item {
                LegalPrincipleCard(
                    title = "2. 30-Day Cooling-Off & Buy-Back Guarantee",
                    subtitle = "100% Refund Protection for Distributors & Customers",
                    description = "Distributors and consumers enjoy a 30-day cooling-off period. Unopened physical products and starter packs can be returned within 30 days for a full refund (minus applicable logistics costs).",
                    icon = Icons.Default.AssignmentReturn,
                    badgeText = "Rule 6(2) Guaranteed"
                )
            }

            // Pillar 3: Legitimate BV Product Commerce Rule
            item {
                LegalPrincipleCard(
                    title = "3. 100% Product-Driven Commission Structure",
                    subtitle = "No Recruitment Bonuses Allowed",
                    description = "Commissions and Business Volume (BV) are strictly generated by actual sales of certified products and services to end consumers. No payouts are made merely for recruiting new individuals.",
                    icon = Icons.Default.ShoppingBag,
                    badgeText = "Rule 7(3) Enforced"
                )
            }

            // Pillar 4: GST & Tax Invoice Automation
            item {
                LegalPrincipleCard(
                    title = "4. Automated GST Invoicing & Tax Compliance",
                    subtitle = "18% GST Breakdown on All Transactions",
                    description = "Every transaction includes a legal tax invoice detailing Central GST (CGST), State GST (SGST), or Integrated GST (IGST) with HSN/SAC codes. All commissions are disbursed with mandatory TDS deduction as per Indian Income Tax laws.",
                    icon = Icons.Default.ReceiptLong,
                    badgeText = "GST & Income Tax Compliant"
                )
            }

            // Pillar 5: Nodal Officer & Grievance Redressal Desk
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = VibrantCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Nodal Officer & Grievance Redressal Officer",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "In accordance with Direct Selling Rules 2021, a dedicated Nodal Compliance Officer is available for handling distributor and consumer grievances:",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavyDeep,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Nodal Officer: Shri Anand V. Kulkarni", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                Text("Designation: Chief Compliance & Grievance Officer", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("Official Email: grievance@earnago.in", fontSize = 11.sp, color = AccentGold)
                                Text("Helpline Desk: +91 1800-266-9900 (Toll Free, Mon-Sat 9AM - 6PM)", fontSize = 11.sp, color = EmeraldSuccess)
                                Text("Address: EarnaGo Tower, Plot 42, Cyber City, Gurugram, HR - 122002", fontSize = 10.sp, color = TextSecondaryDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegalPrincipleCard(
    title: String,
    subtitle: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlate)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = AccentGold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(title, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldSuccess.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(subtitle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VibrantCyan)
            Spacer(modifier = Modifier.height(6.dp))
            Text(description, fontSize = 12.sp, color = TextSecondaryDark)
        }
    }
}
