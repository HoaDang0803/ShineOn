package com.yasinmaden.ecommerceapp.data.model.product

data class Review(
    val comment: String = "",
    val date: String = "",
    val rating: Double = 0.0,
    val reviewerEmail: String = "",
    val reviewerName: String = ""
)