package com.example.reto12.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.example.reto12.BuildConfig

class GitHubAuthManager {

    companion object {
        private const val GITHUB_AUTHORIZE_URL = "https://github.com/login/oauth/authorize"
        private const val REDIRECT_URI = "reto12://github-callback"
        private const val SCOPE = "read:user user:email"

        fun getClientId(): String = BuildConfig.GITHUB_CLIENT_ID
        fun getClientSecret(): String = BuildConfig.GITHUB_CLIENT_SECRET

        /**
         * Inicia el flujo de autenticación OAuth con GitHub
         * Abre Chrome Custom Tabs con la página de autorización de GitHub
         */
        fun startOAuthFlow(context: Context) {
            val authUrl = buildAuthorizationUrl()

            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()

            customTabsIntent.launchUrl(context, Uri.parse(authUrl))
        }

        /**
         * Construye la URL de autorización con los parámetros necesarios
         */
        private fun buildAuthorizationUrl(): String {
            return Uri.parse(GITHUB_AUTHORIZE_URL).buildUpon()
                .appendQueryParameter("client_id", getClientId())
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter("scope", SCOPE)
                .appendQueryParameter("state", generateRandomState())
                .build()
                .toString()
        }

        /**
         * Genera un estado aleatorio para prevenir ataques CSRF
         */
        private fun generateRandomState(): String {
            return java.util.UUID.randomUUID().toString()
        }

        /**
         * Extrae el código de autorización del Intent de callback
         */
        fun extractAuthorizationCode(intent: Intent?): String? {
            val uri = intent?.data
            return uri?.getQueryParameter("code")
        }

        /**
         * Verifica si el Intent es un callback válido de OAuth
         */
        fun isOAuthCallback(intent: Intent?): Boolean {
            val uri = intent?.data
            return uri != null &&
                   uri.scheme == "reto12" &&
                   uri.host == "github-callback"
        }
    }
}

