package com.yasinmaden.ecommerceapp.ui.wishlist

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.yasinmaden.ecommerceapp.R
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.ui.components.LoadingBar
import kotlinx.coroutines.flow.Flow
import androidx.compose.ui.zIndex
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.platform.LocalContext
import com.yasinmaden.ecommerceapp.navigation.DetailsScreen
import com.yasinmaden.ecommerceapp.ui.cart.CartContract
import com.yasinmaden.ecommerceapp.ui.detail.DetailContract
import com.yasinmaden.ecommerceapp.ui.home.HomeContract


@Composable
fun WishlistScreen(
    uiState: WishlistContract.UiState,
    uiEffect: Flow<WishlistContract.UiEffect>,
    onAction: (WishlistContract.UiAction) -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is WishlistContract.UiEffect.NavigateTo -> {
                    navController.navigate(effect.route)
                }

                is WishlistContract.UiEffect.NavigateToProductDetails -> {
                    val itemId = effect.product.id
                    navController.navigate("${DetailsScreen.Information.route}/$itemId")
                }

                is WishlistContract.UiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    when {
        uiState.isLoading -> LoadingBar()
        uiState.wishlist.isEmpty() -> EmptyWishlistScreen(navController = navController) // Truyền NavController
        else -> WishlistContent(
            navController = navController,
            uiState = uiState,
            onAction = onAction,
        )
    }
}


@Composable
fun EmptyWishlistScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hình ảnh từ drawable
        Image(
            painter = painterResource(id = R.drawable.empty_wishlist), // ID ảnh trong drawable
            contentDescription = "Empty Wishlist",
            modifier = Modifier.size(128.dp),
            contentScale = ContentScale.Crop

        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Danh sách yêu thích trống!",
            fontSize = 20.sp,

            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Nhấn vào biểu tượng trái tim để lưu sản phẩm.",
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Gray,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("HOME") }
        ) {
            Text(text = "Khám phá ngay")
        }
    }
}


@Composable
fun WishlistContent(
    navController: NavHostController,
    uiState: WishlistContract.UiState,
    onAction: (WishlistContract.UiAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.wishlist) { product ->
            WishlistItem(product = product, onAction = onAction, navController = navController) // Gọi WishlistItem

        }
    }
}

@Composable
fun WishlistItem(product: ProductDetails, onAction: (WishlistContract.UiAction) -> Unit,
                 navController: NavHostController) {
    Card(
        onClick = { onAction(WishlistContract.UiAction.OnProductSelected(product))},
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
                        text = "${product.price}.000 VNĐ",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            // Nút Add to Bag
            FloatingActionButton(
                onClick = { onAction(WishlistContract.UiAction.AddToCart(product))
                    navController.navigate("CART")},
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