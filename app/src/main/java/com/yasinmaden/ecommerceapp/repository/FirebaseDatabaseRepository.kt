package com.yasinmaden.ecommerceapp.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.yasinmaden.ecommerceapp.data.model.product.ProductDetails
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
    /*fun addCartItem(user: FirebaseUser, product: ProductDetails) {
        val cartRef = databaseReference.child(user.uid).child("cart")

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        cartRef.child(product.id).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
                val currentQuantity = snapshot.child("quantity").getValue(Int::class.java) ?: 0
                val updatedQuantity = currentQuantity + product.quantity
                cartRef.child(product.id).child("quantity").setValue(updatedQuantity)
            } else {
                // Nếu sản phẩm chưa có trong giỏ hàng, thêm mới với số lượng 1
                cartRef.child(product.id).setValue(product)
            }
        }
    }

    fun removeCartItem(user: FirebaseUser, product: ProductDetails) {
        val cartRef = databaseReference.child(user.uid).child("cart")
        cartRef.child(product.id).removeValue()
    }

    // Phương thức để cập nhật số lượng sản phẩm trong giỏ hàng
    fun updateCartItemQuantity(user: FirebaseUser, productId: String, quantity: Int) {
        val cartRef = databaseReference.child(user.uid).child("cart")
        cartRef.child(productId).child("quantity").setValue(quantity)
    }

    fun getCartItems(
        user: FirebaseUser,
        callback: (List<ProductDetails>) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
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


    fun calculateTotalCartPrice(
        user: FirebaseUser,
        callback: (Double) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
        val cartRef = databaseReference.child(user.uid).child("cart")
        cartRef.get().addOnSuccessListener { dataSnapshot ->
            var total = 0.0
            for (snapshot in dataSnapshot.children) {
                val product = snapshot.getValue(ProductDetails::class.java)
                if (product != null && product.quantity > 0) {
                    total += product.price * product.quantity
                }
            }
            callback(total)
        }.addOnFailureListener { exception ->
            onError(DatabaseError.fromException(exception))
        }
    }*/
}