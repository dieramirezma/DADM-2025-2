package com.example.reto11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.reto11.ui.ApiKeyScreen
import com.example.reto11.ui.ChatScreen
import com.example.reto11.ui.theme.Reto11Theme
import com.example.reto11.util.ApiKeyManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto11Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var apiKey by remember { mutableStateOf<String?>(ApiKeyManager.getApiKey(context)) }

    if (apiKey == null) {
        ApiKeyScreen(
            onApiKeySet = { key ->
                ApiKeyManager.saveApiKey(context, key)
                apiKey = key
            }
        )
    } else {
        ChatScreen(apiKey = apiKey)
    }
}