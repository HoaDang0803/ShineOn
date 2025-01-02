package com.yasinmaden.ecommerceapp.ui.cart

import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails

object CartContract {

    data class UiState(
        val isLoading: Boolean = false,
        val list: List<String> = emptyList(),
        val cartItems: List<ProductDetails> = emptyList(),
        val totalPrice: Double = 0.0
    )

    sealed class UiEffect {
        data class ShowMessage(val message: String) : UiEffect()
    }

    sealed class UiAction {
        data class RemoveItem(val productId: String) : UiAction()
        data class UpdateQuantity(val productId: String, val quantity: Int) : UiAction()
        object CalculateTotal : UiAction()
    }
}