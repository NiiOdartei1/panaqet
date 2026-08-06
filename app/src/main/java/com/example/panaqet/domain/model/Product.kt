package com.example.panaqet.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val sellerId: String,
    val category: String?,
    val stock: Int = 0,
    val condition: String? = "New",
    val location: String? = null,
    val brand: String? = null,
    val gender: String? = null,
    val color: String? = null,
    val size: String? = null,
    val isPackage: Boolean = false,
    val qrCode: String? = null,
    val viewCount: Int = 0,
    val isAvailable: Boolean = true,
    val imageUrl: String?,
    val images: List<ProductImage> = emptyList(),
    val components: List<ProductComponent> = emptyList()
)

@Serializable
data class ProductImage(
    val id: Int,
    val productId: String,
    val imageUrl: String
)

@Serializable
data class ProductComponent(
    val id: Int,
    val productId: String,
    val name: String,
    val price: Double,
    val imageUrl: String?
)
