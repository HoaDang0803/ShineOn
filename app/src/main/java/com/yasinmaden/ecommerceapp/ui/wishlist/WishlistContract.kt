package com.yasinmaden.ecommerceapp.ui.wishlist

import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails

object WishlistContract {

    data class UiState(
        val isLoading: Boolean = false,
        val wishlist: List<ProductDetails> = emptyList() // Danh sách sản phẩm yêu thích
    )

    sealed class UiAction {
        data class AddToCart(val product: ProductDetails) : UiAction() // Hành động thêm vào giỏ hàng
        data class RemoveFromWishlist(val product: ProductDetails) : UiAction()
    }

    sealed class UiEffect {
        object NavigateToCart : UiEffect() // Hiệu ứng chuyển đến màn hình giỏ hàng
        data class ShowMessage(val message: String) : UiEffect() // Hiệu ứng hiển thị thông báo
    }
}
