package com.yasinmaden.ecommerceapp.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yasinmaden.ecommerceapp.repository.FirebaseDatabaseRepository
import com.yasinmaden.ecommerceapp.repository.GoogleAuthRepository
import com.yasinmaden.ecommerceapp.ui.profile.ProfileContract.UiAction
import com.yasinmaden.ecommerceapp.ui.profile.ProfileContract.UiEffect
import com.yasinmaden.ecommerceapp.ui.profile.ProfileContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseDatabaseRepository: FirebaseDatabaseRepository,
    private val googleAuthRepository: GoogleAuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }


    init {
        _uiState.update { it.copy(user = firebaseAuth.currentUser) }

        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            firebaseDatabaseRepository.getUserProfile(
                user = currentUser,
                callback = { userProfile ->
                    // Update UI state with the user profile data
                    updateUiState { copy(userProfile = userProfile) }
                    Log.d("user", userProfile.toString())
                },
                onError = { exception ->
                    Log.d("VM", "Error loading user profile: ${exception.message}")
                }
            )
        } else {
            Log.d("VM", "No user logged in")
        }
    }


    private fun signOut() = viewModelScope.launch {
        try {
            firebaseAuth.signOut()
            googleAuthRepository.signOut()
            emitUiEffect(UiEffect.NavigateToAuth)
        } catch (e: Exception) {
            emitUiEffect(UiEffect.ShowToast(e.message.toString()))
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnSignOut -> signOut()
            is UiAction.UpdateUserData -> updateUserData(
                uiAction.displayName,
                uiAction.phoneNumber,
                uiAction.address,
                uiAction.photoUrl,
                uiAction.gender
            )
        }
    }


    private fun updateUserData(displayName: String, phoneNumber: String, address: String, photoUrl: String,gender: String) = viewModelScope.launch {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            try {
                val userProfile = UserProfile(
                    name = displayName,
                    phone = phoneNumber,
                    address = address,
                    photoUrl = photoUrl,
                    gender = gender
                )
                firebaseDatabaseRepository.saveUserProfile(currentUser, userProfile)
                emitUiEffect(UiEffect.ShowToast("Cập nhật thông tin thành công"))
            } catch (e: Exception) {
                emitUiEffect(UiEffect.ShowToast("Cập nhật thất bại: ${e.message}"))
            }
        } else {
            emitUiEffect(UiEffect.ShowToast("Người dùng không tồn tại"))
        }
    }

    private fun updateUiState(update: UiState.() -> UiState) {
        _uiState.value = _uiState.value.update()
    }

    private suspend fun emitUiEffect(uiEffect: UiEffect) {
        _uiEffect.send(uiEffect)
    }
}