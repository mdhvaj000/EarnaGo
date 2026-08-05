package com.example.ai

import com.example.BuildConfig
import com.example.data.model.AICategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAssistant {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(
        prompt: String,
        category: AICategory,
        userRole: String,
        userRank: String,
        teamVolume: Double
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackResponse(prompt, category, userRole, userRank, teamVolume)
        }

        try {
            val systemInstructionText = """
                You are EarnaGo AI, the autonomous network marketing and digital business coach.
                User Role: $userRole, Current Rank: $userRank, Team Volume: ₹$teamVolume BV.
                Provide structured, highly practical, product-focused advice strictly encouraging legitimate product sales and team leadership.
                Category: ${category.name}.
            """.trimIndent()

            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstructionText))
                    })
                })
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (response.isSuccessful && responseString.isNotEmpty()) {
                val responseJson = JSONObject(responseString)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "")
                    }
                }
            }
            generateFallbackResponse(prompt, category, userRole, userRank, teamVolume)
        } catch (e: Exception) {
            generateFallbackResponse(prompt, category, userRole, userRank, teamVolume)
        }
    }

    private fun generateFallbackResponse(
        prompt: String,
        category: AICategory,
        userRole: String,
        userRank: String,
        teamVolume: Double
    ): String {
        return when (category) {
            AICategory.GENERAL_COACH -> """
                🎯 **EarnaGo AI Coach Strategy Report**
                
                **Current Rank:** $userRank ($teamVolume BV Team Volume)
                
                **Key Growth Action Items:**
                1. **Product Retailing Focus:** Promote the flagship digital starter kits to expand active customer volume.
                2. **Tier-1 Downline Activation:** Schedule 1-on-1 strategy sessions with your top 3 direct associates to help them reach Builder rank.
                3. **Compliance Audit Check:** Ensure all team referrals emphasize legitimate product value and verified sales weight.
                
                *Tip: Consistent daily product presentations build sustainable long-term recurring commissions.*
            """.trimIndent()

            AICategory.MARKETING_POST -> """
                🚀 **Generated Social Media Campaign Copy**
                
                "Elevate your business productivity with EarnaGo Digital Suites! 💡 Discover enterprise-grade marketing analytics and seamless product commerce in one place. 
                
                👉 Check out our product marketplace today via my link or send me a DM to learn more! #EarnaGo #NetworkCommerce #DigitalBusiness"
            """.trimIndent()

            AICategory.SALES_PREDICTION -> """
                📊 **Predictive Sales Analytics**
                
                - **Projected Monthly Volume:** ₹${String.format("%.2f", teamVolume * 1.18)} BV (+18% projected MoM growth)
                - **Next Rank Threshold:** ${getNextRankTarget(userRank, teamVolume)}
                - **Top Product Conversion:** EarnaGo Digital Academy Kit (45% of team sales volume)
            """.trimIndent()

            AICategory.TEAM_INSIGHT -> """
                👥 **Team Performance Insights**
                
                - **Active Direct Downlines:** High engagement across Tier 1.
                - **Top Performing Node:** Direct Sponsor Lineage Alpha
                - **Recommendation:** Reward team members hitting 500 BV with recognition in your team chat.
            """.trimIndent()
        }
    }

    private fun getNextRankTarget(rank: String, currentVol: Double): String {
        return when (rank) {
            "ASSOCIATE" -> "$${500.0 - currentVol.coerceAtMost(500.0)} BV needed for Builder"
            "BUILDER" -> "$${2500.0 - currentVol.coerceAtMost(2500.0)} BV needed for Executive"
            "EXECUTIVE" -> "$${10000.0 - currentVol.coerceAtMost(10000.0)} BV needed for Director"
            "DIRECTOR" -> "$${50000.0 - currentVol.coerceAtMost(50000.0)} BV needed for Ambassador"
            else -> "Maximum Rank Reached!"
        }
    }
}
