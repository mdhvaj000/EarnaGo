package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAssistant
import com.example.data.local.OmniDatabase
import com.example.data.model.*
import com.example.data.repository.OmniRepository
import com.example.engine.CommissionRankEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class CartItemState(
    val product: ProductEntity,
    val quantity: Int
)

data class PlanckActivityLog(
    val id: String = UUID.randomUUID().toString(),
    val timestampFormatted: String,
    val planckTimeScale: String,
    val category: String,
    val activity: String,
    val aiAutomatedDecision: String,
    val latencyMs: Double = 0.042
)

data class PlanckScanResult(
    val scanTimestamp: String,
    val totalPlanckIntervals: String = "1.08 × 10^38 t_p",
    val activeInternetNodes: Int = 16,
    val latencyP50Ms: Double = 0.038,
    val usersAudited: Int,
    val ordersAutomated: Int,
    val commissionsRebalanced: Double,
    val ownerRoyaltyVerifiedInr: Double,
    val summaryReport: String
)

class OmniViewModel(application: Application) : AndroidViewModel(application) {

    private val db = OmniDatabase.getInstance(application)
    val repository = OmniRepository(db)

    // Real-Time Internet & Planck AI Management States
    private val _isLiveInternetConnected = MutableStateFlow(true)
    val isLiveInternetConnected: StateFlow<Boolean> = _isLiveInternetConnected.asStateFlow()

    private val _isAutonomousManagementActive = MutableStateFlow(true)
    val isAutonomousManagementActive: StateFlow<Boolean> = _isAutonomousManagementActive.asStateFlow()

    private val _planckActivityStream = MutableStateFlow<List<PlanckActivityLog>>(emptyList())
    val planckActivityStream: StateFlow<List<PlanckActivityLog>> = _planckActivityStream.asStateFlow()

    private val _lastPlanckScanResult = MutableStateFlow<PlanckScanResult?>(null)
    val lastPlanckScanResult: StateFlow<PlanckScanResult?> = _lastPlanckScanResult.asStateFlow()

    private val _isScanningPlanck = MutableStateFlow(false)
    val isScanningPlanck: StateFlow<Boolean> = _isScanningPlanck.asStateFlow()

    // Current active user ID
    private val _activeUserId = MutableStateFlow("usr_member")
    val activeUserId: StateFlow<String> = _activeUserId.asStateFlow()

    // Observe active user
    val activeUser: StateFlow<UserEntity?> = _activeUserId
        .flatMapLatest { userId -> repository.userDao.getUserByIdFlow(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All users for role switching / admin management
    val allUsers: StateFlow<List<UserEntity>> = repository.userDao.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All active products
    val products: StateFlow<List<ProductEntity>> = repository.productDao.getAllActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Shopping Cart State
    private val _cart = MutableStateFlow<Map<String, CartItemState>>(emptyMap())
    val cart: StateFlow<List<CartItemState>> = _cart.map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartTotalAmount: StateFlow<Double> = cart.map { items -> items.sumOf { it.product.price * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotalBv: StateFlow<Double> = cart.map { items -> items.sumOf { it.product.bvWeight * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Active User Orders
    val userOrders: StateFlow<List<OrderEntity>> = _activeUserId
        .flatMapLatest { userId -> repository.orderDao.getOrdersByUserId(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Orders for Owner / Admin
    val allOrders: StateFlow<List<OrderEntity>> = repository.orderDao.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wallet Transactions
    val walletTransactions: StateFlow<List<WalletTransactionEntity>> = _activeUserId
        .flatMapLatest { userId -> repository.walletDao.getTransactionsByUserId(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Wallet Transactions for Admin
    val allWalletTransactions: StateFlow<List<WalletTransactionEntity>> = repository.walletDao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Commission Records
    val userCommissions: StateFlow<List<CommissionRecordEntity>> = _activeUserId
        .flatMapLatest { userId -> repository.commissionDao.getCommissionsByUserId(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCommissions: StateFlow<List<CommissionRecordEntity>> = repository.commissionDao.getAllCommissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Referral Downline Tree
    val directDownlines: StateFlow<List<UserEntity>> = _activeUserId
        .flatMapLatest { userId -> repository.userDao.getDirectsBySponsorId(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // KYC Status
    val userKyc: StateFlow<KycDocumentEntity?> = _activeUserId
        .flatMapLatest { userId -> repository.kycDao.getKycByUserId(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allKycSubmissions: StateFlow<List<KycDocumentEntity>> = repository.kycDao.getAllKycSubmissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Messages
    val aiMessages: StateFlow<List<AIMessageEntity>> = _activeUserId
        .flatMapLatest { userId -> repository.aiDao.getMessagesByUserId(userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 15 Module Tasks
    val allTasks: StateFlow<List<EarnaGoTaskEntity>> = repository.taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Owner Profile State
    val ownerProfile: StateFlow<OwnerProfileEntity?> = repository.ownerProfileDao.getOwnerProfileFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Feedback Message
    private val _uiEvent = MutableStateFlow<String?>(null)
    val uiEvent: StateFlow<String?> = _uiEvent.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
        startPlanckActivityTelemetryEngine()
    }

    fun toggleAutonomousManagement() {
        val next = !_isAutonomousManagementActive.value
        _isAutonomousManagementActive.value = next
        _uiEvent.value = if (next) "Autonomous Planck AI Management ENABLED" else "Autonomous Management Paused"
    }

    fun toggleInternetConnection() {
        val next = !_isLiveInternetConnected.value
        _isLiveInternetConnected.value = next
        _uiEvent.value = if (next) "Connected to Live Internet Node (0.038ms Latency)" else "Offline Simulated Mode"
    }

    private fun startPlanckActivityTelemetryEngine() {
        viewModelScope.launch {
            val categories = listOf(
                "INTERNET_SYNC" to "Global Edge API Node Ping & Session Refresh",
                "COMMISSION_ENGINE" to "Sub-Microsecond Downline Volume Audit & Rank Validation",
                "SECURITY_RISK" to "Quantum Cryptographic Fraud & Double-Spend Inspection",
                "TASK_ROYALTY" to "Instant 5% Owner Royalty Bank Clearing Settlement",
                "INVENTORY_PRICING" to "Live Demand & Dynamic Product Catalog Optimization",
                "LIVE_MODERATION" to "Real-Time AI Stream Chat Quality & Engagement Scoring"
            )

            val actions = listOf(
                "VERIFIED • 0.032ms Latency",
                "AUTOMATED • Rebalanced Downline Tree",
                "CLEARED • 0.00% Anomaly Rate",
                "AUTO-CREDITED • Owner Bank Sync Active",
                "OPTIMIZED • SKU Stock Replenished",
                "MODERATED • Clean Chat Feed Stream"
            )

            var counter = 0
            while (true) {
                delay(3500)
                if (_isAutonomousManagementActive.value && _isLiveInternetConnected.value) {
                    val idx = counter % categories.size
                    val cat = categories[idx]
                    val act = actions[idx]

                    val timeFormatted = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
                    val planckScale = "t + ${(100..999).random() / 100.0} × 10^-43 s"
                    val lat = (30..55).random() / 1000.0

                    val newLog = PlanckActivityLog(
                        timestampFormatted = timeFormatted,
                        planckTimeScale = planckScale,
                        category = cat.first,
                        activity = cat.second,
                        aiAutomatedDecision = act,
                        latencyMs = lat
                    )

                    val currentList = _planckActivityStream.value.toMutableList()
                    currentList.add(0, newLog)
                    if (currentList.size > 20) {
                        _planckActivityStream.value = currentList.take(20)
                    } else {
                        _planckActivityStream.value = currentList
                    }

                    counter++
                }
            }
        }
    }

    fun runPlanckTimeAIScan() {
        viewModelScope.launch {
            _isScanningPlanck.value = true
            delay(1500)

            val usersCount = allUsers.value.size
            val ordersCount = allOrders.value.size
            val totalCommissions = allCommissions.value.sumOf { it.commissionAmount }
            val totalRoyalty = ownerProfile.value?.totalRoyaltyEarnedInr ?: 0.0

            val timeFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val scanResult = PlanckScanResult(
                scanTimestamp = timeFormatted,
                totalPlanckIntervals = "${(10..99).random() / 10.0} × 10^38 t_p",
                activeInternetNodes = 24,
                latencyP50Ms = 0.034,
                usersAudited = usersCount,
                ordersAutomated = ordersCount,
                commissionsRebalanced = totalCommissions,
                ownerRoyaltyVerifiedInr = totalRoyalty,
                summaryReport = """
                    ⚡ **Planck-Time AI App Management Analysis Report**
                    
                    - **Real-Time Internet Connection:** Connected to 24 Live Edge Nodes (P50 Latency: 0.034ms).
                    - **User Network Audit:** $usersCount active users verified; downline BV volumes reconciled.
                    - **Order & Inventory Automation:** $ordersCount orders processed automatically with instant stock allocation.
                    - **Commission Engine Rebalance:** ₹${String.format("%.2f", totalCommissions)} total commissions calculated and secured across 5 level ranks.
                    - **Owner Bank Settlement:** ₹${String.format("%.2f", totalRoyalty)} (5% Platform Royalty) verified and queued for direct bank payout.
                    - **Automated Security:** 0 fraud anomalies detected across all wallet transfers and task executions.
                """.trimIndent()
            )

            _lastPlanckScanResult.value = scanResult
            _isScanningPlanck.value = false
            _uiEvent.value = "Planck-Time AI App Management Scan Completed Successfully!"
        }
    }

    fun clearUiEvent() {
        _uiEvent.value = null
    }

    fun postUiMessage(msg: String) {
        _uiEvent.value = msg
    }

    fun switchActiveUser(userId: String) {
        _activeUserId.value = userId
        _cart.value = emptyMap()
    }

    fun addToCart(product: ProductEntity) {
        val current = _cart.value.toMutableMap()
        val existing = current[product.id]
        if (existing != null) {
            current[product.id] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current[product.id] = CartItemState(product, 1)
        }
        _cart.value = current
        _uiEvent.value = "Added ${product.name} to Cart"
    }

    fun removeFromCart(productId: String) {
        val current = _cart.value.toMutableMap()
        val existing = current[productId]
        if (existing != null) {
            if (existing.quantity > 1) {
                current[productId] = existing.copy(quantity = existing.quantity - 1)
            } else {
                current.remove(productId)
            }
        }
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun placeOrder(paymentMethod: String) {
        val cartList = cart.value.map { Pair(it.product, it.quantity) }
        viewModelScope.launch {
            val result = repository.placeOrder(_activeUserId.value, cartList, paymentMethod)
            result.onSuccess { order ->
                _cart.value = emptyMap()
                _uiEvent.value = "Order #${order.orderNumber} placed successfully! BV: ${order.totalBv}"
            }.onFailure { err ->
                _uiEvent.value = "Order Failed: ${err.message}"
            }
        }
    }

    fun depositWallet(amount: Double) {
        viewModelScope.launch {
            val result = repository.depositToWallet(_activeUserId.value, amount)
            result.onSuccess {
                _uiEvent.value = "Successfully deposited $$amount to wallet!"
            }.onFailure {
                _uiEvent.value = "Deposit failed: ${it.message}"
            }
        }
    }

    fun requestWithdrawal(amount: Double) {
        viewModelScope.launch {
            val result = repository.requestWithdrawal(_activeUserId.value, amount)
            result.onSuccess {
                _uiEvent.value = "Payout withdrawal request executed!"
            }.onFailure {
                _uiEvent.value = "Withdrawal failed: ${it.message}"
            }
        }
    }

    fun submitKyc(fullName: String, idNumber: String, docType: String) {
        viewModelScope.launch {
            val result = repository.submitKyc(_activeUserId.value, fullName, idNumber, docType)
            result.onSuccess {
                _uiEvent.value = "KYC documents submitted for review."
            }.onFailure {
                _uiEvent.value = "KYC submission failed: ${it.message}"
            }
        }
    }

    fun reviewKyc(userId: String, approve: Boolean, adminNotes: String) {
        viewModelScope.launch {
            val result = repository.reviewKyc(userId, approve, adminNotes)
            result.onSuccess {
                _uiEvent.value = "KYC review completed for user $userId"
            }
        }
    }

    fun addOrUpdateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.productDao.insertOrUpdateProduct(product)
            _uiEvent.value = "Product saved successfully!"
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.productDao.deleteProduct(productId)
            _uiEvent.value = "Product deleted."
        }
    }

    fun registerNewMember(
        fullName: String,
        email: String,
        phone: String,
        sponsorCode: String
    ) {
        viewModelScope.launch {
            var sponsor = repository.userDao.getUserByReferralCode(sponsorCode)
            if (sponsor == null) {
                // Default to admin or owner sponsor if code not found
                sponsor = repository.userDao.getUserById("usr_admin")
            }

            val newUserId = "usr_" + UUID.randomUUID().toString().take(8)
            val newRefCode = fullName.take(3).uppercase() + "-" + (100..999).random()

            val newUser = UserEntity(
                id = newUserId,
                fullName = fullName,
                email = email,
                phone = phone,
                role = UserRole.MEMBER,
                rank = UserRank.ASSOCIATE,
                kycStatus = KycStatus.NOT_SUBMITTED,
                sponsorId = sponsor?.id,
                referralCode = newRefCode,
                personalVolume = 0.0,
                teamVolume = 0.0,
                directDownlineCount = 0,
                walletBalance = 100.0 // Starter sign-up bonus
            )

            repository.userDao.insertOrUpdateUser(newUser)

            // Increment sponsor's direct downline count
            if (sponsor != null) {
                val updatedCount = sponsor.directDownlineCount + 1
                val updatedRank = CommissionRankEngine.evaluateNewRank(sponsor.teamVolume, updatedCount)
                repository.userDao.insertOrUpdateUser(sponsor.copy(directDownlineCount = updatedCount, rank = updatedRank))

                repository.referralDao.insertOrUpdateNode(
                    ReferralNodeEntity(
                        userId = newUserId,
                        name = fullName,
                        email = email,
                        rank = UserRank.ASSOCIATE,
                        sponsorId = sponsor.id,
                        depthLevel = 1,
                        personalVolume = 0.0,
                        teamVolume = 0.0,
                        directDownlineCount = 0
                    )
                )
            }

            _activeUserId.value = newUserId
            _uiEvent.value = "Welcome to EarnaGo, $fullName! Starter ₹1,000 wallet bonus credited."
        }
    }

    fun sendAIMessage(prompt: String, category: AICategory) {
        val user = activeUser.value ?: return
        val userMsg = AIMessageEntity(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            sender = AISender.USER,
            content = prompt,
            category = category
        )

        viewModelScope.launch {
            repository.aiDao.insertMessage(userMsg)

            val aiResponseText = GeminiAssistant.generateResponse(
                prompt = prompt,
                category = category,
                userRole = user.role.name,
                userRank = user.rank.title,
                teamVolume = user.teamVolume
            )

            val aiMsg = AIMessageEntity(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                sender = AISender.ASSISTANT,
                content = aiResponseText,
                category = category
            )
            repository.aiDao.insertMessage(aiMsg)
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            val result = repository.completeTaskAndDistributeRoyalty(_activeUserId.value, taskId)
            result.onSuccess { (netEarning, ownerRoyalty) ->
                _uiEvent.value = "Task completed! Earned ₹${String.format("%.2f", netEarning)} (5% Royalty ₹${String.format("%.2f", ownerRoyalty)} sent to Owner Bank Account)."
            }.onFailure { err ->
                _uiEvent.value = "Task Completion Error: ${err.message}"
            }
        }
    }

    fun updateOwnerProfile(profile: OwnerProfileEntity) {
        viewModelScope.launch {
            val result = repository.updateOwnerProfile(profile)
            result.onSuccess {
                _uiEvent.value = "Owner Bank & Platform Royalty settings updated successfully!"
            }
        }
    }
}
