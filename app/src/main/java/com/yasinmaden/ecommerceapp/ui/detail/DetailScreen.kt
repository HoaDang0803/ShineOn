package com.yasinmaden.ecommerceapp.ui.detail

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.ui.components.EmptyScreen
import com.yasinmaden.ecommerceapp.ui.components.LoadingBar
import kotlinx.coroutines.flow.Flow


@Composable
fun DetailScreen(
    uiState: DetailContract.UiState,
    uiEffect: Flow<DetailContract.UiEffect>,
    onAction: (DetailContract.UiAction) -> Unit,
    navController: NavHostController,
    modifier: Modifier,
    productId: Int
) {
    val viewModel: DetailViewModel = hiltViewModel()


    LaunchedEffect(productId) {
        viewModel.loadProductById(productId.toString())

    }

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            TODO()
        }
    }
    when {
        uiState.isLoading -> LoadingBar()
        uiState.list.isNotEmpty() -> EmptyScreen()
        else -> DetailContent(
            navController = navController,
            uiState = uiState,
            onAction = onAction,
        )
    }
}

@Composable
fun DetailContent(
    navController: NavHostController,
    uiState: DetailContract.UiState,
    onAction: (DetailContract.UiAction) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ProductDetailsScreen(
            product = uiState.product,
            onBack = { navController.popBackStack() },
            onToggleFavorite = { onAction(DetailContract.UiAction.OnFavoriteClicked(uiState.product)) },
            onCart ={onAction(DetailContract.UiAction.OnCartClicked(uiState.product))}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: ProductDetails,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCart: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chi tiết sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd // Đặt chúng ở góc dưới bên phải
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd) // Đặt chúng ở góc dưới bên phải
                ) {
                    // Icon Wishlist ở dưới cùng bên trái
                    FloatingActionButton(
                        onClick = onToggleFavorite,
                        containerColor = if (product.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (product.isFavorite) "Xoá khỏi yêu thích" else "Thêm vào yêu thích"
                        )
                    }

                    // Icon Cart ở bên phải
                    FloatingActionButton(
                        onClick = onCart,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Thêm vào giỏ hàng"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            // Image Carousel
            item {
                if (product.images.isNotEmpty()) {
                    HorizontalImageCarousel(images = product.images)
                } else {
                    // Show thumbnail if there are no images
                    Image(
                        painter = rememberAsyncImagePainter(product.thumbnail),
                        contentDescription = product.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .padding(16.dp)
                    )
                }
            }

            // Product Title, Price, and Availability
            item {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "${product.price}.000 VNĐ",
                    style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                Text(
                    text = if (product.stock > 0) "Còn hàng" else "Hết hàng",
                    style = MaterialTheme.typography.bodyMedium.copy(color = if (product.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }

            // Product Description
            item {
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Product Rating
            item {
                RatingBar(rating = product.rating, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))
            }

            // Additional Info
            item {
                Text(
                    text = "Thương hiệu: ${product.brand}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = "Phân loại: ${product.category}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    text = "Khối lượng: ${product.weight}g",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }

            // Shipping Information
            item {
                Text(
                    text = "Thời gian vận chuyển: ${product.shippingInformation}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Warranty Information
            item {
                Text(
                    text = "Bảo hành: ${product.warrantyInformation}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun HorizontalImageCarousel(images: List<String>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(images) { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(465.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
        }
    }
}

@Composable
fun RatingBar(rating: Double, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating.toInt()) Icons.Default.Star else Icons.Default.Check,
                contentDescription = "Đánh giá",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = rating.toString(),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
