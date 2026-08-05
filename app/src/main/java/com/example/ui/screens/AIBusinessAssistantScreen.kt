package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AICategory
import com.example.data.model.AIMessageEntity
import com.example.data.model.AISender
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIBusinessAssistantScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    val aiMessages by viewModel.aiMessages.collectAsState()
    val context = LocalContext.current

    var userPrompt by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AICategory.GENERAL_COACH) }

    val user = activeUser ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Business Assistant", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDeep)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSlate)
                    .padding(12.dp)
            ) {
                // Category Pills
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
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = NavyDeep)
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
                    Text("OmniControl AI Coach", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    Text(
                        "Ask strategy questions, generate marketing copy, or analyze sales volume predictions for your downline.",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick Prompt Suggestions
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
                        text = if (isUser) "You" else "OmniControl AI",
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
