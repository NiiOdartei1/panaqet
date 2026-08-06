package com.example.panaqet.navigation

import com.example.panaqet.domain.model.UserRole
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Login : Screen()
    
    @Serializable
    data object UserTypeSelection : Screen()
    
    @Serializable
    data class Register(val role: UserRole) : Screen()
    
    @Serializable
    data object Marketplace : Screen()
    
    @Serializable
    data object SellerDashboard : Screen()
    
    @Serializable
    sealed class SellerScreen : Screen() {
        @Serializable
        data object Home : SellerScreen()
        @Serializable
        data object Inventory : SellerScreen()
        @Serializable
        data object Orders : SellerScreen()
        @Serializable
        data object Store : SellerScreen()
        @Serializable
        data object Subscriptions : SellerScreen()
        @Serializable
        data object Commissions : SellerScreen()
    }

    @Serializable
    data object AddProduct : Screen()

    @Serializable
    data object AffiliatePortal : Screen()

    @Serializable
    sealed class AffiliateScreen : Screen() {
        @Serializable
        data object Dashboard : AffiliateScreen()
        @Serializable
        data object Links : AffiliateScreen()
        @Serializable
        data object Promote : AffiliateScreen()
        @Serializable
        data object Profile : AffiliateScreen()
    }

    @Serializable
    data object BuyerPortal : Screen()

    @Serializable
    sealed class BuyerScreen : Screen() {
        @Serializable
        data object Marketplace : BuyerScreen()
        @Serializable
        data object Cart : BuyerScreen()
        @Serializable
        data object Wishlist : BuyerScreen()
        @Serializable
        data object Profile : BuyerScreen()
    }

    @Serializable
    data object BuyerDashboard : Screen()

    @Serializable
    data class OrderDetails(val orderId: String) : Screen()

    @Serializable
    data object Conversations : Screen()

    @Serializable
    data class Chat(val conversationId: String) : Screen()

    @Serializable
    data class Checkout(val totalPrice: Double) : Screen()

    @Serializable
    data object OrderSuccess : Screen()
    
    @Serializable
    data class ProductDetail(val productId: String) : Screen()
}
