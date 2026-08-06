package com.example.panaqet.features.buyer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.panaqet.core.ui.components.PanaQetBottomNav
import com.example.panaqet.core.ui.components.PanaQetBottomNavItem
import com.example.panaqet.features.cart.CartScreen
import com.example.panaqet.features.cart.CartViewModel
import com.example.panaqet.features.marketplace.MarketplaceScreen
import com.example.panaqet.features.wishlist.WishlistScreen
import com.example.panaqet.navigation.Screen.BuyerScreen

@Composable
fun BuyerPortalContainer(
    onProductClick: (String) -> Unit,
    onCheckoutClick: (Double) -> Unit,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val cartUiState by cartViewModel.uiState.collectAsState()
    val cartCount = cartUiState.items.sumOf { it.quantity }

    Scaffold(
        bottomBar = {
            PanaQetBottomNav {
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<BuyerScreen.Marketplace>() } == true,
                    onClick = {
                        navController.navigate(BuyerScreen.Marketplace) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.Search,
                    label = "Shop"
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<BuyerScreen.Wishlist>() } == true,
                    onClick = {
                        navController.navigate(BuyerScreen.Wishlist) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.Favorite,
                    label = "Wishlist"
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<BuyerScreen.Cart>() } == true,
                    onClick = {
                        navController.navigate(BuyerScreen.Cart) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.ShoppingCart,
                    label = "Cart",
                    badgeCount = cartCount
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<BuyerScreen.Profile>() } == true,
                    onClick = {
                        navController.navigate(BuyerScreen.Profile) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.Person,
                    label = "Profile"
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BuyerScreen.Marketplace,
            modifier = Modifier.padding(padding)
        ) {
            composable<BuyerScreen.Marketplace> {
                MarketplaceScreen(
                    onProductClick = onProductClick,
                    onCartClick = { navController.navigate(BuyerScreen.Cart) },
                    onWishlistClick = { navController.navigate(BuyerScreen.Wishlist) },
                    onMessagesClick = { /* Global messaging? */ },
                    onProfileClick = { navController.navigate(BuyerScreen.Profile) }
                )
            }
            composable<BuyerScreen.Wishlist> {
                WishlistScreen(
                    onProductClick = onProductClick,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<BuyerScreen.Cart> {
                CartScreen(
                    onCheckoutClick = onCheckoutClick,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<BuyerScreen.Profile> {
                BuyerDashboardScreen(
                    onOrderClick = { /* Detail navigation should be global */ }
                )
            }
        }
    }
}
