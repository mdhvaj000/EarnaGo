package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProductCategory {
    PHYSICAL,
    DIGITAL,
    SERVICE,
    STARTER_KIT
}

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val sku: String,
    val name: String,
    val description: String,
    val price: Double,
    val bvWeight: Double, // Business Volume weight for commissions
    val pvWeight: Double, // Personal Volume weight
    val category: ProductCategory,
    val stockQuantity: Int,
    val imageUrl: String = "",
    val isFeatured: Boolean = false,
    val isActive: Boolean = true
)
