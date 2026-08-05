package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import com.example.engine.CommissionRankEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class OmniRepository(
    private val db: OmniDatabase
) {
    val userDao = db.userDao()
    val productDao = db.productDao()
    val orderDao = db.orderDao()
    val walletDao = db.walletDao()
    val commissionDao = db.commissionDao()
    val referralDao = db.referralDao()
    val kycDao = db.kycDao()
    val aiDao = db.aiDao()

    // Pre-populate sample seed data if database is empty
    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingProducts = productDao.getAllActiveProducts().firstOrNull()
        if (existingProducts.isNullOrEmpty()) {
            // Seed Default Products
            val products = listOf(
                ProductEntity(
                    id = "prod_001",
                    sku = "OMNI-DIG-01",
                    name = "OmniControl Digital Academy Suite",
                    description = "Comprehensive digital business OS, marketing funnel generator, and automation tools.",
                    price = 199.99,
                    bvWeight = 150.0,
                    pvWeight = 150.0,
                    category = ProductCategory.DIGITAL,
                    stockQuantity = 999,
                    imageUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=600&q=80",
                    isFeatured = true
                ),
                ProductEntity(
                    id = "prod_002",
                    sku = "OMNI-KIT-01",
                    name = "Enterprise Ambassador Starter Pack",
                    description = "Physical branding merchandise, product samples, hardware RFID tokens, and training kit.",
                    price = 499.00,
                    bvWeight = 400.0,
                    pvWeight = 400.0,
                    category = ProductCategory.STARTER_KIT,
                    stockQuantity = 150,
                    imageUrl = "https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=600&q=80",
                    isFeatured = true
                ),
                ProductEntity(
                    id = "prod_003",
                    sku = "OMNI-SVC-01",
                    name = "AI Lead Generation & Social Automation (1 Year)",
                    description = "Automated social post generation, team performance predictive insights, and lead tracking.",
                    price = 299.99,
                    bvWeight = 250.0,
                    pvWeight = 250.0,
                    category = ProductCategory.SERVICE,
                    stockQuantity = 999,
                    imageUrl = "https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&w=600&q=80",
                    isFeatured = false
                ),
                ProductEntity(
                    id = "prod_004",
                    sku = "OMNI-PHY-01",
                    name = "OmniBio Health & Focus Supplements (3 Month Supply)",
                    description = "Premium nutritional bio-hacking supplements for elite performers and sales executives.",
                    price = 149.50,
                    bvWeight = 120.0,
                    pvWeight = 120.0,
                    category = ProductCategory.PHYSICAL,
                    stockQuantity = 320,
                    imageUrl = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=600&q=80",
                    isFeatured = false
                )
            )
            productDao.insertAllProducts(products)

            // Seed Sample Hierarchy & Users
            val ownerUser = UserEntity(
                id = "usr_owner",
                fullName = "Eleanor Vance (System Owner)",
                email = "owner@omnicontrol.com",
                phone = "+1 800-555-0100",
                role = UserRole.OWNER,
                rank = UserRank.AMBASSADOR,
                kycStatus = KycStatus.APPROVED,
                sponsorId = null,
                referralCode = "OMNI-OWNER",
                personalVolume = 2500.0,
                teamVolume = 125000.0,
                directDownlineCount = 12,
                walletBalance = 15420.50,
                totalPaidOut = 84000.0
            )

            val adminUser = UserEntity(
                id = "usr_admin",
                fullName = "Marcus Sterling (Admin)",
                email = "admin@omnicontrol.com",
                phone = "+1 800-555-0101",
                role = UserRole.ADMIN,
                rank = UserRank.DIRECTOR,
                kycStatus = KycStatus.APPROVED,
                sponsorId = "usr_owner",
                referralCode = "OMNI-ADMIN",
                personalVolume = 1200.0,
                teamVolume = 45000.0,
                directDownlineCount = 8,
                walletBalance = 4850.00,
                totalPaidOut = 22000.0
            )

            val memberUser = UserEntity(
                id = "usr_member",
                fullName = "Alex Mercer (Executive Member)",
                email = "alex.mercer@omnicontrol.com",
                phone = "+1 555-0199",
                role = UserRole.MEMBER,
                rank = UserRank.EXECUTIVE,
                kycStatus = KycStatus.APPROVED,
                sponsorId = "usr_admin",
                referralCode = "ALEX-M88",
                personalVolume = 650.0,
                teamVolume = 3200.0,
                directDownlineCount = 4,
                walletBalance = 1280.40,
                pendingCommission = 340.00,
                totalPaidOut = 4500.00
            )

            val downlineMember1 = UserEntity(
                id = "usr_downline1",
                fullName = "Sarah Connor",
                email = "sarah.c@omnicontrol.com",
                phone = "+1 555-0211",
                role = UserRole.MEMBER,
                rank = UserRank.BUILDER,
                kycStatus = KycStatus.APPROVED,
                sponsorId = "usr_member",
                referralCode = "SARAH-C",
                personalVolume = 550.0,
                teamVolume = 1200.0,
                directDownlineCount = 2,
                walletBalance = 320.00,
                totalPaidOut = 850.00
            )

            val downlineMember2 = UserEntity(
                id = "usr_downline2",
                fullName = "David Miller",
                email = "david.m@omnicontrol.com",
                phone = "+1 555-0222",
                role = UserRole.MEMBER,
                rank = UserRank.ASSOCIATE,
                kycStatus = KycStatus.PENDING,
                sponsorId = "usr_member",
                referralCode = "DAVID-M",
                personalVolume = 150.0,
                teamVolume = 150.0,
                directDownlineCount = 0,
                walletBalance = 45.00,
                totalPaidOut = 0.0
            )

            userDao.insertOrUpdateUser(ownerUser)
            userDao.insertOrUpdateUser(adminUser)
            userDao.insertOrUpdateUser(memberUser)
            userDao.insertOrUpdateUser(downlineMember1)
            userDao.insertOrUpdateUser(downlineMember2)

            // Seed Referral Nodes
            referralDao.insertOrUpdateNode(
                ReferralNodeEntity(
                    userId = "usr_downline1",
                    name = "Sarah Connor",
                    email = "sarah.c@omnicontrol.com",
                    rank = UserRank.BUILDER,
                    sponsorId = "usr_member",
                    depthLevel = 1,
                    personalVolume = 550.0,
                    teamVolume = 1200.0,
                    directDownlineCount = 2
                )
            )

            referralDao.insertOrUpdateNode(
                ReferralNodeEntity(
                    userId = "usr_downline2",
                    name = "David Miller",
                    email = "david.m@omnicontrol.com",
                    rank = UserRank.ASSOCIATE,
                    sponsorId = "usr_member",
                    depthLevel = 1,
                    personalVolume = 150.0,
                    teamVolume = 150.0,
                    directDownlineCount = 0
                )
            )

            // Seed Initial KYC Document for David Miller
            kycDao.insertOrUpdateKyc(
                KycDocumentEntity(
                    userId = "usr_downline2",
                    fullName = "David Miller",
                    idNumber = "DL-98234-88A",
                    documentType = "Driver License",
                    documentFrontUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=600&q=80",
                    proofOfAddressUrl = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=600&q=80",
                    status = KycStatus.PENDING
                )
            )

            // Seed Initial Wallet Transactions
            walletDao.insertTransaction(
                WalletTransactionEntity(
                    id = "tx_001",
                    userId = "usr_member",
                    amount = 150.00,
                    type = TransactionType.COMMISSION_CREDIT,
                    status = TransactionStatus.COMPLETED,
                    description = "Direct Sale Commission from Order #OMNI-9021 (Sarah Connor)",
                    createdAt = System.currentTimeMillis() - 86400000L
                )
            )

            // Seed Initial Commission Records
            commissionDao.insertCommission(
                CommissionRecordEntity(
                    id = "comm_001",
                    recipientUserId = "usr_member",
                    recipientName = "Alex Mercer",
                    sourceOrderId = "ord_9021",
                    sourceBuyerName = "Sarah Connor",
                    sourceBv = 400.0,
                    commissionAmount = 60.00,
                    tierLevel = 1,
                    type = CommissionType.DIRECT_SALE,
                    isVerifiedProductSale = true
                )
            )
        }
    }

    // Process a new product order with full BV propagation and commission calculation
    suspend fun placeOrder(
        buyerUserId: String,
        cartItems: List<Pair<ProductEntity, Int>>,
        paymentMethod: String
    ): Result<OrderEntity> = withContext(Dispatchers.IO) {
        try {
            val buyer = userDao.getUserById(buyerUserId) ?: return@withContext Result.failure(Exception("User not found"))
            if (cartItems.isEmpty()) return@withContext Result.failure(Exception("Cart is empty"))

            var totalAmount = 0.0
            var totalBv = 0.0
            val orderItems = mutableListOf<OrderItem>()

            for ((product, qty) in cartItems) {
                totalAmount += product.price * qty
                totalBv += product.bvWeight * qty
                orderItems.add(
                    OrderItem(
                        productId = product.id,
                        productName = product.name,
                        unitPrice = product.price,
                        bvWeight = product.bvWeight,
                        quantity = qty
                    )
                )
            }

            // Deduct from wallet if paid via WALLET
            if (paymentMethod == "WALLET") {
                if (buyer.walletBalance < totalAmount) {
                    return@withContext Result.failure(Exception("Insufficient wallet balance. Available: $$${buyer.walletBalance}"))
                }
                val updatedWalletBalance = buyer.walletBalance - totalAmount
                userDao.insertOrUpdateUser(buyer.copy(walletBalance = updatedWalletBalance))
                
                walletDao.insertTransaction(
                    WalletTransactionEntity(
                        id = UUID.randomUUID().toString(),
                        userId = buyer.id,
                        amount = totalAmount,
                        type = TransactionType.ORDER_PAYMENT,
                        status = TransactionStatus.COMPLETED,
                        description = "Payment for Product Order"
                    )
                )
            }

            // Build Order JSON
            val jsonArray = JSONArray()
            for (item in orderItems) {
                val obj = JSONObject().apply {
                    put("productId", item.productId)
                    put("productName", item.productName)
                    put("unitPrice", item.unitPrice)
                    put("bvWeight", item.bvWeight)
                    put("quantity", item.quantity)
                }
                jsonArray.put(obj)
            }

            val orderId = "ord_" + UUID.randomUUID().toString().take(8)
            val orderNumber = "OMNI-" + (1000..9999).random()

            val newOrder = OrderEntity(
                id = orderId,
                orderNumber = orderNumber,
                buyerUserId = buyer.id,
                buyerName = buyer.fullName,
                totalAmount = totalAmount,
                totalBv = totalBv,
                status = OrderStatus.FULFILLED,
                itemsJson = jsonArray.toString(),
                paymentMethod = paymentMethod,
                fulfilledAt = System.currentTimeMillis()
            )

            orderDao.insertOrder(newOrder)

            // Update buyer's Personal Volume & Team Volume
            val newPv = buyer.personalVolume + totalBv
            val newTv = buyer.teamVolume + totalBv
            val newRank = CommissionRankEngine.evaluateNewRank(newTv, buyer.directDownlineCount)
            userDao.insertOrUpdateUser(buyer.copy(personalVolume = newPv, teamVolume = newTv, rank = newRank))

            // Build Upline Chain for Commissions (Sponsor -> Sponsor of Sponsor -> Tier 3)
            val uplineChain = mutableListOf<UserEntity>()
            var currentSponsorId = buyer.sponsorId
            var depthCount = 0

            while (currentSponsorId != null && depthCount < 3) {
                val sponsor = userDao.getUserById(currentSponsorId)
                if (sponsor != null) {
                    uplineChain.add(sponsor)
                    currentSponsorId = sponsor.sponsorId
                    depthCount++
                } else {
                    break
                }
            }

            // Execute Commission Engine
            val commissions = CommissionRankEngine.calculateOrderCommissions(
                orderId = orderId,
                buyerUserId = buyer.id,
                buyerName = buyer.fullName,
                orderBv = totalBv,
                uplineChain = uplineChain,
                isVerifiedProductSale = true
            )

            for (comm in commissions) {
                commissionDao.insertCommission(comm)

                // Credit Upline Wallet
                val recipient = userDao.getUserById(comm.recipientUserId)
                if (recipient != null) {
                    val newBalance = recipient.walletBalance + comm.commissionAmount
                    val updatedUplineTv = recipient.teamVolume + totalBv
                    val updatedRank = CommissionRankEngine.evaluateNewRank(updatedUplineTv, recipient.directDownlineCount)

                    userDao.insertOrUpdateUser(
                        recipient.copy(
                            walletBalance = newBalance,
                            teamVolume = updatedUplineTv,
                            rank = updatedRank
                        )
                    )

                    walletDao.insertTransaction(
                        WalletTransactionEntity(
                            id = UUID.randomUUID().toString(),
                            userId = recipient.id,
                            amount = comm.commissionAmount,
                            type = TransactionType.COMMISSION_CREDIT,
                            status = TransactionStatus.COMPLETED,
                            description = "Earned ${comm.type.name} from Order #${newOrder.orderNumber} ($${totalBv} BV)"
                        )
                    )
                }
            }

            Result.success(newOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Wallet Deposit
    suspend fun depositToWallet(userId: String, amount: Double): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("User not found"))
        val updatedUser = user.copy(walletBalance = user.walletBalance + amount)
        userDao.insertOrUpdateUser(updatedUser)

        walletDao.insertTransaction(
            WalletTransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                amount = amount,
                type = TransactionType.WALLET_DEPOSIT,
                status = TransactionStatus.COMPLETED,
                description = "Funds deposited via Payment Gateway"
            )
        )
        Result.success(Unit)
    }

    // Wallet Withdrawal Request
    suspend fun requestWithdrawal(userId: String, amount: Double): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.kycStatus != KycStatus.APPROVED) {
            return@withContext Result.failure(Exception("KYC approval required before requesting payouts."))
        }
        if (user.walletBalance < amount) {
            return@withContext Result.failure(Exception("Insufficient wallet balance."))
        }

        val updatedUser = user.copy(
            walletBalance = user.walletBalance - amount,
            totalPaidOut = user.totalPaidOut + amount
        )
        userDao.insertOrUpdateUser(updatedUser)

        walletDao.insertTransaction(
            WalletTransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                amount = amount,
                type = TransactionType.PAYOUT_DEBIT,
                status = TransactionStatus.COMPLETED,
                description = "Bank Wire Withdrawal Executed"
            )
        )
        Result.success(Unit)
    }

    // Submit KYC Document
    suspend fun submitKyc(
        userId: String,
        fullName: String,
        idNumber: String,
        documentType: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val kyc = KycDocumentEntity(
            userId = userId,
            fullName = fullName,
            idNumber = idNumber,
            documentType = documentType,
            documentFrontUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?auto=format&fit=crop&w=600&q=80",
            proofOfAddressUrl = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=600&q=80",
            status = KycStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )
        kycDao.insertOrUpdateKyc(kyc)

        val user = userDao.getUserById(userId)
        if (user != null) {
            userDao.insertOrUpdateUser(user.copy(kycStatus = KycStatus.PENDING))
        }
        Result.success(Unit)
    }

    // Admin Review KYC
    suspend fun reviewKyc(userId: String, approve: Boolean, adminNotes: String): Result<Unit> = withContext(Dispatchers.IO) {
        val newStatus = if (approve) KycStatus.APPROVED else KycStatus.REJECTED
        val existingKyc = kycDao.getKycByUserId(userId).firstOrNull()
        if (existingKyc != null) {
            kycDao.insertOrUpdateKyc(
                existingKyc.copy(
                    status = newStatus,
                    adminNotes = adminNotes,
                    reviewedAt = System.currentTimeMillis()
                )
            )
        }

        val user = userDao.getUserById(userId)
        if (user != null) {
            userDao.insertOrUpdateUser(user.copy(kycStatus = newStatus))
        }
        Result.success(Unit)
    }
}
