package com.example.panaqet.data.remote

import com.example.panaqet.data.remote.dto.ProductDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ProductApi {
    @GET("products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("sellerId") sellerId: String? = null
    ): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ProductDto

    @Multipart
    @POST("products")
    suspend fun addProduct(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("category") category: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("condition") condition: RequestBody,
        @Part("location") location: RequestBody,
        @Part("isPackage") isPackage: RequestBody,
        @Part image: MultipartBody.Part
    ): ProductDto

    @POST("products/{id}/availability")
    suspend fun updateProductAvailability(
        @Path("id") id: String,
        @Body request: Map<String, Boolean>
    )
}
