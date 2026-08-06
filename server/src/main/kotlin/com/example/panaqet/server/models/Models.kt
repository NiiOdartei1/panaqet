package com.example.panaqet.server.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val token: String? = null,
    val countryCode: String? = null,
    val phoneNumber: String? = null,
    val country: String? = null,
    val profileImage: String? = null,
    val userTheme: String? = null,
    val preferredLanguage: String? = null,
    val idType: String? = null,
    val signupComplete: Boolean = false,
    val isFirstLogin: Boolean = true,
    val storeName: String? = null,
    val storeAddress: String? = null,
    val storeDescription: String? = null,
    val storeLogo: String? = null
)

@Serializable
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val sellerId: String,
    val category: String?,
    val stock: Int,
    val condition: String?,
    val location: String?,
    val brand: String?,
    val gender: String?,
    val color: String?,
    val size: String?,
    val isPackage: Boolean,
    val qrCode: String?,
    val viewCount: Int,
    val isAvailable: Boolean,
    val imageUrl: String?,
    val images: List<ProductImageResponse> = emptyList(),
    val components: List<ProductComponentResponse> = emptyList()
)

@Serializable
data class ProductImageResponse(
    val id: Int,
    val productId: String,
    val imageUrl: String
)

@Serializable
data class ProductComponentResponse(
    val id: Int,
    val productId: String,
    val name: String,
    val price: Double,
    val imageUrl: String?
)

@Serializable
data class OrderResponse(
    val id: String,
    val buyerId: String,
    val totalAmount: Double,
    val status: String,
    val deliveryStatus: String?,
    val timestamp: String,
    val items: List<OrderItemResponse> = emptyList()
)

@Serializable
data class OrderItemResponse(
    val id: Int,
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val totalPrice: Double,
    val productName: String? = null
)

@Serializable
data class StoreProfile(
    val storeName: String,
    val logoUrl: String? = null,
    val address: String? = null,
    val description: String? = null,
    val policies: String? = null
)

@Serializable
data class SellerStats(
    val totalSales: Double,
    val totalProducts: Int,
    val pendingOrders: Int,
    val activeProducts: Int
)

@Serializable
data class SubscriptionResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val validityPeriod: Int,
    val features: String?,
    val status: String
)

@Serializable
data class CommissionPlanResponse(
    val id: Int,
    val planName: String,
    val description: String?,
    val commissionRate: Double,
    val isActive: Boolean
)

@Serializable
data class ConversationResponse(
    val id: String,
    val buyerId: String,
    val sellerId: String,
    val productId: String,
    val productName: String?,
    val lastMessage: String?,
    val timestamp: String
)

@Serializable
data class MessageResponse(
    val id: Int,
    val conversationId: String,
    val senderId: String,
    val senderRole: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean
)

@Serializable
data class PaymentInitializationResponse(
    val accessCode: String,
    val reference: String
)
