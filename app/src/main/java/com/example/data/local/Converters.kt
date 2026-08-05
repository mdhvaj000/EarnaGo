package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.*

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = enumValueOf(value)

    @TypeConverter
    fun fromUserRank(value: UserRank): String = value.name

    @TypeConverter
    fun toUserRank(value: String): UserRank = enumValueOf(value)

    @TypeConverter
    fun fromKycStatus(value: KycStatus): String = value.name

    @TypeConverter
    fun toKycStatus(value: String): KycStatus = enumValueOf(value)

    @TypeConverter
    fun fromProductCategory(value: ProductCategory): String = value.name

    @TypeConverter
    fun toProductCategory(value: String): ProductCategory = enumValueOf(value)

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String = value.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = enumValueOf(value)

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = enumValueOf(value)

    @TypeConverter
    fun fromTransactionStatus(value: TransactionStatus): String = value.name

    @TypeConverter
    fun toTransactionStatus(value: String): TransactionStatus = enumValueOf(value)

    @TypeConverter
    fun fromCommissionType(value: CommissionType): String = value.name

    @TypeConverter
    fun toCommissionType(value: String): CommissionType = enumValueOf(value)

    @TypeConverter
    fun fromAISender(value: AISender): String = value.name

    @TypeConverter
    fun toAISender(value: String): AISender = enumValueOf(value)

    @TypeConverter
    fun fromAICategory(value: AICategory): String = value.name

    @TypeConverter
    fun toAICategory(value: String): AICategory = enumValueOf(value)

    @TypeConverter
    fun fromEarnaGoModuleCategory(value: EarnaGoModuleCategory): String = value.name

    @TypeConverter
    fun toEarnaGoModuleCategory(value: String): EarnaGoModuleCategory = enumValueOf(value)
}
