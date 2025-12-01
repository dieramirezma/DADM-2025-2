package com.example.reto12

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.reto12.auth.GitHubAuthManager
import com.example.reto12.ui.screens.AuthScreen
import com.example.reto12.ui.theme.Reto12Theme
import com.example.reto12.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Verificar si es un callback de OAuth
        handleOAuthCallback(intent)

        setContent {
            Reto12Theme {
                AuthApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthCallback(intent)
    }

    private fun handleOAuthCallback(intent: Intent?) {
        if (GitHubAuthManager.isOAuthCallback(intent)) {
            val code = GitHubAuthManager.extractAuthorizationCode(intent)

            if (code != null) {
                Log.d(TAG, "Código de autorización recibido")
                viewModel.handleAuthorizationCode(code)
            } else {
                Log.e(TAG, "No se pudo extraer el código de autorización")
                // Podrías manejar el error aquí si lo deseas
            }
        }
    }
}

@Composable
fun AuthApp(viewModel: AuthViewModel) {
    val authState by viewModel.authState.collectAsState()

    AuthScreen(
        viewModel = viewModel,
        authState = authState
    )
}