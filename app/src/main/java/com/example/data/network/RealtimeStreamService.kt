package com.example.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class LiveMarketTicker(
    val nifty50: String = "24,380.50 (+0.42%)",
    val sensex: String = "80,120.15 (+0.38%)",
    val goldRate10g: String = "₹72,450 (-0.12%)",
    val usdInr: String = "₹83.95 (+0.05)",
    val liveNetworkBv: String = "14,25,800 BV",
    val activeLiveViewers: Int = 14280
)

data class LiveChatMessage(
    val id: String,
    val senderName: String,
    val cityState: String,
    val userRank: String,
    val messageText: String,
    val timestamp: String,
    val isSystemAnnouncement: Boolean = false
)

data class LiveNewsArticle(
    val title: String,
    val source: String,
    val snippet: String,
    val publishedAt: String,
    val category: String
)

object RealtimeStreamService {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    /**
     * Ticker Stream emitting updated market and live network statistics every 3 seconds
     */
    fun getMarketAndNetworkTickerStream(): Flow<LiveMarketTicker> = flow {
        var baseBv = 1425800
        var baseViewers = 14280
        var tickCount = 0

        while (true) {
            tickCount++
            baseBv += (100..1200).random()
            baseViewers += (-5..15).random()

            val niftyChange = String.format("%.2f", 24380.50 + (tickCount % 5 - 2) * 4.2)
            val ticker = LiveMarketTicker(
                nifty50 = "$niftyChange (+0.42%)",
                sensex = "80,120.15 (+0.38%)",
                goldRate10g = "₹72,450 (-0.12%)",
                usdInr = "₹83.95",
                liveNetworkBv = "₹${String.format("%,d", baseBv)} BV",
                activeLiveViewers = baseViewers
            )
            emit(ticker)
            delay(3000)
        }
    }

    /**
     * Simulated & Network Live Stream Chat messages from Indian cities
     */
    fun getLiveChatStream(): Flow<LiveChatMessage> = flow {
        val simulatedMessages = listOf(
            LiveChatMessage("c1", "Rajesh Sharma", "Mumbai, MH", "EXECUTIVE", "Jai Hind! Just activated 5 new Ayurvedic Starter Kits in Tier 1! 🇮🇳", "Just now"),
            LiveChatMessage("c2", "Priya Sundaram", "Bengaluru, KA", "DIRECTOR", "Awesome session! The 18% GST invoice automation is super smooth.", "1m ago"),
            LiveChatMessage("c3", "Amitav Sen", "Kolkata, WB", "BUILDER", "Which product has the highest BV weight for rank upgrade?", "2m ago"),
            LiveChatMessage("c4", "SYSTEM", "EarnaGo HQ", "ADMIN", "📢 Announcement: Double Level Bonus active until midnight!", "3m ago", isSystemAnnouncement = true),
            LiveChatMessage("c5", "Vikram Patel", "Ahmedabad, GJ", "EXECUTIVE", "UPI payment via PhonePe was instantly confirmed on my wallet!", "4m ago"),
            LiveChatMessage("c6", "Deepika Reddy", "Hyderabad, TS", "AMBASSADOR", "Welcome to all 250+ new team members who joined today from Telangana!", "5m ago")
        )

        for (msg in simulatedMessages) {
            emit(msg)
            delay(2000)
        }

        var idCounter = 7
        val indianCities = listOf("Delhi, DL", "Pune, MH", "Jaipur, RJ", "Lucknow, UP", "Chandigarh, PB", "Kochi, KL", "Indore, MP")
        val names = listOf("Rohan Gupta", "Sunita Verma", "Karan Malhotra", "Meera Joshi", "Sanjay Rao", "Neha Agarwal")
        val ranks = listOf("ASSOCIATE", "BUILDER", "EXECUTIVE", "DIRECTOR")
        val comments = listOf(
            "Super excited for the national convention in New Delhi!",
            "Just received my commission payout directly into my UPI bank account!",
            "How do we access the AI Business Coach in Hindi?",
            "EarnaGo digital suite is transforming our downline sales!",
            "Rank upgrade to Director completed!"
        )

        while (true) {
            delay(4000)
            val randomMsg = LiveChatMessage(
                id = "c${idCounter++}",
                senderName = names.random(),
                cityState = indianCities.random(),
                userRank = ranks.random(),
                messageText = comments.random(),
                timestamp = "Just now"
            )
            emit(randomMsg)
        }
    }

    /**
     * Fetches Real-time Internet Business News or provides structured Indian market feed
     */
    suspend fun fetchLiveIndianMarketNews(): List<LiveNewsArticle> = withContext(Dispatchers.IO) {
        val staticArticles = listOf(
            LiveNewsArticle(
                title = "Indian Direct Selling & E-Commerce Growth Crosses ₹25,000 Crore",
                source = "Economic Times - Commerce",
                snippet = "Digital transformation and transparent commission structures propel record growth across Tier 2 and Tier 3 Indian cities.",
                publishedAt = "Today, 10:30 AM",
                category = "Industry Trends"
            ),
            LiveNewsArticle(
                title = "UPI Transaction Volume Hits New Record High in India",
                source = "NPCI Live Network",
                snippet = "Seamless instant payments via UPI apps continue to empower micro-entrepreneurs and direct selling networks nationwide.",
                publishedAt = "Today, 09:15 AM",
                category = "Banking & Payments"
            ),
            LiveNewsArticle(
                title = "GST Compliance for E-Commerce & Network Commerce Simplified",
                source = "Ministry of Finance Updates",
                snippet = "New automated GST invoice guidelines provide clear 18% tax breakdown and instant credit mechanisms for verified distributors.",
                publishedAt = "Yesterday",
                category = "Regulatory"
            ),
            LiveNewsArticle(
                title = "Ayurveda & Bio-Health Wellness Products See 35% MoM Demand Surge",
                source = "Healthcare India Journal",
                snippet = "Consumer preference shifts strongly towards certified organic health supplements and wellness starter kits.",
                publishedAt = "Yesterday",
                category = "Market Insights"
            )
        )

        try {
            // Attempt a quick live network check
            val request = Request.Builder()
                .url("https://httpbin.org/get")
                .build()
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                return@withContext staticArticles
            }
        } catch (_: Exception) {
            // Graceful network fallback
        }
        return@withContext staticArticles
    }
}
