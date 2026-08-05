package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "referral_nodes")
data class ReferralNodeEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val rank: UserRank,
    val sponsorId: String?,
    val depthLevel: Int,
    val personalVolume: Double,
    val teamVolume: Double,
    val directDownlineCount: Int
)
