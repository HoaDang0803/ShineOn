package com.yasinmaden.ecommerceapp.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
import com.yasinmaden.ecommerceapp.ui.profile.UserProfile
import javax.inject.Inject

class FirebaseDatabaseRepository @Inject constructor(
    private val databaseReference: DatabaseReference,
) {
    fun addFavoriteItem(user: FirebaseUser, product: ProductDetails){
        val favoriteRef = databaseReference.child(user.uid).child("favorites")
        favoriteRef.child(product.id.toString()).setValue(product)
    }

    fun removeFavoriteItem(user: FirebaseUser, product: ProductDetails) {
        val favoriteRef = databaseReference.child(user.uid).child("favorites")
        favoriteRef.child(product.id.toString()).removeValue()
    }

    fun getAllWishlist(user: FirebaseUser, callback: (List<ProductDetails>) -> Unit, onError: (DatabaseError) -> Unit) {
        val favoriteRef = databaseReference.child(user.uid).child("favorites")
        favoriteRef.get().addOnSuccessListener { dataSnapshot ->
            val favoriteList = mutableListOf<ProductDetails>()
            for (snapshot in dataSnapshot.children) {
                val product = snapshot.getValue(ProductDetails::class.java)
                product?.let { favoriteList.add(it) }
            }
            callback(favoriteList)
        }.addOnFailureListener { exception ->
            onError(DatabaseError.fromException(exception))
        }
    }

    //cart
    fun addCartItem(user: FirebaseUser, product: ProductDetails){
        val cartRef = databaseReference.child(user.uid).child("cart")
        cartRef.child(product.id.toString()).setValue(product)
    }

    fun removeCartItem(user: FirebaseUser, product: ProductDetails) {
        val cartRef = databaseReference.child(user.uid).child("cart")
        cartRef.child(product.id.toString()).removeValue()
    }

    fun getAllCart(user: FirebaseUser, callback: (List<ProductDetails>) -> Unit, onError: (DatabaseError) -> Unit) {
        val cartRef = databaseReference.child(user.uid).child("cart")
        cartRef.get().addOnSuccessListener { dataSnapshot ->
            val cartList = mutableListOf<ProductDetails>()
            for (snapshot in dataSnapshot.children) {
                val product = snapshot.getValue(ProductDetails::class.java)
                product?.let { cartList.add(it) }
            }
            callback(cartList)
        }.addOnFailureListener { exception ->
            onError(DatabaseError.fromException(exception))
        }
    }

    fun increaseCartItemQuantity(user: FirebaseUser, productId: String, quantity: Int) {
        val cartRef = databaseReference.child(user.uid).child("cart")
        val productRef = cartRef.child(productId).child("quantity")

        productRef.get().addOnSuccessListener { snapshot ->
            val currentQuantity = snapshot.getValue(Int::class.java) ?: 1
            val newQuantity = currentQuantity + quantity
            productRef.setValue(newQuantity)
        }.addOnFailureListener {
            // Xử lý lỗi nếu cần
        }
    }

    fun decreaseCartItemQuantity(user: FirebaseUser, productId: String, quantity: Int) {
        val cartRef = databaseReference.child(user.uid).child("cart")
        val productRef = cartRef.child(productId).child("quantity")

        productRef.get().addOnSuccessListener { snapshot ->
            val currentQuantity = snapshot.getValue(Int::class.java) ?: 1
            val newQuantity = currentQuantity - quantity
            if (newQuantity > 0) {
                // Nếu số lượng mới > 0, cập nhật số lượng
                productRef.setValue(newQuantity)
            } else {
                // Nếu số lượng <= 0, xóa sản phẩm khỏi giỏ hàng
                cartRef.child(productId).removeValue()
            }
        }.addOnFailureListener {
            // Xử lý lỗi nếu cần
        }
    }

    //profile
    fun saveUserProfile(user: FirebaseUser, userProfile: UserProfile) {
        val userRef = databaseReference.child(user.uid).child("userInfor")
        userRef.setValue(userProfile)
            .addOnSuccessListener {
                // Lưu thành công
            }
            .addOnFailureListener { exception ->
                // Xử lý lỗi nếu cần
            }
    }

    fun getUserProfile(user: FirebaseUser, callback: (UserProfile?) -> Unit, onError: (DatabaseError) -> Unit) {
        val userRef = databaseReference.child(user.uid).child("userInfor")
        userRef.get().addOnSuccessListener { snapshot ->
            val userProfile = snapshot.getValue(UserProfile::class.java)
            callback(userProfile)
        }.addOnFailureListener { exception ->
            onError(DatabaseError.fromException(exception))
        }
    }

}