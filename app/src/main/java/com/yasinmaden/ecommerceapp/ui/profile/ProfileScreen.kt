package com.yasinmaden.ecommerceapp.ui.profile

import androidx.compose.foundation.layout.*
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.yasinmaden.ecommerceapp.navigation.Graph
import com.yasinmaden.ecommerceapp.ui.components.EmptyScreen
import com.yasinmaden.ecommerceapp.ui.components.LoadingBar
import com.yasinmaden.ecommerceapp.ui.profile.ProfileContract.UiAction
import com.yasinmaden.ecommerceapp.ui.profile.ProfileContract.UiEffect
import com.yasinmaden.ecommerceapp.ui.profile.ProfileContract.UiState
import com.yasinmaden.ecommerceapp.ui.theme.DarkBlue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ProfileScreen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
    navController: NavHostController,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is UiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is UiEffect.NavigateToAuth -> {
                    navController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    when {
        uiState.isLoading -> LoadingBar()
        uiState.list.isNotEmpty() -> EmptyScreen()
        else -> ProfileContent(
            uiState = uiState,
            onAction = onAction
        )
    }
}

@Composable
fun ProfileContent(
    uiState: UiState,
    onAction: (UiAction) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf(uiState.userProfile?.name ?: "") }
    var phoneNumber by remember { mutableStateOf(uiState.userProfile?.phone ?: "") }
    var address by remember { mutableStateOf(uiState.userProfile?.address ?: "") }
    var photoUri by remember { mutableStateOf(uiState.userProfile?.photoUrl ?: "") }
    var gender by remember { mutableStateOf(uiState.userProfile?.gender ?: "Nam") }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            photoUri = it.toString() // Update the selected photo URI
        }
    }
    LaunchedEffect(uiState.userProfile) {
        uiState.userProfile?.let {
            displayName = it.name
            phoneNumber = it.phone ?: ""
            address = it.address ?: ""
            photoUri = it.photoUrl ?: ""
            gender = it.gender?:""
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.padding(20.dp), // Makes the Box take up the full screen size
                contentAlignment = Alignment.Center // Centers the content inside the Box
            ) {
                Text(
                    text = "Hồ sơ người dùng",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold, // Makes the text bold
                    color = DarkBlue

                )
            }

            uiState.user?.let {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = if (photoUri.isNotEmpty()) photoUri else "https://via.placeholder.com/100",
                        contentDescription = "Ảnh hồ sơ",
                        modifier = Modifier
                            .size(150.dp)
                            .clickable(enabled = isEditing) {
                                // Open image picker when in edit mode
                                imagePickerLauncher.launch("image/*")
                            },
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Editable Fields
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Display Name
                    if (isEditing) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Tên") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = displayName.ifEmpty { "Không có tên" },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    GenderSelection(
                        gender = gender.toString(),
                        onGenderChange = { gender = it },
                        isEditing = isEditing
                    )

                    // Email (Read-only)
                    if(isEditing){
                        OutlinedTextField(
                            value = it.email ?: "Không có Email",
                            onValueChange = {},
                            label = { Text("Email") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }else{
                        Text(
                            text = "Email: ${it.email ?: "Không có Email"}",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }

                    // Phone Number
                    if (isEditing) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Số điện thoại") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Số điện thoại: ${phoneNumber.ifEmpty { "Chưa cập nhật" }}",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }

                    // Address
                    if (isEditing) {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Địa chỉ") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Địa chỉ: ${address.ifEmpty { "Chưa cập nhật" }}",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (isEditing) {
                                // Khi nhấn Lưu, gửi dữ liệu cập nhật
                                onAction(UiAction.UpdateUserData(displayName, phoneNumber, address, photoUri, gender))
                            }
                            isEditing = !isEditing
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFa1c9f1)) // Đổi màu nền thành LightBlue
                    ) {
                        Text(text = if (isEditing) "Lưu" else "Chỉnh sửa", fontSize = 15.sp)
                    }

                    Button(
                        onClick = { if (isEditing) {
                            // Khi ở chế độ chỉnh sửa, nút này sẽ trở thành nút "Hủy"
                            isEditing = false
                        } else {
                            // Khi không ở chế độ chỉnh sửa, đây là nút "Đăng xuất"
                            onAction(UiAction.OnSignOut)
                        } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFa1c9f1)) // Đổi màu nền thành LightBlue
                    ) {
                        Text(text = if (isEditing) "Hủy" else "Đăng xuất", fontSize = 15.sp)
                    }
                }
            } ?: run {
                // Placeholder when no user is logged in
                Text(text = "Không có người dùng nào đăng nhập.", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun GenderSelection(isEditing: Boolean, gender: String, onGenderChange: (String) -> Unit) {
    if (isEditing) {
        var expanded by remember { mutableStateOf(false) }
        val genderOptions = listOf("Nam", "Nữ")

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded } // Xử lý click trên toàn bộ Box
                    .padding(vertical = 4.dp) // Tăng vùng nhấn
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = { },
                    label = { Text("Giới tính") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onGenderChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    } else {
        Text(
            text = "Giới tính: $gender",
            fontSize = 15.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview(
    @PreviewParameter(ProfileScreenPreviewProvider::class) uiState: UiState,
) {
    ProfileScreen(
        uiState = uiState,
        uiEffect = emptyFlow(),
        onAction = {},
        navController = NavHostController(LocalContext.current)
    )
}