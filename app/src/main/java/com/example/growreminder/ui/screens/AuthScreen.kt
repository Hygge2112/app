package com.example.growreminder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.growreminder.R
import com.example.growreminder.Sign_in.AuthViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
    onSignInSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val signInError by viewModel.signInError.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Handle sign-in success
    LaunchedEffect(user) {
        user?.let {
            onSignInSuccess()
        }
    }

    LaunchedEffect(signInError) {
        signInError?.let { error ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(error)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "UTH Logo",
                        modifier = Modifier.size(100.dp)
                    )
                    if (user == null) {
                        SignInContent(
                            onSignInClick = { viewModel.signIn() }
                        )
                    } else {
                        UserProfileContent(
                            user = user,
                            onSignOutClick = { viewModel.signOut() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SignInContent(
    onSignInClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Sign In",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Welcome to UTH Smart Tasks",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )
        Button(onClick = onSignInClick) {
            Text("Sign in with Google")
        }
    }
}

@Composable

private fun UserProfileContent(
    user: FirebaseUser?,
    onSignOutClick: () -> Unit
) {
    val profilePainter = rememberAsyncImagePainter(
        model = user?.photoUrl
    )
    @Composable
    fun AuthScreen(
        viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
        onSignInSuccess: () -> Unit = {}
    ) {
        val user by viewModel.user.collectAsStateWithLifecycle()

        LaunchedEffect(user) {
            user?.let {
                onSignInSuccess() // Điều hướng khi đăng nhập thành công
            }
        }

        // Phần UI giữ nguyên...
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = user?.photoUrl,
            contentDescription = "Profile Picture",
            placeholder = profilePainter,
            fallback = profilePainter,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Text(
            text = "Welcome, ${user?.displayName ?: "User"}",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = user?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(onClick = onSignOutClick) {
            Text("Sign Out")
        }
    }
}
