package com.yasinmaden.ecommerceapp.ui.detail

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yasinmaden.ecommerceapp.common.Resource
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.repository.FirebaseDatabaseRepository
import com.yasinmaden.ecommerceapp.repository.ProductRepository
import com.yasinmaden.ecommerceapp.ui.profile.ProfileContract.UiEffect
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
class DetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailContract.UiState())
    val uiState: StateFlow<DetailContract.UiState> = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<DetailContract.UiEffect>() }
    val uiEffect: Flow<DetailContract.UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    fun loadProductById(id: String) {
        viewModelScope.launch {
            loadProductDetails(id)
        }
    }

    private suspend fun loadProductDetails(id: String): Resource<ProductDetails> {
        _uiState.update { it.copy(isLoading = true) }
        when (val request = productRepository.getProductById(id.toInt())) {
            is Resource.Success -> {
                _uiState.update { it.copy(product = request.data, isLoading = false) }
                return Resource.Success(data = request.data)
            }

            is Resource.Error -> {
                _uiState.update { it.copy(isLoading = false) }
                return Resource.Error(exception = request.exception)
            }
        }
    }

    fun onAction(uiAction: DetailContract.UiAction) {
        when (uiAction) {
            is DetailContract.UiAction.OnFavoriteClicked -> onFavoriteClicked(uiAction.product)
            is DetailContract.UiAction.OnCartClicked -> onCartClicked(uiAction.product)
        }

    }

    private fun onFavoriteClicked(product: ProductDetails) {
        val updatedProduct = product.copy(isFavorite = !product.isFavorite)
        updateUiState {
            copy(
                products = products.map { product ->
                    if (product.id == updatedProduct.id) {
                        updatedProduct
                    } else {
                        product
                    }
                }
            )
        }

        if (updatedProduct.isFavorite) {
            firebaseAuth.currentUser?.let {
                firebaseDatabaseRepository.addFavoriteItem(
                    user = it,
                    product = updatedProduct
                )
            }
        } else {
            firebaseAuth.currentUser?.let {
                firebaseDatabaseRepository.removeFavoriteItem(
                    user = it,
                    product = updatedProduct
                )
            }
        }

    }

    private fun onCartClicked(product: ProductDetails) {
        if (product.stock < 1) {
            viewModelScope.launch {
                emitUiEffect(DetailContract.UiEffect.ShowToast("Sản phẩm đã hết hàng"))
            }
            return
        }
        val updatedProduct = product.copy(isInCart = !product.isInCart)
        updateUiState {
            copy(
                products = products.map { product ->
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
                emitUiEffect(DetailContract.UiEffect.ShowToast("Đã thêm vào giỏ hàng"))
            }
        }
    }


        private fun updateUiState(block: DetailContract.UiState.() -> DetailContract.UiState) {
        _uiState.update(block)
    }

    private suspend fun emitUiEffect(uiEffect: DetailContract.UiEffect) {
        _uiEffect.send(uiEffect)
    }
}