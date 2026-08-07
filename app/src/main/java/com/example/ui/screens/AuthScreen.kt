package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: OmniViewModel,
    onAuthSuccess: () -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val activeUser by viewModel.activeUser.collectAsState()

    var isRegisterMode by remember { mutableStateOf(false) }

    // Form fields for registration
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var sponsorCode by remember { mutableStateOf("ALEX-M88") }
    var password by remember { mutableStateOf("••••••••") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyDeep, DarkSlate, CardBackgroundDark)
                )
            )
            .padding(24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo Icon Header
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(AccentGold, RoyalBlue)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "EarnaGo Logo",
                tint = NavyDeep,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "EarnaGo",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )

        Text(
            text = "Autonomous AI Digital Business & Network Platform",
            fontSize = 14.sp,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Role Switcher / Direct Test User Profile Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorderDark, AccentGold)))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Quick Demo Identity Switcher",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentGold
                    )
                    Badge(containerColor = RoyalBlue) {
                        Text("RBAC Live", color = Color.White, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                allUsers.forEach { user ->
                    val isSelected = activeUser?.id == user.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) RoyalBlue.copy(alpha = 0.3f) else Color.Transparent)
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) AccentGold else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.switchActiveUser(user.id)
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (user.role) {
                                    UserRole.OWNER -> Icons.Default.AdminPanelSettings
                                    UserRole.ADMIN -> Icons.Default.SupervisorAccount
                                    UserRole.MEMBER -> Icons.Default.Person
                                },
                                contentDescription = null,
                                tint = if (user.role == UserRole.OWNER) AccentGold else VibrantCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = user.fullName,
                                    color = TextPrimaryDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${user.role.name} • ${user.rank.title}",
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle Authentication Mode: Member Sign In vs Referred Member Join vs Owner Master Portal
        var authTabMode by remember { mutableStateOf(0) } // 0: Sign In, 1: Referred Member Join, 2: Owner Master Registration

        // Owner Registration Specific Fields
        var ownerBankAcc by remember { mutableStateOf("918273645012") }
        var ownerIfsc by remember { mutableStateOf("SBIN0004210") }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSlate)
                .padding(4.dp)
        ) {
            Button(
                onClick = { authTabMode = 0 },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (authTabMode == 0) AccentGold else Color.Transparent,
                    contentColor = if (authTabMode == 0) NavyDeep else TextSecondaryDark
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { authTabMode = 1 },
                modifier = Modifier.weight(1.2f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (authTabMode == 1) AccentGold else Color.Transparent,
                    contentColor = if (authTabMode == 1) NavyDeep else TextSecondaryDark
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Referred Member", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
                onClick = { authTabMode = 2 },
                modifier = Modifier.weight(1.1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (authTabMode == 2) VibrantCyan else Color.Transparent,
                    contentColor = if (authTabMode == 2) NavyDeep else TextSecondaryDark
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Main Owner", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (authTabMode) {
            1 -> {
                // REFERRED MEMBER REGISTRATION
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🔗 Join via Sponsor Referral Link",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                    )

                    OutlinedTextField(
                        value = sponsorCode,
                        onValueChange = { sponsorCode = it },
                        label = { Text("Sponsor Referral Code (Link Connected)") },
                        leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RoyalBlue.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = VibrantCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Member Privileges: Starter ₹1,000 Wallet Bonus, Downline Tree Access, Task Rewards & Direct Selling Catalog.",
                                fontSize = 10.sp,
                                color = TextPrimaryDark
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && email.isNotBlank()) {
                                viewModel.registerNewMember(fullName, email, phone, sponsorCode)
                                onAuthSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Register Member via Referral", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            2 -> {
                // MAIN OWNER MASTER REGISTRATION & AUTHORIZATION
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "👑 Main Owner Master Portal & Bank Setup",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantCyan
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Sole Founder / Owner Name") },
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VibrantCyan)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Official Master Owner Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VibrantCyan)
                    )

                    OutlinedTextField(
                        value = ownerBankAcc,
                        onValueChange = { ownerBankAcc = it },
                        label = { Text("Owner Settlement Bank Account No.") },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VibrantCyan)
                    )

                    OutlinedTextField(
                        value = ownerIfsc,
                        onValueChange = { ownerIfsc = it },
                        label = { Text("Bank IFSC Code") },
                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VibrantCyan)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentGold.copy(alpha = 0.2f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AccentGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exclusive Owner Rights: 5% Platform Royalty Auto-Settlement, Module Task Management, KYC Queue Approval, System Control.",
                                fontSize = 10.sp,
                                color = TextPrimaryDark
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val nameToUse = if (fullName.isBlank()) "Sole App Owner" else fullName
                            val emailToUse = if (email.isBlank()) "owner@earnago.in" else email
                            viewModel.registerMasterOwner(nameToUse, emailToUse, phone, ownerBankAcc, ownerIfsc)
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantCyan, contentColor = NavyDeep),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Register / Authorize Master Owner", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            else -> {
                // DEFAULT MEMBER SIGN IN & ACTIVE ACCOUNT ENTER
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Enter Portal as ${activeUser?.fullName ?: "Member"}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.switchActiveUser("usr_owner")
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(AccentGold, RoyalBlue)))
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Owner Master Login (Bank & 5% Royalty Control)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sole Ownership & Copyright Notice
        Card(
            colors = CardDefaults.cardColors(containerColor = NavyDeep.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Copyright © 2026 EarnaGo Platform. All rights & intellectual property reserved to sole founder owner. 5% platform royalty auto-credited to owner bank account.",
                    fontSize = 11.sp,
                    color = TextSecondaryDark,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
