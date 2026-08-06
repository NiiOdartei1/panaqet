package com.example.panaqet.domain.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: UserRole,
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
    val storeDescription: String? = null,
    val storeLogo: String? = null
)

@Keep
@Serializable
enum class UserRole {
    BUYER, SELLER, AFFILIATE
}
