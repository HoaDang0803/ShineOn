package com.yasinmaden.ecommerceapp.ui.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.yasinmaden.ecommerceapp.R
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.navigation.DetailsScreen
import com.yasinmaden.ecommerceapp.ui.components.EmptyScreen
import com.yasinmaden.ecommerceapp.ui.components.LoadingBar
import com.yasinmaden.ecommerceapp.ui.theme.BrandCardContainerColor
import com.yasinmaden.ecommerceapp.ui.theme.ExtraLightBlue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.yasinmaden.ecommerceapp.ui.detail.DetailContract

@Composable
fun HomeScreen(
    uiState: HomeContract.UiState,
    uiEffect: Flow<HomeContract.UiEffect>,
    onAction: (HomeContract.UiAction) -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is HomeContract.UiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is HomeContract.UiEffect.NavigateTo -> {
                    navController.navigate(effect.route)
                }

                is HomeContract.UiEffect.NavigateToProductDetails -> {
                    val itemId = effect.product.id
                    navController.navigate("${DetailsScreen.Information.route}/$itemId")
                }
            }
        }
    }
    when {
        uiState.isLoading -> LoadingBar()
        uiState.list.isNotEmpty() -> EmptyScreen()
        else -> HomeContent(
            uiState = uiState,
            onAction = onAction
        )
    }
}

@Composable
fun HomeContent(
    uiState: HomeContract.UiState,
    onAction: (HomeContract.UiAction) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopStart,

        ) {
        Column {
            WelcomeSection()
            SearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        // Gửi hành động tìm kiếm
                        onAction(HomeContract.UiAction.OnSearchSelected(searchQuery))
                    }
                }

            )
            if (uiState.isSearchEmpty) {
                // Hiển thị thông báo không tìm thấy
                Text(
                    text = "Không tìm thấy sản phẩm \"$searchQuery\"",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                ChooseBrandSection(
                    uiState = uiState,
                    onAction = onAction
                )
                ProductSection(
                    uiState = uiState,
                    onAction = onAction
                )
            }

        }
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                    contentDescription = "Search"
                )
                Spacer(Modifier.width(8.dp))
                Text("Tìm Kiếm...")
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() } // Gửi hành động tìm kiếm khi nhấn Enter
        ),
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                    contentDescription = "Search"
                )
            }
        }
    )
}

@Composable
fun ChooseBrandSection(
    uiState: HomeContract.UiState,
    onAction: (HomeContract.UiAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Thương hiệu nổi bật",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.brand) { brand ->
                BrandCard(
                    brandName = brand,
                    onAction = onAction
                )
            }
        }
    }
}


@Composable
fun BrandCard(
    brandName: String,
    onAction: (HomeContract.UiAction) -> Unit
) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .clickable { onAction(HomeContract.UiAction.OnBrandSelected(brandName)) },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF4FF))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = brandName,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ProductCard(
    product: ProductDetails,
    onAction: (HomeContract.UiAction) -> Unit
) {
    Box(
        modifier = Modifier
            .size(160.dp, 265.dp)
            .background(MaterialTheme.colorScheme.background)
            .clickable { onAction(HomeContract.UiAction.OnProductSelected(product)) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                AsyncImage(
                    model = product.thumbnail,
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop
                )
            }

            // Row for Title, Price and Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start // Title and price on the left
            ) {
                // Column for Title and Price (one above the other)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Title text
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1, // Limit to 1 line if necessary
                        overflow = TextOverflow.Ellipsis,
                    )

                    // Price text
                    Text(
                        text = product.price.toString() + ".000 VNĐ",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                IconButton(
                    onClick = { onAction(HomeContract.UiAction.OnCartClicked(product))}
                ){
                    Icon(
                        ImageVector.vectorResource(R.drawable.cart), // Example icon
                        contentDescription = "Icon",
                        modifier = Modifier.size(20.dp) // Adjust size of icon
                    )}
            }
        }

        // Favorite Icon
        IconButton(
            onClick = { onAction(HomeContract.UiAction.OnFavoriteClicked(product)) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                if (product.isFavorite) {
                    ImageVector.vectorResource(R.drawable.favorites_filled)
                } else {
                    ImageVector.vectorResource(R.drawable.favorites)
                },
                contentDescription = "Favorite"
            )
        }
    }
}


@Composable
fun ProductSection(
    uiState: HomeContract.UiState,
    onAction: (HomeContract.UiAction) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Sản phẩm bán chạy",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 15.dp)
        )

        if (uiState.isLoadingProducts) {
            LoadingBar()
        } else {
            Log.d("HomeScreen", "Products: ${uiState.products}")

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {

                items(uiState.products) { product ->
                    ProductCard(
                        product = product,
                        onAction = onAction
                    )
                }

            }
        }


    }
}

@Composable
fun WelcomeSection() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .padding(top = 16.dp)
    ) {
        Text("Xin chào,", style = MaterialTheme.typography.titleLarge)
        Text("Chào mừng bạn đến với ShineOn!", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewApp() {
    val sampleCategories = listOf("Electronics", "Clothing", "Books", "Toys")

    val sampleProducts = listOf(
        ProductDetails(
            id = "3",
            title = "Headphones",
            thumbnail = "https://via.placeholder.com/150",
            price = 99,
            isFavorite = false
        ),
        ProductDetails(
            id = "4",
            title = "Camera",
            thumbnail = "https://via.placeholder.com/150",
            price = 499,
            isFavorite = true
        )
    )

    HomeScreen(
        uiState = HomeContract.UiState(
            brand = sampleCategories,
            products = sampleProducts
        ),
        uiEffect = flowOf(),
        onAction = {},
        navController = NavHostController(LocalContext.current),
    )
}