package com.yasinmaden.ecommerceapp.ui.components

import com.yasinmaden.ecommerceapp.R

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val iconRes: Int
) {
    data object Home : BottomBarScreen(
        route = "HOME",
        title = "Trang chủ",
        iconRes = R.drawable.home
    )
    data object Wishlist : BottomBarScreen(
        route = "WISHLIST",
        title = "Yêu thích",
        iconRes = R.drawable.favorites
    )
    data object Cart : BottomBarScreen(
        route = "CART",
        title = "Giỏ hàng",
        iconRes = R.drawable.cart
    )
    data object Profile : BottomBarScreen(
        route = "PROFILE",
        title = "Cá nhân",
        iconRes = R.drawable.person
    )
}