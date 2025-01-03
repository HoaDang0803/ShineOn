package com.yasinmaden.ecommerceapp.ui.wishlist

import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails

object WishlistContract {
    data class UiState(
        val isLoading: Boolean = false,
        val list: List<String> = emptyList(),
        val wishlist: List<ProductDetails> = emptyList(),
        val isWishlistEmpty: Boolean = wishlist.isEmpty() // Thêm trạng thái kiểm tra danh sách rỗng
    )

    sealed class UiAction {
        object AddToWishlist : UiAction() // Hành động thêm sản phẩm vào wishlist
        object RemoveFromWishlist : UiAction() // Hành động xóa sản phẩm khỏi wishlist
    }

    sealed class UiEffect {
        data class ShowMessage(val message: String) : UiEffect() // Hiệu ứng hiển thị thông báo
    }
}
