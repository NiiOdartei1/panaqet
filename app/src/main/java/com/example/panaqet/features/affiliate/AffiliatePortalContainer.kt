package com.example.panaqet.features.affiliate

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.panaqet.navigation.Screen.AffiliateScreen

@Composable
fun AffiliatePortalContainer(
    onBrowseProducts: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            PanaQetBottomNav {
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<AffiliateScreen.Dashboard>() } == true,
                    onClick = {
                        navController.navigate(AffiliateScreen.Dashboard) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.Analytics,
                    label = "Dashboard"
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<AffiliateScreen.Links>() } == true,
                    onClick = {
                        navController.navigate(AffiliateScreen.Links) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.Link,
                    label = "Links"
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<AffiliateScreen.Promote>() } == true,
                    onClick = {
                        navController.navigate(AffiliateScreen.Promote) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = Icons.Default.ShoppingCart,
                    label = "Promote"
                )
                PanaQetBottomNavItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<AffiliateScreen.Profile>() } == true,
                    onClick = {
                        navController.navigate(AffiliateScreen.Profile) {
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
            startDestination = AffiliateScreen.Dashboard,
            modifier = Modifier.padding(padding)
        ) {
            composable<AffiliateScreen.Dashboard> {
                AffiliateDashboardScreen(onBrowseProducts = onBrowseProducts)
            }
            composable<AffiliateScreen.Links> {
                // Placeholder
                AffiliateDashboardScreen(onBrowseProducts = onBrowseProducts)
            }
            composable<AffiliateScreen.Promote> {
                LaunchedEffect(Unit) { onBrowseProducts() }
            }
            composable<AffiliateScreen.Profile> {
                // Placeholder
                AffiliateDashboardScreen(onBrowseProducts = onBrowseProducts)
            }
        }
    }
}
