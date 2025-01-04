package com.yasinmaden.ecommerceapp.ui.cart

//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.yasinmaden.ecommerceapp.R
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.ui.components.LoadingBar
import com.yasinmaden.ecommerceapp.ui.theme.LightBlue
import kotlinx.coroutines.flow.Flow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle

@Composable
fun CartScreen(
    uiState: CartContract.UiState,
    uiEffect: Flow<CartContract.UiEffect>,
    onAction: (CartContract.UiAction) -> Unit,
    navController: NavHostController
) {
    LaunchedEffect(Unit) {
        uiEffect.collect {
            // Handle UI effects here
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
    val totalPrice = uiState.cartItems.sumOf { it.price * it.quantity } // Tính tổng tiền

    Column(modifier = Modifier.fillMaxSize()) { // Sử dụng Column để chứa danh sách và nút thanh toán
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Danh sách sản phẩm chiếm phần lớn màn hình
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.cartItems) { product ->
                CartItem(product = product, onAction = onAction)
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
                Text(
                    text = "Tạm tính: ",
                    color = Color.Gray,
                    modifier = Modifier.weight(1f) // Căn bên trái
                )
                Text(
                    text = "${totalPrice}.000 VNĐ",
                    color = Color.Black
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Phí vận chuyển: ",
                    color = Color.Gray,
                    modifier = Modifier.weight(1f) // Căn bên trái
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "30.000 VNĐ",
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng:",
                    color = Color.Gray,
                    modifier = Modifier.weight(1f) // Căn bên trái
                )
                Text(
                    text = "${totalPrice + 30}.000 VNĐ",
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            var showDialog by remember { mutableStateOf(false) }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightBlue),
                enabled = uiState.cartItems.isNotEmpty()
            ) {
                Text(
                    text = "Thanh toán",
                    color = if (uiState.cartItems.isNotEmpty()) Color.White else Color.Gray
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
                color = Color.Black
            )
        },
        text = {
            Text(
                text = "Cảm ơn bạn đã đặt hàng. Chúng tôi sẽ liên hệ bạn sớm nhất!"
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
    onAction: (CartContract.UiAction) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                Text(text = product.title)
                Text(
                    text = "${product.price}.000 VNĐ"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            onAction(CartContract.UiAction.DecreaseQuantity(product.id))
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.remove_circle),
                                contentDescription = "Decrease"
                            )
                        }
                        Text(
                            text = product.quantity.toString()
                        )
                        IconButton(onClick = {
                            onAction(CartContract.UiAction.IncreaseQuantity(product.id))
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.add_circle),
                                contentDescription = "Increase"
                            )
                        }
                    }

                    IconButton(onClick = {
                        onAction(CartContract.UiAction.RemoveItem(product.id))
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_outline),
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }
    }
}
