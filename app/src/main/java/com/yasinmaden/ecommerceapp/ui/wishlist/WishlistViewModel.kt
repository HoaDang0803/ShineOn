package com.yasinmaden.ecommerceapp.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yasinmaden.ecommerceapp.common.Resource
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.repository.FirebaseDatabaseRepository
import com.yasinmaden.ecommerceapp.repository.ProductRepository
import com.yasinmaden.ecommerceapp.ui.cart.CartContract
import com.yasinmaden.ecommerceapp.ui.detail.DetailContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(WishlistContract.UiState())
    val uiState: StateFlow<WishlistContract.UiState> = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<WishlistContract.UiEffect>() }
    val uiEffect: Flow<WishlistContract.UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    init {
        firebaseDatabaseRepository.getAllWishlist(
            user = firebaseAuth.currentUser!!,
            callback = {
                updateUiState { copy(wishlist = it) }
            },
            onError = {  }
        )
    }

    fun onAction(uiAction: WishlistContract.UiAction) {
        when (uiAction) {
            is WishlistContract.UiAction.AddToCart -> addToCart(uiAction.product)
            is WishlistContract.UiAction.OnProductSelected -> viewModelScope.launch {
                emitUiEffect(WishlistContract.UiEffect.NavigateToProductDetails(uiAction.product))
                loadProductDetails(uiAction.product.id.toInt())
            }
        }
    }

    private suspend fun loadProductDetails(id: Int): Resource<ProductDetails> {
        updateUiState { copy(isLoading = true) }
        when (val request = productRepository.getProductById(id)) {
            is Resource.Success -> {
                updateUiState { copy(product = request.data, isLoading = false) }
                return Resource.Success(data = request.data)
            }

            is Resource.Error -> {
                updateUiState { copy(isLoading = false) }
                return Resource.Error(exception = request.exception)
            }
        }
    }

    private fun addToCart(product: ProductDetails) {
        if (product.stock < 1) {
            viewModelScope.launch {
                emitUiEffect(WishlistContract.UiEffect.ShowToast("Sản phẩm đã hết hàng"))
            }
            return
        }
        val updatedProduct = product.copy(isInCart = !product.isInCart)
        updateUiState {
            copy(
                wishlist = wishlist.map { product ->
                    if (product.id == updatedProduct.id) {
                        updatedProduct
                    } else {
                        product
                    }
                }
            )
        }

        if (updatedProduct.isInCart) {
            firebaseAuth.currentUser?.let {
                firebaseDatabaseRepository.addCartItem(
                    user = it,
                    product = updatedProduct
                )
            }
            viewModelScope.launch {
                emitUiEffect(WishlistContract.UiEffect.ShowToast("Đã thêm vào giỏ hàng"))
            }
        }
    }

    private fun updateUiState(block: WishlistContract.UiState.() -> WishlistContract.UiState) {
        _uiState.update(block)
    }

    private suspend fun emitUiEffect(uiEffect: WishlistContract.UiEffect) {
        _uiEffect.send(uiEffect)
    }

}