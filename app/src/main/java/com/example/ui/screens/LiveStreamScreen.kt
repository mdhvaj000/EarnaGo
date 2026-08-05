package com.example.ui.screens

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
import com.example.data.network.LiveChatMessage
import com.example.data.network.LiveMarketTicker
import com.example.data.network.LiveNewsArticle
import com.example.data.network.RealtimeStreamService
import com.example.data.network.UpiPaymentManager
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LiveStreamScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(true) }
    var selectedQuality by remember { mutableStateOf("1080p HD") }
    var fireCount by remember { mutableStateOf(1240) }
    var clapCount by remember { mutableStateOf(3420) }
    var heartCount by remember { mutableStateOf(5180) }
    var rocketCount by remember { mutableStateOf(2890) }

    var tickerData by remember { mutableStateOf(LiveMarketTicker()) }
    val chatMessages = remember { mutableStateListOf<LiveChatMessage>() }
    var chatInput by remember { mutableStateOf("") }

    var newsList by remember { mutableStateOf<List<LiveNewsArticle>>(emptyList()) }
    var isLoadingNews by remember { mutableStateOf(false) }

    // Collect real-time live ticker stream
    LaunchedEffect(Unit) {
        launch {
            RealtimeStreamService.getMarketAndNetworkTickerStream().collectLatest {
                tickerData = it
            }
        }
        launch {
            RealtimeStreamService.getLiveChatStream().collectLatest {
                if (chatMessages.size > 40) chatMessages.removeAt(0)
                chatMessages.add(it)
            }
        }
        launch {
            isLoadingNews = true
            newsList = RealtimeStreamService.fetchLiveIndianMarketNews()
            isLoadingNews = false
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = DarkSlate,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimaryDark
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Live Broadcast & Network",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE53935)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${tickerData.activeLiveViewers} Viewers Across India 🇮🇳",
                            fontSize = 12.sp,
                            color = AccentGold
                        )
                    }
                }
            }
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
            // 1. Real-Time Indian Market & Volume Ticker Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(RoyalBlue, AccentGold))
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🇮🇳 REAL-TIME LIVE MARKET TICKER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGold
                            )
                            Text(
                                text = "Live Stream Active",
                                fontSize = 10.sp,
                                color = EmeraldSuccess
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("NIFTY 50", fontSize = 10.sp, color = TextSecondaryDark)
                                Text(tickerData.nifty50, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                            }
                            Column {
                                Text("SENSEX", fontSize = 10.sp, color = TextSecondaryDark)
                                Text(tickerData.sensex, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                            }
                            Column {
                                Text("GOLD / 10g", fontSize = 10.sp, color = TextSecondaryDark)
                                Text(tickerData.goldRate10g, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                            Column {
                                Text("NETWORK BV", fontSize = 10.sp, color = TextSecondaryDark)
                                Text(tickerData.liveNetworkBv, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                            }
                        }
                    }
                }
            }

            // 2. Video Broadcast Player Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Column {
                        // Video Screen Display Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF101828), Color(0xFF070B14))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            // Simulated Video Content Overlay
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Videocam else Icons.Default.Pause,
                                    contentDescription = "Video Stream",
                                    tint = AccentGold,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isPlaying) "National Leadership Convention - Live Broadcast" else "Broadcast Paused",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Streaming in $selectedQuality • Ultra Low Latency Network",
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark
                                )
                            }

                            // Quality Badge Top-Right
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = selectedQuality,
                                    fontSize = 10.sp,
                                    color = AccentGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Player Controls Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSlate)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { isPlaying = !isPlaying },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = AccentGold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPlaying) "STREAMING" else "PAUSED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPlaying) EmeraldSuccess else Color.Gray
                                )
                            }

                            // Reaction Buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { fireCount++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("🔥 $fireCount", fontSize = 11.sp, color = TextPrimaryDark)
                                }
                                Button(
                                    onClick = { clapCount++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("👏 $clapCount", fontSize = 11.sp, color = TextPrimaryDark)
                                }
                                Button(
                                    onClick = { heartCount++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("❤️ $heartCount", fontSize = 11.sp, color = TextPrimaryDark)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Live Indian UPI Instant Payment Launcher
            item {
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
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = AccentGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Live UPI Payment Gateway (India 🇮🇳)", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Supports instant launch of GPay, PhonePe, Paytm, BHIM & Cred for starter kit orders and wallet recharges.",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val gst = UpiPaymentManager.calculateGst(4999.0, 18.0)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Sample Starter Kit: ₹4,999.00", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                Text("Base: ₹${String.format("%.2f", gst.baseAmount)} + GST (18%): ₹${String.format("%.2f", gst.totalTax)}", fontSize = 11.sp, color = TextSecondaryDark)
                            }

                            Button(
                                onClick = {
                                    UpiPaymentManager.launchUpiPayment(
                                        context = context,
                                        payeeVpa = "earnago@upi",
                                        payeeName = "EarnaGo Digital India",
                                        transactionId = "TXN${System.currentTimeMillis()}",
                                        transactionRef = "REF${(100000..999999).random()}",
                                        note = "EarnaGo Enterprise Suite Activation",
                                        amountInInr = 4999.0,
                                        onError = { errorMsg ->
                                            viewModel.postUiMessage(errorMsg)
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Pay via UPI", color = NavyDeep, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 4. Live Interactive Chat Stream Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💬 LIVE NETWORK CHAT FEED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                            Text("${chatMessages.size} messages", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            reverseLayout = true
                        ) {
                            items(chatMessages.reversed()) { msg ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (msg.isSystemAnnouncement) AccentGold.copy(alpha = 0.15f) else NavyDeep,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${msg.senderName} (${msg.cityState})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (msg.isSystemAnnouncement) AccentGold else VibrantCyan
                                            )
                                            Text(msg.timestamp, fontSize = 9.sp, color = TextSecondaryDark)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(msg.messageText, fontSize = 12.sp, color = TextPrimaryDark)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Chat Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Post a message to live network...", fontSize = 12.sp, color = TextSecondaryDark) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentGold,
                                    unfocusedBorderColor = Color.Gray,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (chatInput.isNotBlank()) {
                                        chatMessages.add(
                                            LiveChatMessage(
                                                id = "user_${System.currentTimeMillis()}",
                                                senderName = viewModel.activeUser.value?.fullName ?: "You",
                                                cityState = "New Delhi, DL",
                                                userRank = viewModel.activeUser.value?.rank?.name ?: "MEMBER",
                                                messageText = chatInput,
                                                timestamp = "Just now"
                                            )
                                        )
                                        chatInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(AccentGold)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = NavyDeep)
                            }
                        }
                    }
                }
            }

            // 5. Live Indian Market & Industry News Stream
            item {
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
                                Icon(Icons.Default.Newspaper, contentDescription = null, tint = VibrantCyan)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Live Indian Market & Industry Stream", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
                            }
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    isLoadingNews = true
                                    newsList = RealtimeStreamService.fetchLiveIndianMarketNews()
                                    isLoadingNews = false
                                }
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = AccentGold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoadingNews) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(16.dp),
                                color = AccentGold
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                newsList.forEach { article ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = NavyDeep,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(article.source, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                                Text(article.publishedAt, fontSize = 10.sp, color = TextSecondaryDark)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(article.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(article.snippet, fontSize = 11.sp, color = TextSecondaryDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
