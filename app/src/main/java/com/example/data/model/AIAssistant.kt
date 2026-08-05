package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AISender {
    USER,
    ASSISTANT
}

enum class AICategory {
    GENERAL_COACH,
    MARKETING_POST,
    SALES_PREDICTION,
    TEAM_INSIGHT
}

@Entity(tableName = "ai_messages")
data class AIMessageEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val sender: AISender,
    val content: String,
    val category: AICategory = AICategory.GENERAL_COACH,
    val timestamp: Long = System.currentTimeMillis()
)
