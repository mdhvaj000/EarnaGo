package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE referralCode = :code")
    suspend fun getUserByReferralCode(code: String): UserEntity?

    @Query("SELECT * FROM users WHERE sponsorId = :sponsorId")
    fun getDirectsBySponsorId(sponsorId: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1")
    fun getAllActiveProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE buyerUserId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUserId(userId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus)
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactionsByUserId(userId: String): Flow<List<WalletTransactionEntity>>

    @Query("SELECT * FROM wallet_transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransactionEntity)
}

@Dao
interface CommissionDao {
    @Query("SELECT * FROM commission_records WHERE recipientUserId = :userId ORDER BY createdAt DESC")
    fun getCommissionsByUserId(userId: String): Flow<List<CommissionRecordEntity>>

    @Query("SELECT * FROM commission_records ORDER BY createdAt DESC")
    fun getAllCommissions(): Flow<List<CommissionRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommission(commission: CommissionRecordEntity)
}

@Dao
interface ReferralDao {
    @Query("SELECT * FROM referral_nodes WHERE sponsorId = :sponsorId")
    fun getNodesBySponsor(sponsorId: String): Flow<List<ReferralNodeEntity>>

    @Query("SELECT * FROM referral_nodes")
    fun getAllNodes(): Flow<List<ReferralNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNode(node: ReferralNodeEntity)
}

@Dao
interface KycDao {
    @Query("SELECT * FROM kyc_documents WHERE userId = :userId")
    fun getKycByUserId(userId: String): Flow<KycDocumentEntity?>

    @Query("SELECT * FROM kyc_documents ORDER BY submittedAt DESC")
    fun getAllKycSubmissions(): Flow<List<KycDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateKyc(kyc: KycDocumentEntity)
}

@Dao
interface AIDao {
    @Query("SELECT * FROM ai_messages WHERE userId = :userId ORDER BY timestamp ASC")
    fun getMessagesByUserId(userId: String): Flow<List<AIMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AIMessageEntity)
}
