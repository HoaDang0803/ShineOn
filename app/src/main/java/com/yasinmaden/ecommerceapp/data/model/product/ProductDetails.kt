package com.yasinmaden.ecommerceapp.data.model.product

data class ProductDetails(
    val availabilityStatus: String = "",
    val brand: String = "",
    val category: String = "",
    val description: String = "",
    val discountPercentage: Double = 0.0,
    val id: String = "",
    val images: List<String> = emptyList(),
    val minimumOrderQuantity: Int = 0,
    val price: Int = 0,
    val rating: Double = 0.0,
    val returnPolicy: String = "",
    val reviews: List<Review> = emptyList(),
    val shippingInformation: String = "",
    val sku: String = "",
    val stock: Int = 0,
    val tags: List<String> = emptyList(),
    val thumbnail: String = "",
    val title: String = "",
    val warrantyInformation: String = "",
    val weight: Int = 0,
    var isFavorite: Boolean = false,
    var isInCart: Boolean = false,
    var quantity: Int = 1
)