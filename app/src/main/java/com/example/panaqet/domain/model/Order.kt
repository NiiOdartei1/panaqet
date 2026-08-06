package com.example.panaqet.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val buyerId: String,
    val totalAmount: Double,
    val status: OrderStatus,
    val deliveryStatus: String? = null,
    val timestamp: String,
    val items: List<OrderItem> = emptyList()
)

@Serializable
data class OrderItem(
    val id: Int,
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val totalPrice: Double,
    val productName: String? = null
)

@Serializable
enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
