package com.example.reto12.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reto12.auth.GitHubAuthManager
import com.example.reto12.data.GitHubUser
import com.example.reto12.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: GitHubUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val apiService = RetrofitClient.gitHubApiService

    companion object {
        private const val TAG = "AuthViewModel"
    }

    /**
     * Inicia el flujo de autenticación OAuth
     */
    fun startAuthentication(context: Context) {
        try {
            GitHubAuthManager.startOAuthFlow(context)
            _authState.value = AuthState.Loading
        } catch (e: Exception) {
            Log.e(TAG, "Error al iniciar OAuth", e)
            _authState.value = AuthState.Error("Error al abrir el navegador: ${e.message}")
        }
    }

    /**
     * Procesa el código de autorización recibido del callback
     */
    fun handleAuthorizationCode(code: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                Log.d(TAG, "Intercambiando código por token...")

                // Intercambiar el código por un access token
                val tokenResponse = apiService.getAccessToken(
                    clientId = GitHubAuthManager.getClientId(),
                    clientSecret = GitHubAuthManager.getClientSecret(),
                    code = code
                )

                Log.d(TAG, "Token obtenido exitosamente")

                // Obtener información del usuario
                val user = apiService.getCurrentUser(
                    authorization = "Bearer ${tokenResponse.accessToken}"
                )

                Log.d(TAG, "Usuario obtenido: ${user.login}")

                _authState.value = AuthState.Success(user)

            } catch (e: Exception) {
                Log.e(TAG, "Error en autenticación", e)
                _authState.value = AuthState.Error(
                    "Error al autenticar: ${e.message ?: "Error desconocido"}"
                )
            }
        }
    }

    /**
     * Resetea el estado de autenticación
     */
    fun resetAuth() {
        _authState.value = AuthState.Idle
    }
}

