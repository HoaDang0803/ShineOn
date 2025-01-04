package com.yasinmaden.ecommerceapp.ui.wishlist
import androidx.compose.ui.draw.clip // Dành cho Modifier.clip
import androidx.compose.ui.text.style.TextOverflow // Dành cho TextOverflow
import androidx.compose.material.icons.Icons // Dành cho Icons
import androidx.compose.material.icons.filled.ShoppingCart // Dành cho biểu tượng giỏ hàng
import androidx.compose.ui.graphics.Color // Dành cho Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@Composable
fun WishlistScreen(
    uiState: WishlistContract.UiState,
    uiEffect: Flow<WishlistContract.UiEffect>,
    onAction: (WishlistContract.UiAction) -> Unit,
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is WishlistContract.UiEffect.NavigateToCart -> {
                    navController.navigate(route = "cart") // Chuyển đến màn hình giỏ hàng
                }
                is WishlistContract.UiEffect.ShowMessage -> {
                    // Hiển thị thông báo nếu cần
                }
            }
        }
    }

    WishlistContent(
        navController = navController,
        uiState = uiState,
        onAddToCart = { product ->
            onAction(WishlistContract.UiAction.AddToCart(product))
        }
    )
}

@Composable
fun WishlistContent(
    navController: NavHostController,
    uiState: WishlistContract.UiState,
    onAddToCart: (ProductDetails) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.wishlist) { product ->
            WishlistItem(product = product, onAddToCart = onAddToCart)
        }
    }
}

@Composable
fun WishlistItem(product: ProductDetails, onAddToCart: (ProductDetails) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hình ảnh sản phẩm
                AsyncImage(
                    model = product.thumbnail,
                    contentDescription = product.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.medium)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Thông tin sản phẩm
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            // Nút Add to Bag
            FloatingActionButton(
                onClick = { onAddToCart(product) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(30.dp), // Kích thước nút
                containerColor = MaterialTheme.colorScheme.primary // Màu nền
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Thêm vào giỏ hàng",
                    tint = Color.White, // Màu biểu tượng
                    modifier = Modifier.size(18.dp) // Kích thước biểu tượng
                )
            }
        }
    }
}
