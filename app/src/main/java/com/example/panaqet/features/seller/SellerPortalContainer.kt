package com.example.panaqet.features.seller

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.panaqet.core.ui.components.PanaQetBottomNav
import com.example.panaqet.core.ui.components.PanaQetBottomNavItem
import com.example.panaqet.features.monetization.CommissionPlanScreen
import com.example.panaqet.features.monetization.SubscriptionScreen
import com.example.panaqet.navigation.Screen
import com.example.panaqet.navigation.Screen.SellerScreen

@Composable
fun SellerPortalContainer(
    onAddProductClick: () -> Unit,
    onNavigateToOrderDetails: (String) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            PanaQetBottomNav {
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<SellerScreen.Home>() } == true,
                    onClick = {
                        navController.navigate(SellerScreen.Home) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.Home,
                    label = "Dashboard"
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<SellerScreen.Inventory>() } == true,
                    onClick = {
                        navController.navigate(SellerScreen.Inventory) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.AutoMirrored.Filled.List,
                    label = "Inventory"
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<SellerScreen.Orders>() } == true,
                    onClick = {
                        navController.navigate(SellerScreen.Orders) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.ShoppingCart,
                    label = "Orders"
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<SellerScreen.Subscriptions>() } == true,
                    onClick = {
                        navController.navigate(SellerScreen.Subscriptions) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.Star,
                    label = "Pro Plans"
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<SellerScreen.Store>() } == true,
                    onClick = {
                        navController.navigate(SellerScreen.Store) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.Settings,
                    label = "Settings"
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = SellerScreen.Home,
            modifier = Modifier.padding(padding)
        ) {
            composable<SellerScreen.Home> {
                SellerDashboardScreen(
                    onAddProductClick = onAddProductClick,
                    onProductClick = { /* Navigate to Edit */ }
                )
            }
            composable<SellerScreen.Inventory> {
                ProductManagementScreen(
                    onAddProductClick = onAddProductClick,
                    onProductClick = { /* Navigate to Edit */ }
                )
            }
            composable<SellerScreen.Orders> {
                OrderManagementScreen(
                    onOrderClick = onNavigateToOrderDetails
                )
            }
            composable<SellerScreen.Store> {
                StoreManagementScreen()
            }
            composable<SellerScreen.Subscriptions> {
                SubscriptionScreen(onBack = { navController.popBackStack() })
            }
            composable<SellerScreen.Commissions> {
                CommissionPlanScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
