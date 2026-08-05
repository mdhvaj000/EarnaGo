package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CommissionType {
    DIRECT_SALE,
    TIER_LEVEL_BONUS,
    MATCHING_RANK_BONUS
}

@Entity(tableName = "commission_records")
data class CommissionRecordEntity(
    @PrimaryKey val id: String,
    val recipientUserId: String,
    val recipientName: String,
    val sourceOrderId: String,
    val sourceBuyerName: String,
    val sourceBv: Double,
    val commissionAmount: Double,
    val tierLevel: Int, // Tier 1, Tier 2, etc.
    val type: CommissionType,
    val isVerifiedProductSale: Boolean = true, // Compliance check
    val createdAt: Long = System.currentTimeMillis()
)
