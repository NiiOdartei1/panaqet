package com.example.panaqet.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Subscription(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val validityPeriod: Int,
    val features: String?,
    val status: String
)

@Serializable
data class CommissionPlan(
    val id: Int,
    val planName: String,
    val description: String?,
    val commissionRate: Double,
    val isActive: Boolean
)
