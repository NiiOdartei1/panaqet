package com.example.panaqet.data.remote.dto

import com.example.panaqet.domain.model.User
import com.example.panaqet.domain.model.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
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
    val storeDescription: String? = null,
    val storeLogo: String? = null
) {
    fun toUser(): User {
        return User(
            id = id,
            username = username,
            email = email,
            role = when (role.uppercase()) {
                "SELLER" -> UserRole.SELLER
                "AFFILIATE" -> UserRole.AFFILIATE
                else -> UserRole.BUYER
            },
            countryCode = countryCode,
            phoneNumber = phoneNumber,
            country = country,
            profileImage = profileImage,
            userTheme = userTheme,
            preferredLanguage = preferredLanguage,
            idType = idType,
            signupComplete = signupComplete,
            isFirstLogin = isFirstLogin,
            storeName = storeName,
            storeDescription = storeDescription,
            storeLogo = storeLogo
        )
    }
}
