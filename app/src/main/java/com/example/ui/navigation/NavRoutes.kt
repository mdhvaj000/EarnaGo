package com.example.ui.navigation

sealed class NavRoutes(val route: String, val title: String) {
    object Auth : NavRoutes("auth", "Identity & Portal Access")
    object Home : NavRoutes("home", "Member Overview")
    object Marketplace : NavRoutes("marketplace", "Product Catalog")
    object Wallet : NavRoutes("wallet", "Wallet & Ledger")
    object NetworkTree : NavRoutes("network_tree", "Referral Tree")
    object Commissions : NavRoutes("commissions", "Commissions & Ranks")
    object AIAssistant : NavRoutes("ai_assistant", "AI Business Assistant")
    object KYC : NavRoutes("kyc", "KYC Verification")
    object AdminConsole : NavRoutes("admin_console", "Owner Console")
}
