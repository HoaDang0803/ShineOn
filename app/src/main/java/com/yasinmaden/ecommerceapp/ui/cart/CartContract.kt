package com.yasinmaden.ecommerceapp.ui.cart

import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.ui.home.HomeContract.UiAction
import com.yasinmaden.ecommerceapp.ui.home.HomeContract.UiEffect

object CartContract {

    data class UiState(
        val isLoading: Boolean = false,
        val list: List<String> = emptyList(),
        val product: ProductDetails = ProductDetails(),
        val cartItems: List<ProductDetails> = emptyList(),
        val totalPrice: Double = 0.0
    )

    sealed class UiEffect {
        data class NavigateTo(val route: String) : UiEffect()
        data class NavigateToProductDetails(val product: ProductDetails) : UiEffect()
    }

    sealed class UiAction {
        data class RemoveItem(val product: String) : UiAction()
        data class IncreaseQuantity(val productId: String, val quantity: Int = 1) : UiAction()
        data class DecreaseQuantity(val productId: String, val quantity: Int = 1) : UiAction()
        data class OnProductSelected(val product: ProductDetails) : UiAction()
    }
}