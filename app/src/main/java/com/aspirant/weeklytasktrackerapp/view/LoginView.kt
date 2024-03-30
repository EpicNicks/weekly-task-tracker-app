package com.aspirant.weeklytasktrackerapp.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.lifecycle.ViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.aspirant.weeklytasktrackerapp.model.auth.AuthService
import com.aspirant.weeklytasktrackerapp.model.entity.response.ApiResponse
import com.aspirant.weeklytasktrackerapp.view.common.AnimatedLinearGradientBackground
import com.aspirant.weeklytasktrackerapp.view.common.OutlinedTextFieldBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel) {

    val (focusRequester) = FocusRequester.createRefs()
    val isLoading by rememberUpdatedState(newValue = viewModel.isLoading)
    val errorMsg by rememberUpdatedState(newValue = viewModel.errorMsg)

    AnimatedLinearGradientBackground(
        colors = listOf(
            Color(0xFF579ED1), // #579ed1
            Color(0xFF1FC8DB), // #1fc8db
            // third color breaks it because it lerps from first to last
//            Color(0xFF579ED1)  // #579ed1
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Adjust padding as needed
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1F73AE), shape = RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .height(100.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "FreeTime",
                        color = Color.White,
                        fontSize = 42.sp,
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                OutlinedTextFieldBackground(color = Color.White) {
                    OutlinedTextField(
                        value = viewModel.username,
                        onValueChange = { username -> viewModel.updateUsername(username) },
                        label = { Text("Username") },
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .fillMaxWidth()
                    )
                }
                OutlinedTextFieldBackground(color = Color.White) {
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { password ->
                            viewModel.updatePassword(password)
                        },
                        label = { Text("Password") },
                        enabled = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.login() }
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .fillMaxWidth()
                    )
                }
                Text(errorMsg, color = Color.Red)
                Button(
                    onClick = { viewModel.login() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Login")
                }
                Button(
                    onClick = { viewModel.register() },
                    colors = ButtonColors(
                        contentColor = Color(0xFF1F73AE),
                        containerColor = Color.White,
                        disabledContentColor = Color(0x901F73AE),
                        disabledContainerColor = Color.Gray,
                    ),
                    enabled = !isLoading,
                    modifier = Modifier
                        .border(width = 1.5.dp, color = Color(0xFF1F73AE), shape = RoundedCornerShape(32.dp))
                        .fillMaxWidth()
                ) {
                    Text("Register")
                }
            }
        }
    }
}


class LoginViewModel(
    private val authService: AuthService,
    private val onNavigateToTaskTracker: () -> Unit,
    private val onNavigateToRegister: () -> Unit
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var errorMsg by mutableStateOf("")
        private set
    var username by mutableStateOf("")
        private set

    init {
        if (authService.getAuthToken() != null) {
            onNavigateToTaskTracker()
        }
    }

    fun updateUsername(input: String) {
        username = input
    }

    var password by mutableStateOf("")
        private set

    fun updatePassword(input: String) {
        password = input
    }

    fun register() {
        onNavigateToRegister()
    }

    fun login() {
        viewModelScope.launch {
            errorMsg = ""
            isLoading = true
            authService.login(username, password, onLoginResponse = { loginResponse ->
                val loginResponseTag = "login Response"
                if (loginResponse != null) {
                    when (loginResponse) {
                        is ApiResponse.Success -> {
                            Log.i(loginResponseTag, "token: ${loginResponse.value}")
                            onNavigateToTaskTracker()
                        }

                        is ApiResponse.Failure -> {
                            Log.e(loginResponseTag, "error: ${loginResponse.error}")
                            errorMsg = "Incorrect password provided"
                        }
                    }
                } else {
                    Log.e(loginResponseTag, "response was null")
                }
                isLoading = false
            })
        }
    }
}
