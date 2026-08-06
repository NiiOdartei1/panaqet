package com.example.panaqet.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommissionPlanDto(
    val id: Int,
    val planName: String,
    val description: String?,
    val commissionRate: Double,
    val isActive: Boolean
)
