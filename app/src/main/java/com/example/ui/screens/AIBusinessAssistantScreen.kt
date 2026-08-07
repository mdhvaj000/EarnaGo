package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AICategory
import com.example.data.model.AIMessageEntity
import com.example.data.model.AISender
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel
import com.example.ui.viewmodel.PlanckActivityLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIBusinessAssistantScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    val aiMessages by viewModel.aiMessages.collectAsState()
    val isInternetConnected by viewModel.isLiveInternetConnected.collectAsState()
    val isAutonomousActive by viewModel.isAutonomousManagementActive.collectAsState()
    val planckLogs by viewModel.planckActivityStream.collectAsState()
    val lastScanResult by viewModel.lastPlanckScanResult.collectAsState()
    val isScanning by viewModel.isScanningPlanck.collectAsState()

    val context = LocalContext.current

    var userPrompt by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AICategory.GENERAL_COACH) }
    var activeSubTab by remember { mutableStateOf(0) } // 0: Planck Management Engine, 1: AI Assistant Chat

    val user = activeUser ?: return

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(NavyDeep)) {
                TopAppBar(
                    title = {
                        Column {
                            Text("Planck-Time AI & App Control", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Automated Internet Management Engine", color = AccentGold, fontSize = 11.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDeep)
                )

                // Live Internet & Edge Node Connectivity Status Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isInternetConnected) DarkSlate else CardBackgroundDark)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isInternetConnected) EmeraldSuccess else CrimsonError)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isInternetConnected) "LIVE INTERNET CONNECTED • 0.038ms" else "OFFLINE MODE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isInternetConnected) EmeraldSuccess else CrimsonError
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isAutonomousActive) "AI AUTO-MANAGE ON" else "PAUSED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAutonomousActive) AccentGold else TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = isAutonomousActive,
                            onCheckedChange = { viewModel.toggleAutonomousManagement() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NavyDeep,
                                checkedTrackColor = AccentGold,
                                uncheckedThumbColor = TextSecondaryDark,
                                uncheckedTrackColor = SurfaceBorderDark
                            ),
                            modifier = Modifier.scaleScale(0.8f)
                        )
                    }
                }

                // Tab Selector
                TabRow(
                    selectedTabIndex = activeSubTab,
                    containerColor = DarkSlate,
                    contentColor = AccentGold
                ) {
                    Tab(
                        selected = activeSubTab == 0,
                        onClick = { activeSubTab = 0 }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Planck Management", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Tab(
                        selected = activeSubTab == 1,
                        onClick = { activeSubTab = 1 }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Strategy Chat", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (activeSubTab == 1) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSlate)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategory == AICategory.GENERAL_COACH,
                            onClick = { selectedCategory = AICategory.GENERAL_COACH },
                            label = { Text("Coach Strategy", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = selectedCategory == AICategory.MARKETING_POST,
                            onClick = { selectedCategory = AICategory.MARKETING_POST },
                            label = { Text("Social Post", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = selectedCategory == AICategory.SALES_PREDICTION,
                            onClick = { selectedCategory = AICategory.SALES_PREDICTION },
                            label = { Text("Predictive Sales", fontSize = 11.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userPrompt,
                            onValueChange = { userPrompt = it },
                            placeholder = { Text("Ask AI business coach...", color = TextSecondaryDark) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentGold,
                                unfocusedBorderColor = SurfaceBorderDark,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (userPrompt.isNotBlank()) {
                                    viewModel.sendAIMessage(userPrompt, selectedCategory)
                                    userPrompt = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AccentGold)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NavyDeep)
                        }
                    }
                }
            }
        },
        containerColor = NavyDeep
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (activeSubTab == 0) {
                // Planck Management & Live Telemetry Feed View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Scan Trigger Hero Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSlate),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = Brush.horizontalGradient(listOf(AccentGold, VibrantCyan))
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Planck-Time AI App Manager", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                        Text("Real-Time Sub-Nanosecond Autonomous Optimization", fontSize = 11.sp, color = AccentGold)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldSuccess.copy(alpha = 0.2f)
                                    ) {
                                        Text("10^38 t_p Precision", color = EmeraldSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    "Continuously monitors, audits, and manages app commissions, task royalty payouts, order fulfillment, and security in Planck time.",
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { viewModel.runPlanckTimeAIScan() },
                                    enabled = !isScanning,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isScanning) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = NavyDeep)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Executing Planck AI Scan...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.Bolt, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Trigger Instant Planck AI App Scan", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // 2. Scan Results Card
                    lastScanResult?.let { res ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldSuccess, AccentGold)))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Scan Result • ${res.scanTimestamp}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AccentGold)
                                        Text("Latency: ${res.latencyP50Ms} ms", fontSize = 11.sp, color = EmeraldSuccess)
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(res.summaryReport, fontSize = 12.sp, color = TextPrimaryDark, lineHeight = 18.sp)
                                }
                            }
                        }
                    }

                    // 3. Live Telemetry Activity Feed
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Real-Time Live Planck Telemetry Stream", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            Surface(shape = RoundedCornerShape(6.dp), color = RoyalBlue.copy(alpha = 0.2f)) {
                                Text("${planckLogs.size} Events", color = RoyalBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    items(planckLogs) { log ->
                        PlanckActivityItemCard(log)
                    }
                }
            } else {
                // AI Coach Chat View
                if (aiMessages.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = AccentGold, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("EarnaGo AI Strategy Coach", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Text(
                            "Ask strategy questions, generate marketing copy, or analyze sales volume predictions for your downline.",
                            fontSize = 13.sp,
                            color = TextSecondaryDark,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.sendAIMessage("How can I increase team sales volume to reach Director rank?", AICategory.GENERAL_COACH)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CardBackgroundDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("💡 Strategy: Path to Director Rank", color = AccentGold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                viewModel.sendAIMessage("Generate a promotional social post for the Digital Academy Suite.", AICategory.MARKETING_POST)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CardBackgroundDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("✍️ Generate Social Media Product Post", color = VibrantCyan, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(aiMessages) { msg ->
                            AIMessageBubble(message = msg, onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("AI Content", msg.content)
                                clipboard.setPrimaryClip(clip)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanckActivityItemCard(log: PlanckActivityLog) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = RoyalBlue.copy(alpha = 0.2f)
                    ) {
                        Text(log.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = VibrantCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(log.planckTimeScale, fontSize = 10.sp, color = TextSecondaryDark)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(log.activity, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                Text(log.aiAutomatedDecision, fontSize = 11.sp, color = EmeraldSuccess, fontWeight = FontWeight.SemiBold)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(log.timestampFormatted, fontSize = 10.sp, color = TextSecondaryDark)
                Text("${String.format("%.3f", log.latencyMs)} ms", fontSize = 10.sp, color = AccentGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helper modifier scale
private fun Modifier.scaleScale(scale: Float): Modifier = this.then(Modifier.size((36 * scale).dp, (20 * scale).dp))

@Composable
fun AIMessageBubble(
    message: AIMessageEntity,
    onCopy: () -> Unit
) {
    val isUser = message.sender == AISender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) RoyalBlue else CardBackgroundDark
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUser) "You" else "EarnaGo AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) Color.White.copy(alpha = 0.8f) else AccentGold
                    )

                    if (!isUser) {
                        IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.content,
                    fontSize = 13.sp,
                    color = TextPrimaryDark
                )
            }
        }
    }
}

