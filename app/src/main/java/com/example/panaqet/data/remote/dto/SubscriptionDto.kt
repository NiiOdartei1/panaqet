package com.example.panaqet.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionDto(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val validityPeriod: Int,
    val features: String?,
    val status: String
)
