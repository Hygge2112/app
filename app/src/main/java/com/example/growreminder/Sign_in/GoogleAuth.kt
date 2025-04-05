package com.example.growreminder.Sign_in

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val context: Context,
    private val credentialManager: CredentialManager = CredentialManager.create(context)
) : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _signInError = MutableStateFlow<String?>(null)
    val signInError: StateFlow<String?> = _signInError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        _user.value = auth.currentUser
    }

    fun signIn() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Create Google ID Option - key change: don't filter by authorized accounts
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(context.getString(com.example.growreminder.R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false) // Changed to false
                    .setAutoSelectEnabled(false) // Changed to false to show account picker
                    .build()

                // Create Credential Request
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // Attempt to get credentials
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                // Process the retrieved credential
                processSignInResult(result)
            } catch (e: GetCredentialException) {
                handleSignInError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleSignInError(e: Exception) {
        val errorMessage = when (e) {
            is NoCredentialException -> {
                Log.d("AuthViewModel", "No credential selected or available")
                "No account selected. Please try again and select an account."
            }
            is GetCredentialException -> "Credential retrieval error: ${e.message}"
            else -> "Sign-in failed: ${e.message}"
        }

        Log.e("AuthViewModel", "Sign-in error", e)
        _signInError.value = errorMessage
    }

    private fun processSignInResult(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Extract Google ID token credential
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                        // Authenticate with Firebase
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: Exception) {
                        _signInError.value = "Invalid Google ID token: ${e.message}"
                        Log.e("AuthViewModel", "Google ID token error", e)
                    }
                } else {
                    _signInError.value = "Unsupported credential type"
                }
            }
            else -> {
                _signInError.value = "Unknown credential type"
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _signInError.value = null
                    Log.d("AuthViewModel", "Google sign-in successful")
                } else {
                    _user.value = null
                    _signInError.value = "Firebase authentication failed: ${task.exception?.message}"
                    Log.e("AuthViewModel", "Firebase sign-in failed", task.exception)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
        _signInError.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]?.applicationContext
                    ?: throw IllegalArgumentException("Application context not found")
                AuthViewModel(context)

            }
        }
    }

}