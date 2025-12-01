package com.example.reto12.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.reto12.data.GitHubUser
import com.example.reto12.viewmodel.AuthState
import com.example.reto12.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    authState: AuthState
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1117),
                        Color(0xFF161B22)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (authState) {
                is AuthState.Idle -> {
                    IdleContent(onLoginClick = { viewModel.startAuthentication(context) })
                }
                is AuthState.Loading -> {
                    LoadingContent()
                }
                is AuthState.Success -> {
                    SuccessContent(user = authState.user, onLogoutClick = { viewModel.resetAuth() })
                }
                is AuthState.Error -> {
                    ErrorContent(
                        message = authState.message,
                        onRetryClick = { viewModel.resetAuth() }
                    )
                }
            }
        }
    }
}

@Composable
fun IdleContent(onLoginClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // GitHub Logo (usando un icono simple)
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "GitHub",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D1117)
            )
        }

        Text(
            text = "Bienvenido",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Inicia sesión con tu cuenta de GitHub\npara continuar",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF238636)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Iniciar sesión con GitHub",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Esta app no almacena contraseñas.\nLa autenticación es manejada por GitHub.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = Color(0xFF238636),
            strokeWidth = 4.dp
        )

        Text(
            text = "Autenticando...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        Text(
            text = "Por favor completa la autenticación\nen el navegador",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SuccessContent(user: GitHubUser, onLogoutClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Ícono de éxito animado
        AnimatedVisibility(
            visible = true,
            enter = scaleIn() + fadeIn()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF238636), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Text(
            text = "¡Autenticación Exitosa!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF238636)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Avatar del usuario
        Image(
            painter = rememberAsyncImagePainter(user.avatarUrl),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Card con información del usuario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161B22)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UserInfoRow(label = "Usuario", value = user.login)
                if (user.name != null) {
                    UserInfoRow(label = "Nombre", value = user.name)
                }
                if (user.email != null) {
                    UserInfoRow(label = "Email", value = user.email)
                }
                if (user.location != null) {
                    UserInfoRow(label = "Ubicación", value = user.location)
                }

                HorizontalDivider(
                    color = Color.Gray.copy(alpha = 0.3f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "Repos", value = user.publicRepos.toString())
                    StatItem(label = "Seguidores", value = user.followers.toString())
                    StatItem(label = "Siguiendo", value = user.following.toString())
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Cerrar sesión",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ErrorContent(message: String, onRetryClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFDA3633), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Error",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Text(
            text = "Autenticación Fallida",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFDA3633)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161B22)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Error:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Button(
            onClick = onRetryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF238636)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Reintentar",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun UserInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF238636)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

