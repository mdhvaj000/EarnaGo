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
    val taskDao = db.taskDao()
    val ownerProfileDao = db.ownerProfileDao()

    // Pre-populate sample seed data if database is empty
    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        // Seed Owner Profile
        val existingOwner = ownerProfileDao.getOwnerProfile()
        if (existingOwner == null) {
            ownerProfileDao.insertOrUpdateOwnerProfile(OwnerProfileEntity())
        }

        // Seed Initial 15 Module Tasks
        val existingTasks = taskDao.getAllTasks().firstOrNull()
        if (existingTasks.isNullOrEmpty()) {
            val sampleTasks = listOf(
                EarnaGoTaskEntity(
                    id = "task_01",
                    module = EarnaGoModuleCategory.AFFILIATE_MARKETING,
                    title = "Amazon Associates Electronics Promotion",
                    description = "Share Amazon affiliate deal links for smart noise-canceling headphones with your network.",
                    partnerName = "Amazon India Associates",
                    rewardAmount = 450.0,
                    commissionPct = 8.5,
                    requirements = "Generate at least 3 valid buyer clicks or 1 order",
                    estimatedTime = "15 mins",
                    difficulty = "Easy",
                    bvValue = 100.0,
                    actionUrl = "https://amazon.in/affiliate"
                ),
                EarnaGoTaskEntity(
                    id = "task_02",
                    module = EarnaGoModuleCategory.DIGITAL_PRODUCTS,
                    title = "Canva Social Media Template Suite",
                    description = "Promote & sell the 200+ Instagram & LinkedIn high-converting canvas template bundle.",
                    partnerName = "EarnaGo Digital Hub",
                    rewardAmount = 850.0,
                    commissionPct = 25.0,
                    requirements = "Direct sale of 1 digital template pack",
                    estimatedTime = "30 mins",
                    difficulty = "Easy",
                    bvValue = 350.0
                ),
                EarnaGoTaskEntity(
                    id = "task_03",
                    module = EarnaGoModuleCategory.AI_SERVICES,
                    title = "AI Corporate Logo & Voiceover Scripting",
                    description = "Use EarnaGo AI tools to generate brand logos, social graphics, and Hindi AI voiceovers for client business.",
                    partnerName = "EarnaGo AI Studio",
                    rewardAmount = 1200.0,
                    requirements = "Submit 3 generated logo assets and MP3 audio link",
                    estimatedTime = "45 mins",
                    difficulty = "Medium",
                    bvValue = 500.0
                ),
                EarnaGoTaskEntity(
                    id = "task_04",
                    module = EarnaGoModuleCategory.FREELANCE_GIGS,
                    title = "Android App UI Jetpack Compose Refactoring",
                    description = "Help local MSME client fix responsive layout spacing and dark theme styling in Jetpack Compose.",
                    partnerName = "TechGig India",
                    rewardAmount = 2500.0,
                    requirements = "Provide clean Kotlin code snippet or pull request",
                    estimatedTime = "2 hours",
                    difficulty = "Advanced",
                    bvValue = 1000.0
                ),
                EarnaGoTaskEntity(
                    id = "task_05",
                    module = EarnaGoModuleCategory.ONLINE_LEARNING,
                    title = "Direct Selling Legal Rules & Ethics Masterclass",
                    description = "Complete the 40-minute certification course on Consumer Protection Direct Selling Rules 2021.",
                    partnerName = "EarnaGo Academy",
                    rewardAmount = 600.0,
                    requirements = "Score 80%+ in final compliance quiz",
                    estimatedTime = "40 mins",
                    difficulty = "Easy",
                    bvValue = 250.0
                ),
                EarnaGoTaskEntity(
                    id = "task_06",
                    module = EarnaGoModuleCategory.REFERRAL_NETWORK,
                    title = "Sponsor Tier 1 Direct Downline Mission",
                    description = "Share your unique referral link to enroll new active business partners into your network tree.",
                    partnerName = "EarnaGo Direct MLM Network",
                    rewardAmount = 1000.0,
                    requirements = "1 verified member registration with starter product activation",
                    estimatedTime = "1 hour",
                    difficulty = "Medium",
                    bvValue = 800.0
                ),
                EarnaGoTaskEntity(
                    id = "task_07",
                    module = EarnaGoModuleCategory.CASHBACK_COUPONS,
                    title = "Flipkart Festival Shopping Cashback Campaign",
                    description = "Share exclusive 15% discount coupons for home appliances and earn instant cashback credit.",
                    partnerName = "Flipkart Affiliate / EarnKaro",
                    rewardAmount = 350.0,
                    requirements = "Coupon redemption by 2 unique shoppers",
                    estimatedTime = "20 mins",
                    difficulty = "Easy",
                    bvValue = 120.0
                ),
                EarnaGoTaskEntity(
                    id = "task_08",
                    module = EarnaGoModuleCategory.MICRO_TASKS_SURVEYS,
                    title = "Fintech Mobile Banking Usability Audit",
                    description = "Answer 12 survey questions and evaluate UPI payment flow speed on mobile browsers.",
                    partnerName = "SurveyMonkey / UserTesting",
                    rewardAmount = 300.0,
                    requirements = "Complete survey form with accurate feedback",
                    estimatedTime = "15 mins",
                    difficulty = "Easy",
                    bvValue = 80.0
                ),
                EarnaGoTaskEntity(
                    id = "task_09",
                    module = EarnaGoModuleCategory.LOCAL_BUSINESS,
                    title = "Local City Restaurant & Gym Onboarding",
                    description = "Visit or contact local shops in your city to list their services on EarnaGo Local Directory.",
                    partnerName = "EarnaGo City Hyperlocal",
                    rewardAmount = 1500.0,
                    requirements = "Upload shop details, UPI VPA, and owner consent photo",
                    estimatedTime = "1.5 hours",
                    difficulty = "Medium",
                    bvValue = 600.0
                ),
                EarnaGoTaskEntity(
                    id = "task_10",
                    module = EarnaGoModuleCategory.DIGITAL_UTILITIES,
                    title = "FASTag Recharge & Electricity Bill Pay Deal",
                    description = "Pay monthly electricity bills or FASTag recharges for team members or clients.",
                    partnerName = "Bharat BillPay (BBPS)",
                    rewardAmount = 180.0,
                    requirements = "Complete transaction above ₹500",
                    estimatedTime = "5 mins",
                    difficulty = "Easy",
                    bvValue = 50.0
                ),
                EarnaGoTaskEntity(
                    id = "task_11",
                    module = EarnaGoModuleCategory.DROPSHIPPING,
                    title = "Zero-Inventory AMOLED Smartwatch Sale",
                    description = "Sell zero-inventory smartwatch directly from verified suppliers to customers across India.",
                    partnerName = "Shiprocket / Supplier Direct",
                    rewardAmount = 950.0,
                    requirements = "Submit customer delivery address and prepaid order",
                    estimatedTime = "30 mins",
                    difficulty = "Medium",
                    bvValue = 400.0
                ),
                EarnaGoTaskEntity(
                    id = "task_12",
                    module = EarnaGoModuleCategory.CREATOR_ECONOMY,
                    title = "YouTube Shorts & Instagram Reel Monetization",
                    description = "Post an unboxing reel or YouTube Short highlighting EarnaGo Digital Business Suite.",
                    partnerName = "Creator Studio India",
                    rewardAmount = 800.0,
                    requirements = "Minimum 500 views or 50 engagement likes",
                    estimatedTime = "1 hour",
                    difficulty = "Medium",
                    bvValue = 300.0
                ),
                EarnaGoTaskEntity(
                    id = "task_13",
                    module = EarnaGoModuleCategory.RESELLING_COMMERCE,
                    title = "Wholesale Kurti & Festive Wear Margin Reselling",
                    description = "Add your own retail profit margin to wholesale ethnic wear catalogs and share via WhatsApp.",
                    partnerName = "EarnaGo Reseller Hub",
                    rewardAmount = 1100.0,
                    requirements = "1 confirmed retail customer delivery order",
                    estimatedTime = "45 mins",
                    difficulty = "Easy",
                    bvValue = 450.0
                ),
                EarnaGoTaskEntity(
                    id = "task_14",
                    module = EarnaGoModuleCategory.JOB_GIG_MARKETPLACE,
                    title = "Remote Data Entry & CRM Lead Clean-up Gig",
                    description = "Categorize and format 50 prospective business leads into structured CSV format.",
                    partnerName = "WorkFromHome India",
                    rewardAmount = 1800.0,
                    requirements = "Submit verified Excel/CSV spreadsheet",
                    estimatedTime = "2 hours",
                    difficulty = "Medium",
                    bvValue = 700.0
                ),
                EarnaGoTaskEntity(
                    id = "task_15",
                    module = EarnaGoModuleCategory.B2B_WHOLESALE,
                    title = "B2B Solar Micro-Inverter Wholesale Procurement",
                    description = "Connect commercial solar contractors with factory-direct solar inverter manufacturers.",
                    partnerName = "Indiamart / B2B Connect",
                    rewardAmount = 3500.0,
                    requirements = "Successful B2B buyer verification & purchase intent quote",
                    estimatedTime = "3 hours",
                    difficulty = "Advanced",
                    bvValue = 1500.0
                )
            )
            taskDao.insertAllTasks(sampleTasks)
        }
        val existingProducts = productDao.getAllActiveProducts().firstOrNull()
        if (existingProducts.isNullOrEmpty()) {
            // Seed Default Products (Indian Market Environment)
            val products = listOf(
                ProductEntity(
                    id = "prod_001",
                    sku = "EARN-DIG-IN",
                    name = "EarnaGo Digital Business & AI Suite (1 Year)",
                    description = "Complete digital marketing OS, funnel builder, WhatsApp lead generator, and AI business coach.",
                    price = 9999.00,
                    bvWeight = 800.0,
                    pvWeight = 800.0,
                    category = ProductCategory.DIGITAL,
                    stockQuantity = 999,
                    imageUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=600&q=80",
                    isFeatured = true
                ),
                ProductEntity(
                    id = "prod_002",
                    sku = "OMNI-AYUR-01",
                    name = "OmniAyur Bio-Health & Focus Kit (3 Month Supply)",
                    description = "Certified Ayurvedic wellness boosters, organic ashwagandha & herbal vitality supplements.",
                    price = 4999.00,
                    bvWeight = 350.0,
                    pvWeight = 350.0,
                    category = ProductCategory.STARTER_KIT,
                    stockQuantity = 450,
                    imageUrl = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?auto=format&fit=crop&w=600&q=80",
                    isFeatured = true
                ),
                ProductEntity(
                    id = "prod_003",
                    sku = "OMNI-ORG-01",
                    name = "OmniSpices & Organic Tea Ambassador Box",
                    description = "Premium export-quality Darjeeling green tea, organic turmeric, and spice gift hamper.",
                    price = 2499.00,
                    bvWeight = 180.0,
                    pvWeight = 180.0,
                    category = ProductCategory.PHYSICAL,
                    stockQuantity = 600,
                    imageUrl = "https://images.unsplash.com/photo-1576092768241-dec231879fc3?auto=format&fit=crop&w=600&q=80",
                    isFeatured = false
                ),
                ProductEntity(
                    id = "prod_004",
                    sku = "OMNI-SOLAR-01",
                    name = "OmniSolar Micro-Inverter & Emergency Energy Kit",
                    description = "Portable solar power kit with 50W foldable panel and fast USB-C charging station.",
                    price = 18500.00,
                    bvWeight = 1500.0,
                    pvWeight = 1500.0,
                    category = ProductCategory.SERVICE,
                    stockQuantity = 120,
                    imageUrl = "https://images.unsplash.com/photo-1508514177221-188b1cf16e9d?auto=format&fit=crop&w=600&q=80",
                    isFeatured = true
                )
            )
            productDao.insertAllProducts(products)

            // Seed Sample Hierarchy & Users (Indian Environment Context)
            val ownerUser = UserEntity(
                id = "usr_owner",
                fullName = "Vikramaditya Singhania (Owner)",
                email = "owner@omnicontrol.in",
                phone = "+91 98110 01100",
                role = UserRole.OWNER,
                rank = UserRank.AMBASSADOR,
                kycStatus = KycStatus.APPROVED,
                sponsorId = null,
                referralCode = "OMNI-OWNER",
                personalVolume = 2500.0,
                teamVolume = 125000.0,
                directDownlineCount = 12,
                walletBalance = 154200.50,
                totalPaidOut = 840000.0
            )

            val adminUser = UserEntity(
                id = "usr_admin",
                fullName = "Rajesh Sharma (Admin)",
                email = "admin@omnicontrol.in",
                phone = "+91 98220 02200",
                role = UserRole.ADMIN,
                rank = UserRank.DIRECTOR,
                kycStatus = KycStatus.APPROVED,
                sponsorId = "usr_owner",
                referralCode = "OMNI-ADMIN",
                personalVolume = 1200.0,
                teamVolume = 45000.0,
                directDownlineCount = 8,
                walletBalance = 48500.00,
                totalPaidOut = 220000.0
            )

            val memberUser = UserEntity(
                id = "usr_member",
                fullName = "Rohan Verma (Executive Member)",
                email = "rohan.verma@omnicontrol.in",
                phone = "+91 98765 43210",
                role = UserRole.MEMBER,
                rank = UserRank.EXECUTIVE,
                kycStatus = KycStatus.APPROVED,
                sponsorId = "usr_admin",
                referralCode = "ROHAN-V88",
                personalVolume = 650.0,
                teamVolume = 3200.0,
                directDownlineCount = 4,
                walletBalance = 12800.40,
                pendingCommission = 3400.00,
                totalPaidOut = 45000.00
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

    // Complete Task from 15 Modules with Auto 5% Owner Royalty Deducted
    suspend fun completeTaskAndDistributeRoyalty(userId: String, taskId: String): Result<Pair<Double, Double>> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("Member not found"))
        val tasks = taskDao.getAllTasks().firstOrNull() ?: emptyList()
        val task = tasks.find { it.id == taskId } ?: return@withContext Result.failure(Exception("Task not found"))

        if (task.isCompleted) {
            return@withContext Result.failure(Exception("Task already completed."))
        }

        // 1. Fetch Owner Profile for Royalty Rate
        val ownerProfile = ownerProfileDao.getOwnerProfile() ?: OwnerProfileEntity()
        val royaltyRate = ownerProfile.platformRoyaltyPct / 100.0 // 5.0% -> 0.05
        val grossReward = task.rewardAmount
        val ownerRoyaltyAmount = grossReward * royaltyRate
        val memberNetEarnings = grossReward - ownerRoyaltyAmount

        // 2. Mark Task Completed
        taskDao.markTaskCompleted(taskId)

        // 3. Credit Member Wallet with 95% Net Earnings
        val updatedMember = user.copy(
            walletBalance = user.walletBalance + memberNetEarnings,
            teamVolume = user.teamVolume + task.bvValue,
            personalVolume = user.personalVolume + task.bvValue
        )
        userDao.insertOrUpdateUser(updatedMember)

        walletDao.insertTransaction(
            WalletTransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                amount = memberNetEarnings,
                type = TransactionType.TASK_COMPLETION_EARNING,
                status = TransactionStatus.COMPLETED,
                description = "Earned from ${task.module.displayName}: ${task.title} (₹${String.format("%.2f", grossReward)} gross minus 5% owner royalty)"
            )
        )

        // 4. Credit Owner Royalty Ledger & Owner Wallet (5%)
        ownerProfileDao.addRoyaltyEarnings(ownerRoyaltyAmount)

        val ownerUser = userDao.getUserById("usr_owner")
        if (ownerUser != null) {
            val updatedOwnerUser = ownerUser.copy(walletBalance = ownerUser.walletBalance + ownerRoyaltyAmount)
            userDao.insertOrUpdateUser(updatedOwnerUser)

            walletDao.insertTransaction(
                WalletTransactionEntity(
                    id = UUID.randomUUID().toString(),
                    userId = ownerUser.id,
                    amount = ownerRoyaltyAmount,
                    type = TransactionType.PLATFORM_ROYALTY_DEDUCTION,
                    status = TransactionStatus.COMPLETED,
                    description = "5% Platform Royalty from Member ${user.fullName} (${task.module.displayName})"
                )
            )
        }

        Result.success(Pair(memberNetEarnings, ownerRoyaltyAmount))
    }

    // Update Owner Profile Bank Details & Royalty Config
    suspend fun updateOwnerProfile(profile: OwnerProfileEntity): Result<Unit> = withContext(Dispatchers.IO) {
        ownerProfileDao.insertOrUpdateOwnerProfile(profile)
        Result.success(Unit)
    }
}
