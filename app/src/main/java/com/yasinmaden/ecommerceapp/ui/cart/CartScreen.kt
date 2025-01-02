package com.yasinmaden.ecommerceapp.ui.cart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.ui.components.EmptyScreen
import com.yasinmaden.ecommerceapp.ui.components.LoadingBar
import kotlinx.coroutines.flow.Flow

@Composable
fun CartScreen(
    uiState: CartContract.UiState,
    uiEffect: Flow<CartContract.UiEffect>,
    onAction: (CartContract.UiAction) -> Unit,
    navController: NavHostController
    ) {
        LaunchedEffect(Unit) {
            uiEffect.collect {
                TODO()
            }
        }
        when {
            uiState.isLoading -> LoadingBar()
            uiState.list.isNotEmpty() -> EmptyScreen()
            else -> CartContent(
                navController = navController,
                uiState = uiState,
                onAction = onAction,
            )
        }
    }


    @Composable
    fun CartContent(
        navController: NavHostController,
        uiState: CartContract.UiState,
        onAction: (CartContract.UiAction) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.cartItems) { product ->
               CartItem(product = product)
            }
        }
    }


    @Composable
    fun CartItem(product: ProductDetails) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(IntrinsicSize.Min),
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
        }
    }
