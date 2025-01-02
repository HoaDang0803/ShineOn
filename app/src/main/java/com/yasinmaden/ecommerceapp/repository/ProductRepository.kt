package com.yasinmaden.ecommerceapp.repository

import android.util.Log
import com.yasinmaden.ecommerceapp.common.Resource
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.data.model.product.ProductResponse
import com.yasinmaden.ecommerceapp.network.DummyAPIService
import javax.inject.Inject

class ProductRepository @Inject constructor(

    private val dummyAPIService: DummyAPIService
){

    suspend fun getProducts(): Resource<List<ProductDetails>> {
        val response = try {
            Log.d("Repos", "123 ")
            dummyAPIService.getProducts()
        }catch (e: Exception){
            return Resource.Error(exception = e)
        }
        return Resource.Success(data = response)
    }

    suspend fun getProductById(id: Int): Resource<ProductDetails>{
        val response = try {
            dummyAPIService.getProductById(id)
        }catch (e: Exception){
            return Resource.Error(exception = e)
        }
        return Resource.Success(data = response)
    }

    suspend fun getProductsByBrand(brandName: String): Resource< List<ProductDetails>>{
        val response = try {
            dummyAPIService.getProductsByBrand(brandName)
        }catch (e: Exception){
            return Resource.Error(exception = e)
        }
        return Resource.Success(data = response)
    }

    suspend fun getProductsByName(productName: String):Resource<List<ProductDetails>>{
        val response = try {
            dummyAPIService.getProductsByName(productName)
        }catch (e: Exception){
            return Resource.Error(exception = e)
        }
        return Resource.Success(data = response)
    }
}