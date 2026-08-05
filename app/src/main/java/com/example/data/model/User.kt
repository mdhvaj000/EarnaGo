package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    MEMBER,
    ADMIN,
    OWNER
}

enum class UserRank(val title: String, val requiredBv: Double, val requiredDirects: Int, val matchingBonusPct: Double) {
    ASSOCIATE("Associate", 0.0, 0, 0.0),
    BUILDER("Builder", 500.0, 2, 0.05),
    EXECUTIVE("Executive", 2500.0, 4, 0.08),
    DIRECTOR("Director", 10000.0, 6, 0.12),
    AMBASSADOR("Ambassador", 50000.0, 10, 0.20);

    companion object {
        fun evaluateRank(volume: Double, directs: Int): UserRank {
            return values().reversed().firstOrNull { volume >= it.requiredBv && directs >= it.requiredDirects } ?: ASSOCIATE
        }
    }
}

enum class KycStatus {
    NOT_SUBMITTED,
    PENDING,
    APPROVED,
    REJECTED
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val rank: UserRank,
    val kycStatus: KycStatus,
    val sponsorId: String?,
    val referralCode: String,
    val personalVolume: Double = 0.0,
    val teamVolume: Double = 0.0,
    val directDownlineCount: Int = 0,
    val walletBalance: Double = 0.0,
    val pendingCommission: Double = 0.0,
    val totalPaidOut: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
