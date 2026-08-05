package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus {
    DRAFT,
    PAYMENT_PENDING,
    PROCESSING,
    FULFILLED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}

data class OrderItem(
    val productId: String,
    val productName: String,
    val unitPrice: Double,
    val bvWeight: Double,
    val quantity: Int
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val buyerUserId: String,
    val buyerName: String,
    val totalAmount: Double,
    val totalBv: Double,
    val status: OrderStatus,
    val itemsJson: String, // JSON serialized List<OrderItem>
    val paymentMethod: String,
    val createdAt: Long = System.currentTimeMillis(),
    val fulfilledAt: Long? = null
)
