package com.example.panaqet.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.panaqet.domain.model.UserRole
import com.example.panaqet.features.auth.LoginScreen
import com.example.panaqet.features.auth.RegisterScreen
import com.example.panaqet.features.onboarding.UserTypeSelectionScreen
import com.example.panaqet.features.seller.AddProductScreen
import com.example.panaqet.features.seller.SellerPortalContainer
import com.example.panaqet.features.buyer.BuyerPortalContainer
import com.example.panaqet.features.affiliate.AffiliatePortalContainer
import com.example.panaqet.features.order.OrderDetailsScreen
import com.example.panaqet.features.marketplace.ProductDetailScreen
import com.example.panaqet.features.messaging.ChatScreen
import com.example.panaqet.features.messaging.ConversationListScreen

@Composable
fun PanaQetNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ) {
        composable<Screen.Login> {
            LoginScreen(
                onLoginSuccess = { role: UserRole ->
                    val destination = when (role) {
                        UserRole.BUYER -> Screen.BuyerPortal
                        UserRole.SELLER -> Screen.SellerDashboard
                        UserRole.AFFILIATE -> Screen.AffiliatePortal
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.UserTypeSelection)
                }
            )
        }
        composable<Screen.UserTypeSelection> {
            UserTypeSelectionScreen(
                onRoleSelected = { role ->
                    navController.navigate(Screen.Register(role))
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable<Screen.Register> { backStackEntry ->
            val register = backStackEntry.toRoute<Screen.Register>()
            RegisterScreen(
                selectedRole = register.role,
                onRegisterSuccess = { role: UserRole ->
                    val destination = when (role) {
                        UserRole.BUYER -> Screen.BuyerPortal
                        UserRole.SELLER -> Screen.SellerDashboard
                        UserRole.AFFILIATE -> Screen.AffiliatePortal
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.UserTypeSelection) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screen.BuyerPortal> {
            BuyerPortalContainer(
                onProductClick = { id -> navController.navigate(Screen.ProductDetail(id)) },
                onCheckoutClick = { total -> navController.navigate(Screen.Checkout(total)) }
            )
        }

        composable<Screen.SellerDashboard> {
            SellerPortalContainer(
                onAddProductClick = { navController.navigate(Screen.AddProduct) },
                onNavigateToOrderDetails = { id -> navController.navigate(Screen.OrderDetails(id)) },
                onLogout = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.SellerDashboard) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.AffiliatePortal> {
            AffiliatePortalContainer(
                onBrowseProducts = { navController.navigate(Screen.BuyerPortal) }
            )
        }

        composable<Screen.AddProduct> {
            AddProductScreen(
                onSuccess = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.OrderDetails> { backStackEntry ->
            val details = backStackEntry.toRoute<Screen.OrderDetails>()
            OrderDetailsScreen(
                orderId = details.orderId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Chat> { backStackEntry ->
            val chat = backStackEntry.toRoute<Screen.Chat>()
            ChatScreen(
                conversationId = chat.conversationId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.ProductDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<Screen.ProductDetail>()
            ProductDetailScreen(
                productId = detail.productId,
                onBack = { navController.popBackStack() }
            )
        }

        // Global Checkout route
        composable<Screen.Checkout> { backStackEntry ->
            val checkout = backStackEntry.toRoute<Screen.Checkout>()
            com.example.panaqet.features.checkout.CheckoutScreen(
                totalPrice = checkout.totalPrice,
                onOrderSuccess = {
                    navController.navigate(Screen.OrderSuccess)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Screen.OrderSuccess> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Order Placed Successfully!")
                    Button(onClick = {
                        navController.navigate(Screen.BuyerPortal) {
                            popUpTo(Screen.BuyerPortal) { inclusive = true }
                        }
                    }) {
                        Text("Back to Shop")
                    }
                }
            }
        }
    }
}
