package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EarnaGoModuleCategory(val displayName: String, val badgeColorHex: String) {
    AFFILIATE_MARKETING("Affiliate Networks", "#FF9900"),
    DIGITAL_PRODUCTS("Digital Product Store", "#00C853"),
    AI_SERVICES("AI Services Hub", "#7C4DFF"),
    FREELANCE_GIGS("Freelance & Tech Gigs", "#00E5FF"),
    ONLINE_LEARNING("Academy & Certification", "#FFAB00"),
    REFERRAL_NETWORK("MLM Referral Engine", "#D500F9"),
    CASHBACK_COUPONS("Cashback & Deals", "#FF1744"),
    MICRO_TASKS_SURVEYS("Surveys & Micro Tasks", "#1DE9B6"),
    LOCAL_BUSINESS("Local City Marketplace", "#3D5AFF"),
    DIGITAL_UTILITIES("Recharge & Bill Pay", "#00B0FF"),
    DROPSHIPPING("Dropshipping Commerce", "#FF6D00"),
    CREATOR_ECONOMY("Creator Economy Tools", "#E040FB"),
    RESELLING_COMMERCE("Wholesale Reselling", "#00E676"),
    JOB_GIG_MARKETPLACE("Jobs & Daily Gigs", "#651FFF"),
    B2B_WHOLESALE("B2B & Manufacturer Deals", "#3F51B5")
}

@Entity(tableName = "earnago_tasks")
data class EarnaGoTaskEntity(
    @PrimaryKey val id: String,
    val module: EarnaGoModuleCategory,
    val title: String,
    val description: String,
    val partnerName: String,
    val rewardAmount: Double, // Total reward in INR
    val commissionPct: Double = 0.0,
    val requirements: String,
    val estimatedTime: String,
    val difficulty: String = "Easy", // Easy, Medium, Advanced
    val bvValue: Double = 0.0,
    val actionUrl: String = "",
    val imageUrl: String = "",
    val isLiveInternetSynced: Boolean = true,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "owner_profile")
data class OwnerProfileEntity(
    @PrimaryKey val id: String = "owner_profile",
    val ownerUserId: String = "usr_owner",
    val ownerName: String = "Sole Platform Founder & Rights Holder",
    val bankAccountName: String = "EarnaGo Digital Private Limited",
    val bankAccountNumber: String = "91800266990042",
    val bankIfscCode: String = "HDFC0000240",
    val bankName: String = "HDFC Bank Cyber City",
    val upiVpa: String = "earnago.owner@okicici",
    val panNumber: String = "ABCDE1234F",
    val gstinNumber: String = "06ABCDE1234F1Z5",
    val platformRoyaltyPct: Double = 5.0, // 5% auto-deducted to owner
    val totalRoyaltyEarnedInr: Double = 0.0,
    val autoSettlementEnabled: Boolean = true,
    val copyrightNotice: String = "Copyright © 2026 EarnaGo Platform India. All rights and intellectual property strictly reserved to sole founder owner."
)
