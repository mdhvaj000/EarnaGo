package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.NavRoutes
import com.example.ui.navigation.OmniNavGraph
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: OmniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            OmniControlTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val uiEvent by viewModel.uiEvent.collectAsState()
                val activeUser by viewModel.activeUser.collectAsState()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                LaunchedEffect(uiEvent) {
                    uiEvent?.let { message ->
                        snackbarHostState.showSnackbar(message)
                        viewModel.clearUiEvent()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = NavyDeep,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        if (currentRoute != NavRoutes.Auth.route) {
                            NavigationBar(
                                containerColor = DarkSlate,
                                contentColor = TextPrimaryDark,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == NavRoutes.Home.route,
                                    onClick = {
                                        navController.navigate(NavRoutes.Home.route) {
                                            popUpTo(NavRoutes.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyDeep,
                                        selectedTextColor = AccentGold,
                                        indicatorColor = AccentGold,
                                        unselectedIconColor = TextSecondaryDark,
                                        unselectedTextColor = TextSecondaryDark
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentRoute == NavRoutes.Marketplace.route,
                                    onClick = {
                                        navController.navigate(NavRoutes.Marketplace.route) {
                                            popUpTo(NavRoutes.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Market") },
                                    label = { Text("Market", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyDeep,
                                        selectedTextColor = AccentGold,
                                        indicatorColor = AccentGold,
                                        unselectedIconColor = TextSecondaryDark,
                                        unselectedTextColor = TextSecondaryDark
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentRoute == NavRoutes.NetworkTree.route,
                                    onClick = {
                                        navController.navigate(NavRoutes.NetworkTree.route) {
                                            popUpTo(NavRoutes.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.AccountTree, contentDescription = "Tree") },
                                    label = { Text("Network", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyDeep,
                                        selectedTextColor = AccentGold,
                                        indicatorColor = AccentGold,
                                        unselectedIconColor = TextSecondaryDark,
                                        unselectedTextColor = TextSecondaryDark
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentRoute == NavRoutes.Wallet.route,
                                    onClick = {
                                        navController.navigate(NavRoutes.Wallet.route) {
                                            popUpTo(NavRoutes.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                                    label = { Text("Wallet", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyDeep,
                                        selectedTextColor = AccentGold,
                                        indicatorColor = AccentGold,
                                        unselectedIconColor = TextSecondaryDark,
                                        unselectedTextColor = TextSecondaryDark
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentRoute == NavRoutes.AIAssistant.route,
                                    onClick = {
                                        navController.navigate(NavRoutes.AIAssistant.route) {
                                            popUpTo(NavRoutes.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Psychology, contentDescription = "AI") },
                                    label = { Text("AI Coach", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyDeep,
                                        selectedTextColor = AccentGold,
                                        indicatorColor = AccentGold,
                                        unselectedIconColor = TextSecondaryDark,
                                        unselectedTextColor = TextSecondaryDark
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentRoute == NavRoutes.Auth.route,
                                    onClick = {
                                        navController.navigate(NavRoutes.Auth.route)
                                    },
                                    icon = { Icon(Icons.Default.SwitchAccount, contentDescription = "Auth") },
                                    label = { Text("Identity", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NavyDeep,
                                        selectedTextColor = AccentGold,
                                        indicatorColor = AccentGold,
                                        unselectedIconColor = TextSecondaryDark,
                                        unselectedTextColor = TextSecondaryDark
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = NavyDeep
                    ) {
                        OmniNavGraph(
                            navController = navController,
                            viewModel = viewModel,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }
        }
    }
}
