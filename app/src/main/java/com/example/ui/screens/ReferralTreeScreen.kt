package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralTreeScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    val directs by viewModel.directDownlines.collectAsState()
    val context = LocalContext.current

    val user = activeUser ?: return
    val referralLink = "https://omnicontrol.app/ref/${user.referralCode}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Referral Tree & Lineage", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
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
            // Referral Link Copy Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AccentGold, RoyalBlue)))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Your Direct Sponsor Referral Link", fontSize = 12.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardBackgroundDark)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(referralLink, fontSize = 12.sp, color = TextPrimaryDark, fontWeight = FontWeight.Bold)

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("EarnaGo Referral Link", referralLink)
                                    clipboard.setPrimaryClip(clip)
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AccentGold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Share this link to automatically place new team members under your sponsor ID.", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                }
            }

            // Downline Network Hierarchy Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tier 1 Direct Network (${directs.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RoyalBlue.copy(alpha = 0.2f)
                    ) {
                        Text("Binary / Unilevel Tree", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (directs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.AccountTree, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No direct downline members yet.", fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Text("Share your referral link above to start expanding your network!", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                    }
                }
            } else {
                items(directs) { member ->
                    DownlineNodeCard(member = member)
                }
            }
        }
    }
}

@Composable
fun DownlineNodeCard(member: UserEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorderDark, RoyalBlue.copy(alpha = 0.4f))))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(RoyalBlue, VibrantCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.fullName.take(1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(member.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
                        Text("Ref: ${member.referralCode} • ${member.email}", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AccentGold.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = member.rank.title,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = SurfaceBorderDark)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Personal Vol (PV)", fontSize = 10.sp, color = TextSecondaryDark)
                    Text("${member.personalVolume.toInt()} BV", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Team Vol (TV)", fontSize = 10.sp, color = TextSecondaryDark)
                    Text("${member.teamVolume.toInt()} BV", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Directs", fontSize = 10.sp, color = TextSecondaryDark)
                    Text("${member.directDownlineCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                }
            }
        }
    }
}
