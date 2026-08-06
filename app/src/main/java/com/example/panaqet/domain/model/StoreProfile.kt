package com.example.panaqet.domain.model

import kotlinx.serialization.Serializable

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
