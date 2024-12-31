package com.yasinmaden.ecommerceapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yasinmaden.ecommerceapp.common.Resource
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.data.model.product.ProductResponse
import com.yasinmaden.ecommerceapp.repository.CategoryRepository
import com.yasinmaden.ecommerceapp.repository.FirebaseDatabaseRepository
import com.yasinmaden.ecommerceapp.repository.ProductRepository
import com.yasinmaden.ecommerceapp.ui.components.BottomBarScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeContract.UiState())
    val uiState: StateFlow<HomeContract.UiState> = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<HomeContract.UiEffect>() }
    val uiEffect: Flow<HomeContract.UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    fun onAction(uiAction: HomeContract.UiAction) {
        when (uiAction) {
            is HomeContract.UiAction.OnTabSelected -> updateSelectedTab(uiAction.screen)
            is HomeContract.UiAction.OnBrandSelected -> viewModelScope.launch {
                loadProductsByBrand(uiAction.brand)
                loadFavoriteStates()
            }

            is HomeContract.UiAction.OnProductSelected -> viewModelScope.launch {
                emitUiEffect(HomeContract.UiEffect.NavigateToProductDetails(uiAction.product))
                loadProductDetails(uiAction.product.id.toInt())
            }

            is HomeContract.UiAction.OnFavoriteClicked -> onFavoriteClicked(uiAction.product)
        }
    }


    init {
        viewModelScope.launch {
            try {
                loadProducts()
                loadBrand()
                loadFavoriteStates()

            } catch (e: Exception) {
                Log.e("DataLoad", "Veriler yüklenirken hata oluştu: ${e.message}")
            }
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

    private suspend fun loadProductsByBrand(brandName: String): Resource< List<ProductDetails>> {
        updateUiState { copy(isLoadingProducts = true) }
        when (val request = productRepository.getProductsByBrand(brandName)) {
            is Resource.Success -> {
                updateUiState {
                    copy(
                        products = request.data,
                        isLoadingProducts = false
                    )
                }
                return Resource.Success(data = request.data)
            }

            is Resource.Error -> {
                updateUiState { copy(isLoading = false) }
                return Resource.Error(exception = request.exception)
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

    private suspend fun loadProducts(): Resource<List<ProductDetails>> {
        updateUiState { copy(isLoading = true) }
        when (val request = productRepository.getProducts()) {
            is Resource.Success -> {
                Log.d("HomeViewModel", "API Response: ${request.data}")
                updateUiState { copy(products = request.data, isLoading = false)
                }
                return Resource.Success(data = request.data)
            }

            is Resource.Error -> {
                Log.d("HomeViewModel", "${request.exception}")
                updateUiState { copy(isLoading = false) }
                return Resource.Error(exception = request.exception)
            }
        }
    }

    private fun loadFavoriteStates() {
        firebaseDatabaseRepository.getAllWishlist(
            user = firebaseAuth.currentUser!!,
            callback = { wishlist ->
                updateUiState { // uiState güncelleme işlemi
                    val updatedProducts = products.map { product ->
                        val isFavoriteInWishlist = wishlist.any { item -> item.id == product.id && item.isFavorite }
                        if (isFavoriteInWishlist) {
                            product.copy(isFavorite = true) // isFavorite'i true yap
                        } else {
                            product
                        }
                    }
                    copy(products = updatedProducts)
                }
            },
            onError = {}
        )
    }

    private suspend fun loadBrand(): Resource<List<String>> {
        updateUiState { copy(isLoading = true) }
        when (val request = categoryRepository.getBrands()) {
            is Resource.Success -> {
                updateUiState { copy(brand = request.data, isLoading = false) }
                return Resource.Success(data = request.data)
            }

            is Resource.Error -> {
                updateUiState { copy(isLoading = false) }
                return Resource.Error(exception = request.exception)
            }
        }
    }

    private fun updateSelectedTab(screen: BottomBarScreen) = viewModelScope.launch {
        updateUiState { copy(currentTab = screen) }
        emitUiEffect(HomeContract.UiEffect.NavigateTo(screen.route))
    }


    private fun updateUiState(block: HomeContract.UiState.() -> HomeContract.UiState) {
        _uiState.update(block)
    }

    private suspend fun emitUiEffect(uiEffect: HomeContract.UiEffect) {
        _uiEffect.send(uiEffect)
    }
}