package com.example.panaqet.domain.repository

import com.example.panaqet.domain.model.User
import com.example.panaqet.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Result<User>>
    fun register(
        username: String,
        email: String,
        password: String,
        role: UserRole,
        country: String,
        phoneNumber: String? = null
    ): Flow<Result<User>>
    fun getCurrentUser(): Flow<User?>
}
