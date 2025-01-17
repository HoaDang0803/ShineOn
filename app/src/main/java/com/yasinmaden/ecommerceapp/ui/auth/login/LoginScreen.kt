package com.yasinmaden.ecommerceapp.ui.auth.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.yasinmaden.ecommerceapp.R
import com.yasinmaden.ecommerceapp.navigation.AuthScreen
import com.yasinmaden.ecommerceapp.navigation.Graph
import com.yasinmaden.ecommerceapp.ui.auth.login.LoginContract.UiAction
import com.yasinmaden.ecommerceapp.ui.auth.login.LoginContract.UiEffect
import com.yasinmaden.ecommerceapp.ui.auth.login.LoginContract.UiState
import com.yasinmaden.ecommerceapp.ui.components.EmptyScreen
import com.yasinmaden.ecommerceapp.ui.components.LoadingBar
import com.yasinmaden.ecommerceapp.ui.theme.DarkBlue
import com.yasinmaden.ecommerceapp.ui.theme.GoogleButtonColor
import com.yasinmaden.ecommerceapp.ui.theme.LightBlue
import com.yasinmaden.ecommerceapp.ui.theme.Pink40

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun LoginScreen(
    navController: NavHostController,
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                is UiEffect.NavigateToSignUp -> {
                    navController.navigate(AuthScreen.SignUp.route)
                }

                is UiEffect.NavigateToHome -> {
                    navController.navigate(Graph.HOME)
                }

                is UiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
    when {
        uiState.isLoading -> LoadingBar()
        uiState.list.isNotEmpty() -> EmptyScreen()
        else -> LoginContent(
            uiState = uiState,
            onAction = onAction,
            viewModel = LoginViewModel(
                firebaseAuthRepository = viewModel.firebaseAuthRepository,
                googleAuthRepository = viewModel.googleAuthRepository
            )
        )
    }
}

@Composable
fun LoginContent(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
    viewModel: LoginViewModel
) {
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account: GoogleSignInAccount? =
                GoogleSignIn.getSignedInAccountFromIntent(result.data).result
            account?.idToken?.let { idToken ->
                onAction(UiAction.OnGoogleSignIn(idToken))
            }
        } catch (e: Exception) {
            UiEffect.ShowToast(e.message.toString())
        }

    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top, // Điều chỉnh căn dọc trong cột
            modifier = Modifier.padding(16.dp)
                .padding(top = 180.dp)
        ) {
            Text(
                text = "Chào mừng bạn đến với ShineOn",
                style = MaterialTheme.typography.titleLarge,
                color = DarkBlue,
                modifier = Modifier.padding(bottom = 5.dp) // Tạo khoảng cách 5.dp
            )

            Text(
                text = "Đăng nhập để tiếp tục",
                fontSize = 13.sp,
                style = MaterialTheme.typography.bodySmall,
                color = LightBlue
            )
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { onAction(UiAction.OnEmailChange(it)) },
                label = { Text(text = "Email", fontSize = 13.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { onAction(UiAction.OnPasswordChange(it)) },
                label = { Text(text = "Mật khẩu", fontSize = 13.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp), // Đảm bảo có khoảng cách nếu cần
                    contentAlignment = Alignment.Center // Căn giữa nội dung bên trong
                ) {
                    Text(
                        text = "Tạo tài khoản",
                        fontSize = 15.sp,
                        color = LightBlue,
                        modifier = Modifier.clickable { onAction(UiAction.OnSignUpClick) }
                    )
                }
//                Text(
//                    text = "Forgot Password?",
//                    fontSize = 15.sp,
//                    color = Red,
//                    modifier = Modifier
//                        .padding(top = 8.dp)
//                        .clickable { onAction(UiAction.OnForgotClick) }
//                )
            }
//            Text(
//                "Or login with social account",
//                color = Gray,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//
//            Button(
//            onClick = { googleSignInLauncher.launch(viewModel.onGoogleSignInIntent()) },
//                shape = RoundedCornerShape(10.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = GoogleButtonColor),
//                modifier = Modifier
//                    .padding(top = 16.dp)
//                    .fillMaxWidth()
//                    .size(height = 55.dp, width = 150.dp)
//
//            ) {
//                Row(
//                    Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center
//
//                ) {
//                    Image(
//                        painter = painterResource(R.drawable.google_logo),
//                        null,
//                        modifier = Modifier.size(15.dp),
//                        colorFilter = ColorFilter.tint(White)
//                    )
//                    Text(
//                        text = "Google",
//                        modifier = Modifier.padding(start = 8.dp),
//                        fontSize = 17.sp
//                    )
//                }
//            }
//
//
       }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp)
                .padding(bottom = 80.dp),

            horizontalAlignment = Alignment.CenterHorizontally // Canh giữa các nút theo chiều ngang
        ) {
            Button(
                onClick = { onAction(UiAction.OnLoginClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .size(height = 55.dp, width = 150.dp)
            ) {
                Text(text = "Đăng nhập", fontSize = 17.sp, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách giữa hai nút

            Button(
                onClick = { onAction(UiAction.OnAnonymousSignIn) },
                modifier = Modifier
                    .fillMaxWidth()
                    .size(height = 55.dp, width = 150.dp),
                shape = RoundedCornerShape(50.dp), // Bo tròn cả hai bên nút
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7faedd)) // Đổi màu nền thành LightBlue

            ) {
                Text("Đăng nhập tài khoản khách",fontSize = 17.sp, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(
    @PreviewParameter(LoginScreenPreviewProvider::class) uiState: UiState,
) {
    LoginScreen(
        navController = NavHostController(LocalContext.current),
        uiState = uiState,
        uiEffect = emptyFlow(),
        onAction = {},
    )
}

