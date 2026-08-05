package com.example

import com.example.data.model.CommissionType
import com.example.data.model.KycStatus
import com.example.data.model.UserEntity
import com.example.data.model.UserRank
import com.example.data.model.UserRole
import com.example.engine.CommissionRankEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommissionEngineTest {

    @Test
    fun `calculateOrderCommissions calculates direct sale and tiered bonuses for legitimate product order`() {
        val sponsor1 = UserEntity(
            id = "sponsor_1",
            fullName = "Sponsor One",
            email = "sp1@omnicontrol.com",
            phone = "123",
            role = UserRole.MEMBER,
            rank = UserRank.BUILDER,
            kycStatus = KycStatus.APPROVED,
            sponsorId = "sponsor_2",
            referralCode = "SP1"
        )

        val sponsor2 = UserEntity(
            id = "sponsor_2",
            fullName = "Sponsor Two",
            email = "sp2@omnicontrol.com",
            phone = "456",
            role = UserRole.MEMBER,
            rank = UserRank.EXECUTIVE,
            kycStatus = KycStatus.APPROVED,
            sponsorId = null,
            referralCode = "SP2"
        )

        val uplineChain = listOf(sponsor1, sponsor2)
        val orderBv = 400.0

        val commissions = CommissionRankEngine.calculateOrderCommissions(
            orderId = "ord_1001",
            buyerUserId = "buyer_1",
            buyerName = "John Buyer",
            orderBv = orderBv,
            uplineChain = uplineChain,
            isVerifiedProductSale = true
        )

        assertTrue("Commissions should be generated for verified product sale", commissions.isNotEmpty())

        // Direct Sale Commission = 15% of $400 = $60
        val directSaleComm = commissions.find { it.type == CommissionType.DIRECT_SALE }
        assertEquals(60.0, directSaleComm?.commissionAmount ?: 0.0, 0.01)

        // Tier 1 Bonus = 10% of $400 = $40
        val tier1Comm = commissions.find { it.type == CommissionType.TIER_LEVEL_BONUS && it.tierLevel == 1 }
        assertEquals(40.0, tier1Comm?.commissionAmount ?: 0.0, 0.01)

        // Tier 2 Bonus = 5% of $400 = $20
        val tier2Comm = commissions.find { it.type == CommissionType.TIER_LEVEL_BONUS && it.tierLevel == 2 }
        assertEquals(20.0, tier2Comm?.commissionAmount ?: 0.0, 0.01)
    }

    @Test
    fun `calculateOrderCommissions enforces zero commission for non-product or zero BV orders`() {
        val sponsor = UserEntity(
            id = "sponsor_1",
            fullName = "Sponsor One",
            email = "sp1@omnicontrol.com",
            phone = "123",
            role = UserRole.MEMBER,
            rank = UserRank.ASSOCIATE,
            kycStatus = KycStatus.APPROVED,
            sponsorId = null,
            referralCode = "SP1"
        )

        val zeroBvCommissions = CommissionRankEngine.calculateOrderCommissions(
            orderId = "ord_1002",
            buyerUserId = "buyer_1",
            buyerName = "John Buyer",
            orderBv = 0.0,
            uplineChain = listOf(sponsor),
            isVerifiedProductSale = true
        )

        assertTrue("Zero BV orders must produce 0 commissions", zeroBvCommissions.isEmpty())

        val unverifiedCommissions = CommissionRankEngine.calculateOrderCommissions(
            orderId = "ord_1003",
            buyerUserId = "buyer_1",
            buyerName = "John Buyer",
            orderBv = 500.0,
            uplineChain = listOf(sponsor),
            isVerifiedProductSale = false
        )

        assertTrue("Unverified product sales must produce 0 commissions", unverifiedCommissions.isEmpty())
    }

    @Test
    fun `evaluateNewRank correctly upgrades rank based on volume and direct downline thresholds`() {
        assertEquals(UserRank.ASSOCIATE, CommissionRankEngine.evaluateNewRank(100.0, 0))
        assertEquals(UserRank.BUILDER, CommissionRankEngine.evaluateNewRank(500.0, 2))
        assertEquals(UserRank.EXECUTIVE, CommissionRankEngine.evaluateNewRank(2500.0, 4))
        assertEquals(UserRank.DIRECTOR, CommissionRankEngine.evaluateNewRank(10000.0, 6))
        assertEquals(UserRank.AMBASSADOR, CommissionRankEngine.evaluateNewRank(50000.0, 10))
    }
}
