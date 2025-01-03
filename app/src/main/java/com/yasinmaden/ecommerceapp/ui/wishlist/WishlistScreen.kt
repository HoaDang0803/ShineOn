package com.yasinmaden.ecommerceapp.ui.wishlist

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


@Composable
fun WishlistScreen(
    uiState: WishlistContract.UiState,
    uiEffect: Flow<WishlistContract.UiEffect>,
    onAction: (WishlistContract.UiAction) -> Unit,
    navController: NavHostController,
) {
    LaunchedEffect(Unit) {
        uiEffect.collect {
            TODO()
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
            onClick = { navController.navigate("home") }
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
            WishlistItem(product = product) // Gọi WishlistItem
        }
    }
}





@Composable
fun WishlistItem(product: ProductDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(200.dp), // Tăng chiều cao để kiểm tra
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = product.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "Price: $${product.price}", style = MaterialTheme.typography.titleSmall)
                Text(text = "Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // Kiểm tra Box chứa nút
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f) // Đặt nút ở lớp trên cùng
                .background(Color.Red), // Màu nền để kiểm tra vị trí của Box
            contentAlignment = Alignment.BottomEnd
        ) {
            androidx.compose.material3.Button(
                onClick = { /* Chưa xử lý sự kiện */ },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Thêm vào giỏ hàng")
            }
        }
    }
}
