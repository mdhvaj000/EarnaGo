package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRank
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankCommissionScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    val commissions by viewModel.userCommissions.collectAsState()

    val user = activeUser ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Commissions & Rank Engine", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
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
            // Compliance Audit Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldSuccess, AccentGold)))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Legitimate Commerce Protection", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
                            Text("100% of commissions are strictly derived from verified product sales. Recruitment-only bonuses are strictly prohibited.", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }

            // Rank Roadmap Matrix Section
            item {
                Text("Rank Requirements Roadmap", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    UserRank.values().forEach { rank ->
                        val isAchieved = user.rank.ordinal >= rank.ordinal
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAchieved) RoyalBlue.copy(alpha = 0.2f) else CardBackgroundDark
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isAchieved) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isAchieved) EmeraldSuccess else TextSecondaryDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(rank.title, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                        Text("Min BV: ${rank.requiredBv.toInt()} • Directs: ${rank.requiredDirects}", fontSize = 11.sp, color = TextSecondaryDark)
                                    }
                                }

                                if (rank.matchingBonusPct > 0.0) {
                                    Text("${(rank.matchingBonusPct * 100).toInt()}% Match", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                }
                            }
                        }
                    }
                }
            }

            // Commission History Log Section
            item {
                Text("Earned Commission Log", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            }

            if (commissions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                    ) {
                        Text("No commissions logged yet. Sales in your downline will automatically trigger commissions here.", modifier = Modifier.padding(16.dp), color = TextSecondaryDark, fontSize = 13.sp)
                    }
                }
            } else {
                items(commissions) { comm ->
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
                            Column {
                                Text(comm.type.name.replace("_", " "), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                                Text("Buyer: ${comm.sourceBuyerName} • BV: ${comm.sourceBv}", fontSize = 11.sp, color = TextSecondaryDark)
                                Text("Order ID: ${comm.sourceOrderId}", fontSize = 10.sp, color = AccentGold)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("+$${String.format("%.2f", comm.commissionAmount)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldSuccess)
                                Text("Tier ${comm.tierLevel}", fontSize = 10.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
