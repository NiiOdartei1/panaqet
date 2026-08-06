package com.example.panaqet.data.repository

import com.example.panaqet.data.remote.ProductApi
import com.example.panaqet.domain.model.Product
import com.example.panaqet.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi
) : ProductRepository {

    override fun getProducts(category: String?, sellerId: String?): Flow<Result<List<Product>>> = flow {
        try {
            val response = api.getProducts(category, sellerId)
            emit(Result.success(response.map { it.toProduct() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getProductById(id: String): Flow<Result<Product>> = flow {
        try {
            val response = api.getProductById(id)
            emit(Result.success(response.toProduct()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun addProduct(
        name: String,
        description: String,
        price: Double,
        category: String,
        stock: Int,
        imageFile: File,
        condition: String,
        location: String?,
        isPackage: Boolean
    ): Flow<Result<Product>> = flow {
        try {
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
            val stockBody = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val conditionBody = condition.toRequestBody("text/plain".toMediaTypeOrNull())
            val locationBody = (location ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
            val isPackageBody = isPackage.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val response = api.addProduct(
                nameBody, descBody, priceBody, categoryBody, stockBody,
                conditionBody, locationBody, isPackageBody, imagePart
            )
            emit(Result.success(response.toProduct()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun updateProductAvailability(id: String, isAvailable: Boolean): Flow<Result<Unit>> = flow {
        try {
            api.updateProductAvailability(id, mapOf("isAvailable" to isAvailable))
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
