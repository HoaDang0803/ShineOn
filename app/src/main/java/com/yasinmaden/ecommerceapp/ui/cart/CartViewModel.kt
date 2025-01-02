package com.yasinmaden.ecommerceapp.ui.cart

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.yasinmaden.ecommerceapp.repository.FirebaseDatabaseRepository
import com.yasinmaden.ecommerceapp.ui.wishlist.WishlistContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
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

    }
//    private fun loadCartItems() {
//        val currentUser = firebaseAuth.currentUser
//        if (currentUser != null) {
//            firebaseDatabaseRepository.getCartItems(
//                user = currentUser,
//                callback = {
//                    updateUiState { copy(cartItems = it) }
//                },
//                onError = { error ->
//                    // Xử lý lỗi nếu cần
//                }
//            )
//        }
//    }
//
//    fun onAction(uiAction: CartContract.UiAction) {
//        when (uiAction) {
//            is CartContract.UiAction.RemoveItem -> removeItemFromCart(uiAction.productId)
//            is CartContract.UiAction.UpdateQuantity -> updateItemQuantity(uiAction.productId, uiAction.quantity)
//            CartContract.UiAction.CalculateTotal -> calculateTotalPrice()
//        }
//    }
//
//    private fun removeItemFromCart(productId: String) {
//        val currentUser = firebaseAuth.currentUser
//        if (currentUser != null) {
//            val product = uiState.value.cartItems.find { it.id == productId }
//            product?.let {
//                firebaseDatabaseRepository.removeCartItem(
//                    user = currentUser,
//                    product = it
//                )
//                loadCartItems() // Refresh the cart items
//            }
//        }
//    }
//
//    private fun updateItemQuantity(productId: String, quantity: Int) {
//        val currentUser = firebaseAuth.currentUser
//        if (currentUser != null) {
//            firebaseDatabaseRepository.updateCartItemQuantity(
//                user = currentUser,
//                productId = productId,
//                quantity = quantity
//            )
//            loadCartItems() // Refresh the cart items
//        }
//    }
//
//    private fun calculateTotalPrice() {
//        val currentUser = firebaseAuth.currentUser
//        if (currentUser != null) {
//            firebaseDatabaseRepository.calculateTotalCartPrice(
//                user = currentUser,
//                callback = { total ->
//                    updateUiState { copy(totalPrice = total) }
//                },
//                onError = { error ->
//                    // Xử lý lỗi nếu cần
//                }
//            )
//        }
//    }

    private fun updateUiState(block: CartContract.UiState.() -> CartContract.UiState) {
        _uiState.update(block)
    }

    private suspend fun emitUiEffect(uiEffect: CartContract.UiEffect) {
        _uiEffect.send(uiEffect)
    }
}
