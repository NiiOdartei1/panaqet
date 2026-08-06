package com.example.panaqet.data.remote.dto

import com.example.panaqet.domain.model.Product
import com.example.panaqet.domain.model.ProductComponent
import com.example.panaqet.domain.model.ProductImage
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
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
    val images: List<ProductImageDto> = emptyList(),
    val components: List<ProductComponentDto> = emptyList()
) {
    fun toProduct(): Product {
        return Product(
            id = id,
            name = name,
            description = description,
            price = price,
            sellerId = sellerId,
            category = category,
            stock = stock,
            condition = condition,
            location = location,
            brand = brand,
            gender = gender,
            color = color,
            size = size,
            isPackage = isPackage,
            qrCode = qrCode,
            viewCount = viewCount,
            isAvailable = isAvailable,
            imageUrl = imageUrl,
            images = images.map { it.toProductImage() },
            components = components.map { it.toProductComponent() }
        )
    }
}

@Serializable
data class ProductImageDto(
    val id: Int,
    val productId: String,
    val imageUrl: String
) {
    fun toProductImage() = ProductImage(id, productId, imageUrl)
}

@Serializable
data class ProductComponentDto(
    val id: Int,
    val productId: String,
    val name: String,
    val price: Double,
    val imageUrl: String?
) {
    fun toProductComponent() = ProductComponent(id, productId, name, price, imageUrl)
}
