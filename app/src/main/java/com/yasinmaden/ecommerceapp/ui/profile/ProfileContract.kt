package com.yasinmaden.ecommerceapp.ui.profile

import android.net.Uri
import com.google.firebase.auth.FirebaseUser

object ProfileContract {
    data class UiState(
        val isLoading: Boolean = false,
        val list: List<String> = emptyList(),
        val user: FirebaseUser? = null,
        val userProfile: UserProfile? = null
    )

    sealed class UiAction{
        data object OnSignOut : UiAction()
        data class UpdateUserData(val displayName: String, val phoneNumber: String, val address: String, val photoUrl: String,val gender: String) : UiAction()
    }

    sealed class UiEffect{
        data class ShowToast(val message: String) : UiEffect()
        data object NavigateToAuth : UiEffect()
    }
}