package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kyc_documents")
data class KycDocumentEntity(
    @PrimaryKey val userId: String,
    val fullName: String,
    val idNumber: String,
    val documentType: String,
    val documentFrontUrl: String,
    val proofOfAddressUrl: String,
    val status: KycStatus,
    val adminNotes: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)
