package com.example.panaqet.data.remote

import com.example.panaqet.domain.model.AffiliateStats
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AffiliateApi {
    @GET("affiliate/stats")
    suspend fun getStats(): AffiliateStats

    @POST("affiliate/links")
    suspend fun generateLink(@Query("productId") productId: String): String
}
