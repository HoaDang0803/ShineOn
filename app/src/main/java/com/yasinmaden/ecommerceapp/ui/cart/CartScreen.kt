package com.yasinmaden.ecommerceapp.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.yasinmaden.ecommerceapp.R
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.navigation.DetailsScreen
import com.yasinmaden.ecommerceapp.ui.components.EmptyScreen
import com.yasinmaden.ecommerceapp.ui.components.LoadingBar
import com.yasinmaden.ecommerceapp.ui.home.HomeContract
import com.yasinmaden.ecommerceapp.ui.theme.DarkBlue
import com.yasinmaden.ecommerceapp.ui.theme.LightBlue
import kotlinx.coroutines.flow.Flow

@Composable
fun CartScreen(
    uiState: CartContract.UiState,
    uiEffect: Flow<CartContract.UiEffect>,
    onAction: (CartContract.UiAction) -> Unit,
    navController: NavHostController
    ) {
        LaunchedEffect(Unit) {
            uiEffect.collect { effect ->
                when (effect) {
                    is CartContract.UiEffect.NavigateTo -> {
                        navController.navigate(effect.route)
                    }

                    is CartContract.UiEffect.NavigateToProductDetails -> {
                        val itemId = effect.product.id
                        navController.navigate("${DetailsScreen.Information.route}/$itemId")
                    }
                }
            }
        }
        when {
            uiState.isLoading -> LoadingBar()
            uiState.cartItems.isEmpty() -> EmptyCartScreen { navController.navigate("home") }
            else -> CartContent(
                navController = navController,
                uiState = uiState,
                onAction = onAction,
            )
        }
    }

@Composable
fun EmptyCartScreen(onNavigateToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_cart),
            contentDescription = "Empty Cart",
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Giỏ hàng của bạn đang trống!",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hãy bắt đầu mua sắm và thêm sản phẩm vào giỏ hàng",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic,
            color = Color.Gray,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onNavigateToHome() }) {
            Text(text = "Khám phá ngay")
        }
    }
}

@Composable
fun CartContent(
    navController: NavHostController,
    uiState: CartContract.UiState,
    onAction: (CartContract.UiAction) -> Unit
) {
    val totalPrice = uiState.cartItems.sumOf { it.price * it.quantity} // Tính tổng tiền

    Column(modifier = Modifier.fillMaxSize()) { // Sử dụng Column để chứa danh sách và nút thanh toán
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Danh sách sản phẩm chiếm phần lớn màn hình
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.cartItems) { product ->
                CartItem(product = product,
                    onAction=onAction)
            }
        }

        // Nút Thanh toán
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Phần Tạm tính
                Text(
                    text = "Tạm tính: ",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f) // Căn bên trái
                )

                // Phần giá tiền
                Text(
                    text = "${totalPrice}.000 VNĐ",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Phần vận chuyển
                Text(
                    text = "Phí vận chuyển: ",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f) // Căn bên trái
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Phần giá tiền
                Text(
                    text = "30.000 VNĐ",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Divider (đường ngăn cách)
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Phần Tổng số tiền
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Phần chữ "Tổng:"
                Text(
                    text = "Tổng:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f) // Căn bên trái
                )

                // Phần giá tiền
                Text(
                    text = "${totalPrice + 30}.000 VNĐ",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa Tổng và nút
            var showDialog by remember { mutableStateOf(false) }
            // Nút Thanh toán
            Button(
                onClick = {showDialog = true},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightBlue),
                enabled = uiState.cartItems.isNotEmpty()
            ) {
                Text(
                    text = "Thanh toán",
                    color =  if (uiState.cartItems.isNotEmpty()) Color.White else Color.Gray
                )
            }

            if (showDialog) {
                SuccessDialog(
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Đặt hàng thành công",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(
                text = "Cảm ơn bạn đã đặt hàng. Chúng tôi sẽ liên hệ bạn sớm nhất!",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}


@Composable
fun CartItem(
    product: ProductDetails,
    onAction: (CartContract.UiAction)-> Unit
    ) {
    Card(
        onClick = {onAction(CartContract.UiAction.OnProductSelected(product))},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = product.title, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${product.price}.000 VNĐ",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))


                // Nút điều chỉnh số lượng
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Căn 2 nhóm thành phần về 2 phía
                ) {
                    // Phần hiển thị giá (bên trái)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Nút giảm số lượng
                        IconButton(onClick = {
                            onAction(CartContract.UiAction.DecreaseQuantity(product.id))
                        },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.Gray)) {
                            Icon(
                                contentDescription = "Remove Circle Icon",
                                imageVector = ImageVector.vectorResource(id = R.drawable.remove_circle))
                        }
                        Text(
                            text = product.quantity.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = {
                            onAction(CartContract.UiAction.IncreaseQuantity(product.id))
                        },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.Gray)) {
                            Icon(
                                contentDescription = "Add Circle Icon",
                                imageVector = ImageVector.vectorResource(id = R.drawable.add_circle))
                        }
                    }

                    // Delete (bên phải)
                    IconButton(onClick = {
                        onAction(CartContract.UiAction.RemoveItem(product.id))
                    },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.Red)
                    ){
                        Icon(
                            contentDescription = "Add Circle Icon",
                            imageVector = ImageVector.vectorResource(id = R.drawable.delete_outline))
                    }
                }

            }
        }
    }
}