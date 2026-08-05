package com.example.engine

import com.example.data.model.CommissionRecordEntity
import com.example.data.model.CommissionType
import com.example.data.model.UserEntity
import com.example.data.model.UserRank
import java.util.UUID

object CommissionRankEngine {

    const val DIRECT_SALE_COMMISSION_PCT = 0.15 // 15% of BV
    const val TIER_1_BONUS_PCT = 0.10 // 10% of BV
    const val TIER_2_BONUS_PCT = 0.05 // 5% of BV
    const val TIER_3_BONUS_PCT = 0.02 // 2% of BV

    /**
     * Calculates commissions strictly triggered by a verified product order.
     * Enforces legitimate product sale verification rule: $0 BV or non-product sales yield $0 commission.
     */
    fun calculateOrderCommissions(
        orderId: String,
        buyerUserId: String,
        buyerName: String,
        orderBv: Double,
        uplineChain: List<UserEntity>, // Index 0 = Direct Sponsor (Tier 1), Index 1 = Tier 2, Index 2 = Tier 3
        isVerifiedProductSale: Boolean = true
    ): List<CommissionRecordEntity> {
        if (!isVerifiedProductSale || orderBv <= 0) {
            return emptyList()
        }

        val commissions = mutableListOf<CommissionRecordEntity>()

        // 1. Direct Sale Commission to Buyer's Direct Sponsor
        val directSponsor = uplineChain.getOrNull(0)
        if (directSponsor != null) {
            val directAmount = orderBv * DIRECT_SALE_COMMISSION_PCT
            commissions.add(
                CommissionRecordEntity(
                    id = UUID.randomUUID().toString(),
                    recipientUserId = directSponsor.id,
                    recipientName = directSponsor.fullName,
                    sourceOrderId = orderId,
                    sourceBuyerName = buyerName,
                    sourceBv = orderBv,
                    commissionAmount = directAmount,
                    tierLevel = 1,
                    type = CommissionType.DIRECT_SALE,
                    isVerifiedProductSale = true
                )
            )

            // Matching Rank Bonus if Sponsor is Builder or higher
            val rankMatchingPct = directSponsor.rank.matchingBonusPct
            if (rankMatchingPct > 0.0) {
                val matchingBonus = directAmount * rankMatchingPct
                commissions.add(
                    CommissionRecordEntity(
                        id = UUID.randomUUID().toString(),
                        recipientUserId = directSponsor.id,
                        recipientName = directSponsor.fullName,
                        sourceOrderId = orderId,
                        sourceBuyerName = buyerName,
                        sourceBv = orderBv,
                        commissionAmount = matchingBonus,
                        tierLevel = 1,
                        type = CommissionType.MATCHING_RANK_BONUS,
                        isVerifiedProductSale = true
                    )
                )
            }
        }

        // 2. Tiered Network Bonuses
        uplineChain.forEachIndexed { index, uplineUser ->
            val tierLevel = index + 1
            val bonusPct = when (tierLevel) {
                1 -> TIER_1_BONUS_PCT
                2 -> TIER_2_BONUS_PCT
                3 -> TIER_3_BONUS_PCT
                else -> 0.0
            }

            if (bonusPct > 0.0) {
                val tierBonusAmount = orderBv * bonusPct
                commissions.add(
                    CommissionRecordEntity(
                        id = UUID.randomUUID().toString(),
                        recipientUserId = uplineUser.id,
                        recipientName = uplineUser.fullName,
                        sourceOrderId = orderId,
                        sourceBuyerName = buyerName,
                        sourceBv = orderBv,
                        commissionAmount = tierBonusAmount,
                        tierLevel = tierLevel,
                        type = CommissionType.TIER_LEVEL_BONUS,
                        isVerifiedProductSale = true
                    )
                )
            }
        }

        return commissions
    }

    /**
     * Evaluates new rank eligibility based on accumulated total BV volume and direct downline count.
     */
    fun evaluateNewRank(currentVolume: Double, directDownlines: Int): UserRank {
        return UserRank.evaluateRank(currentVolume, directDownlines)
    }
}
