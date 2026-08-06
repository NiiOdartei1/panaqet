package com.example.panaqet.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CartItemDto(
    val id: Int,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String?
)
