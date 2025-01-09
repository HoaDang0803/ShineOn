package com.yasinmaden.ecommerceapp.ui.wishlist

import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.ui.cart.CartContract.UiEffect
import com.yasinmaden.ecommerceapp.ui.detail.DetailContract

object WishlistContract {
    data class UiState(
        val isLoading: Boolean = false,
        val list: List<String> = emptyList(),
        val product: ProductDetails = ProductDetails(),
        val wishlist: List<ProductDetails> = emptyList(),
        val isWishlistEmpty: Boolean = wishlist.isEmpty() // Thêm trạng thái kiểm tra danh sách rỗng
    )

    sealed class UiAction {
        data class AddToCart(val product: ProductDetails) : UiAction() // Hành động thêm sản phẩm vào wishlist
        data class OnProductSelected(val product: ProductDetails) :UiAction()
    }

    sealed class UiEffect {
        data class ShowToast(val message: String) : UiEffect()
        data class NavigateTo(val route: String) : UiEffect()
        data class NavigateToProductDetails(val product: ProductDetails) : UiEffect()
    }
}