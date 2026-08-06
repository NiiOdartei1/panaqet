package com.example.panaqet.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AffiliateStats(
    val earnings: Double,
    val referralCode: String,
    val totalReferrals: Int,
    val pendingCommission: Double
)

@Serializable
data class AffiliateLink(
    val productId: String,
    val productName: String,
    val referralUrl: String
)
