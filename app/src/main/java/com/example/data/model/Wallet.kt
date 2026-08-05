package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    COMMISSION_CREDIT,
    REFERRAL_BONUS,
    MATCHING_BONUS,
    PAYOUT_DEBIT,
    ORDER_PAYMENT,
    WALLET_DEPOSIT,
    ADMIN_ADJUSTMENT
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REJECTED
}

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val type: TransactionType,
    val status: TransactionStatus,
    val description: String,
    val referenceId: String? = null, // Order ID or Payout ID
    val createdAt: Long = System.currentTimeMillis()
)
