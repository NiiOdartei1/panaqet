package com.example.panaqet.data.repository

import com.example.panaqet.data.remote.AuthApi
import com.example.panaqet.data.remote.AuthInterceptor
import com.example.panaqet.domain.model.User
import com.example.panaqet.domain.model.UserRole
import com.example.panaqet.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val authInterceptor: AuthInterceptor
) : AuthRepository {
    override fun login(email: String, password: String): Flow<Result<User>> = flow {
        try {
            val response = api.login(mapOf("email" to email, "password" to password))
            authInterceptor.token = response.token
            emit(Result.success(response.toUser()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun register(
        username: String,
        email: String,
        password: String,
        role: UserRole,
        country: String,
        phoneNumber: String?
    ): Flow<Result<User>> = flow {
        try {
            val response = api.register(mutableMapOf(
                "username" to username,
                "email" to email,
                "password" to password,
                "role" to role.name,
                "country" to country
            ).apply {
                phoneNumber?.let { put("phoneNumber", it) }
            })
            authInterceptor.token = response.token
            emit(Result.success(response.toUser()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getCurrentUser(): Flow<User?> = flow {
        emit(null)
    }
}
