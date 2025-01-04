package com.yasinmaden.ecommerceapp.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.repository.FirebaseDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistContract.UiState())
    val uiState: StateFlow<WishlistContract.UiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<WishlistContract.UiEffect>()
    val uiEffect: Flow<WishlistContract.UiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadWishlist()
    }

    private fun loadWishlist() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            firebaseDatabaseRepository.getAllWishlist(
                user = user,
                callback = { wishlist ->
                    updateUiState { copy(wishlist = wishlist) }
                },
                onError = {
                    // Xử lý lỗi nếu cần
                }
            )
        }
    }

    fun onAction(uiAction: WishlistContract.UiAction) {
        when (uiAction) {
            is WishlistContract.UiAction.AddToCart -> {
                viewModelScope.launch {
                    addToCart(uiAction.product)
                }
            }
            is WishlistContract.UiAction.RemoveFromWishlist -> {
                viewModelScope.launch {
                    removeFromWishlist(uiAction.product)
                }
            }
        }
    }

    private fun addToCart(product: ProductDetails) {
        viewModelScope.launch {
            firebaseAuth.currentUser?.let { user ->
                firebaseDatabaseRepository.addToCart(
                    user = user,
                    product = product,
                    onSuccess = {
                        viewModelScope.launch {
                            emitUiEffect(WishlistContract.UiEffect.NavigateToCart)
                        }
                    },
                    onError = {
                        // Xử lý lỗi nếu cần
                    }
                )
            }
        }
    }


    private fun removeFromWishlist(product: ProductDetails) {
        viewModelScope.launch {
            firebaseAuth.currentUser?.let { user ->
                firebaseDatabaseRepository.removeFromWishlist(
                    user = user,
                    product = product,
                    onSuccess = {
                        viewModelScope.launch {
                            emitUiEffect(WishlistContract.UiEffect.NavigateToCart) // Đặt trong coroutine
                        }
                        loadWishlist()
                    },
                    onError = {
                        // Xử lý lỗi nếu cần
                    }
                )
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
