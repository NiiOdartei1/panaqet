package com.example.panaqet.data.remote

import com.example.panaqet.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: Map<String, String>): UserDto

    @POST("auth/register")
    suspend fun register(@Body request: Map<String, String>): UserDto
}
