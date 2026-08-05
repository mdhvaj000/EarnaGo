package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KycStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KYCScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val activeUser by viewModel.activeUser.collectAsState()
    val kycDocument by viewModel.userKyc.collectAsState()

    val user = activeUser ?: return

    var fullName by remember { mutableStateOf(user.fullName) }
    var idNumber by remember { mutableStateOf("") }
    var documentType by remember { mutableStateOf("Passport") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KYC & Compliance Verification", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
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
            // Status Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSlate),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(
                                when (user.kycStatus) {
                                    KycStatus.APPROVED -> EmeraldSuccess
                                    KycStatus.PENDING -> AmberWarning
                                    KycStatus.REJECTED -> CrimsonError
                                    else -> SurfaceBorderDark
                                },
                                AccentGold
                            )
                        )
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (user.kycStatus) {
                                        KycStatus.APPROVED -> Icons.Default.Verified
                                        KycStatus.PENDING -> Icons.Default.HourglassTop
                                        KycStatus.REJECTED -> Icons.Default.Error
                                        else -> Icons.Default.Assignment
                                    },
                                    contentDescription = null,
                                    tint = when (user.kycStatus) {
                                        KycStatus.APPROVED -> EmeraldSuccess
                                        KycStatus.PENDING -> AmberWarning
                                        KycStatus.REJECTED -> CrimsonError
                                        else -> TextSecondaryDark
                                    },
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Verification Status", fontSize = 12.sp, color = TextSecondaryDark)
                                    Text(
                                        text = user.kycStatus.name.replace("_", " "),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                }
                            }
                        }

                        if (kycDocument?.adminNotes != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Admin Review Notes: ${kycDocument?.adminNotes}", fontSize = 12.sp, color = AccentGold)
                        }
                    }
                }
            }

            // Document Submission Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackgroundDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Submit Identification Documents", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        Text("Identity verification is required for international banking and commission payouts.", fontSize = 12.sp, color = TextSecondaryDark)

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Legal Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = idNumber,
                            onValueChange = { idNumber = it },
                            label = { Text("Government ID / Passport Number") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentGold)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Document Type:", fontSize = 12.sp, color = TextSecondaryDark)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = documentType == "Passport",
                                onClick = { documentType = "Passport" },
                                label = { Text("Passport") }
                            )
                            FilterChip(
                                selected = documentType == "Driver License",
                                onClick = { documentType = "Driver License" },
                                label = { Text("Driver License") }
                            )
                            FilterChip(
                                selected = documentType == "National ID",
                                onClick = { documentType = "National ID" },
                                label = { Text("National ID") }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (fullName.isNotBlank() && idNumber.isNotBlank()) {
                                    viewModel.submitKyc(fullName, idNumber, documentType)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = NavyDeep),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Submit KYC Documents", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
