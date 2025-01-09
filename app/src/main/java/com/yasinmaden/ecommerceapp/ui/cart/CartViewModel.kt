package com.yasinmaden.ecommerceapp.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yasinmaden.ecommerceapp.common.Resource
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.repository.FirebaseDatabaseRepository
import com.yasinmaden.ecommerceapp.repository.ProductRepository
import com.yasinmaden.ecommerceapp.ui.home.HomeContract
import com.yasinmaden.ecommerceapp.ui.wishlist.WishlistContract
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
class CartViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartContract.UiState())
    val uiState: StateFlow<CartContract.UiState> = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<CartContract.UiEffect>() }
    val uiEffect: Flow<CartContract.UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    init {
        firebaseDatabaseRepository.getAllCart(
            user = firebaseAuth.currentUser!!,
            callback = {
                updateUiState { copy(cartItems = it) }
            },
            onError = {  }
        )
    }

    fun onAction(uiAction: CartContract.UiAction) {
        when (uiAction) {
            is CartContract.UiAction.RemoveItem -> viewModelScope.launch {
                removeItemFromCart(uiAction.product)
                loadCartItems()
            }

            is CartContract.UiAction.IncreaseQuantity -> viewModelScope.launch {
                increaseItemQuantity(uiAction.productId, uiAction.quantity)
                loadCartItems()
            }

            is CartContract.UiAction.DecreaseQuantity -> viewModelScope.launch {
                decreaseItemQuantity(uiAction.productId, uiAction.quantity)
                loadCartItems()
            }

            is CartContract.UiAction.OnProductSelected -> viewModelScope.launch {
                emitUiEffect(CartContract.UiEffect.NavigateToProductDetails(uiAction.product))
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

    private fun loadCartItems() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            firebaseDatabaseRepository.getAllCart(
                user = currentUser,
                callback = {
                    updateUiState { copy(cartItems = it) }
                },
                onError = { error ->
                    // Xử lý lỗi nếu cần
                }
            )
        }
    }

    private fun removeItemFromCart(productId: String) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val product = uiState.value.cartItems.find { it.id == productId }
            product?.let {
                firebaseDatabaseRepository.removeCartItem(
                    user = currentUser,
                    product = it
                )
            }
        }
    }

    private fun increaseItemQuantity(productId: String, quantity: Int) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            firebaseDatabaseRepository.increaseCartItemQuantity(
                user = currentUser,
                productId = productId,
                quantity = quantity
                )
        }
    }

    private fun decreaseItemQuantity(productId: String, quantity: Int) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            firebaseDatabaseRepository.decreaseCartItemQuantity(
                user = currentUser,
                productId = productId,
                quantity = quantity
            )
        }
    }

    private fun updateUiState(block: CartContract.UiState.() -> CartContract.UiState) {
        _uiState.update(block)
    }

    private suspend fun emitUiEffect(uiEffect: CartContract.UiEffect) {
        _uiEffect.send(uiEffect)
    }
}
