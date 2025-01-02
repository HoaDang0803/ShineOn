package com.yasinmaden.ecommerceapp.network

import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.data.model.product.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DummyAPIService {
    @GET("ShineOn/products")
    suspend fun getProducts(): List<ProductDetails>

    @GET("ShineOn/products")
    suspend fun getProductsByBrand(@Query("brand") brandName: String):  List<ProductDetails>

    @GET("ShineOn/products")
    suspend fun getProductsByName(@Query("title") productName: String):  List<ProductDetails>

    @GET("ShineOn/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDetails

    @GET("ShineOn/brand-list")
    suspend fun getBrand(): List<String>
}