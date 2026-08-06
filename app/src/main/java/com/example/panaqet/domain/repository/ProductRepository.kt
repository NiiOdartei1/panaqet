package com.example.panaqet.domain.repository

import com.example.panaqet.domain.model.Product
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ProductRepository {
    fun getProducts(category: String? = null, sellerId: String? = null): Flow<Result<List<Product>>>
    fun getProductById(id: String): Flow<Result<Product>>
    fun addProduct(
        name: String,
        description: String,
        price: Double,
        category: String,
        stock: Int,
        imageFile: File,
        condition: String = "New",
        location: String? = null,
        isPackage: Boolean = false
    ): Flow<Result<Product>>

    fun updateProductAvailability(id: String, isAvailable: Boolean): Flow<Result<Unit>>
}
