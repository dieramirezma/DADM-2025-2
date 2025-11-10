package com.example.reto11.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reto11.data.ChatMessage
import com.example.reto11.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(apiKey: String?) {
    val viewModel: ChatViewModel = viewModel(
        key = apiKey ?: "no_key"
    ) {
        ChatViewModel(apiKey)
    }
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    // Show error snackbar
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // Error will be shown in the UI
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Asistente de Cocina 👨‍🍳",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        // Error Banner
        error?.let { errorMsg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Cerrar")
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }

            if (isLoading) {
                item {
                    TypingIndicator()
                }
            }
        }

        // Input Field
        MessageInput(
            messageText = messageText,
            onMessageTextChange = { messageText = it },
            onSendClick = {
                if (messageText.isNotBlank() && !isLoading) {
                    viewModel.sendMessage(messageText)
                    messageText = ""
                }
            },
            enabled = !isLoading && apiKey != null
        )
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(horizontal = 4.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            if (message.isUser) {
                // Mensajes del usuario: texto simple
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp
                )
            } else {
                // Mensajes del asistente: renderizar Markdown
                Text(
                    text = parseMarkdown(message.text),
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Escribiendo...",
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun MessageInput(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp, max = 120.dp),
                placeholder = { Text("Escribe un mensaje...") },
                enabled = enabled,
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            FloatingActionButton(
                onClick = {
                    if (enabled && messageText.isNotBlank()) {
                        onSendClick()
                    }
                },
                modifier = Modifier.size(56.dp),
                containerColor = if (enabled && messageText.isNotBlank()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                },
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar"
                )
            }
        }
    }
}

/**
 * Parsea texto Markdown y lo convierte en AnnotatedString con estilos
 * Soporta: **negritas**, *cursivas*, listas con - o *, listas numeradas
 */
fun parseMarkdown(text: String): AnnotatedString {
    val builder = AnnotatedString.Builder()
    var i = 0

    while (i < text.length) {
        when {
            // Negritas: **texto**
            i + 1 < text.length && text[i] == '*' && text[i + 1] == '*' -> {
                val endIndex = text.indexOf("**", i + 2)
                if (endIndex != -1) {
                    val boldText = text.substring(i + 2, endIndex)
                    builder.withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldText)
                    }
                    i = endIndex + 2
                } else {
                    builder.append(text[i])
                    i++
                }
            }
            // Cursivas: *texto* (solo si no es **)
            text[i] == '*' && (i + 1 >= text.length || text[i + 1] != '*') -> {
                // Buscar el siguiente * que no sea parte de **
                var endIndex = -1
                for (j in i + 1 until text.length) {
                    if (text[j] == '*' && (j + 1 >= text.length || text[j + 1] != '*')) {
                        endIndex = j
                        break
                    }
                }
                if (endIndex != -1 && endIndex > i + 1) {
                    val italicText = text.substring(i + 1, endIndex)
                    builder.withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(italicText)
                    }
                    i = endIndex + 1
                } else {
                    builder.append(text[i])
                    i++
                }
            }
            // Listas con - o * al inicio de línea
            (text.startsWith("- ", i) || text.startsWith("* ", i)) &&
            (i == 0 || text[i - 1] == '\n') -> {
                builder.append("• ")
                i += 2
            }
            // Listas numeradas: 1. 2. etc. al inicio de línea
            (i == 0 || text[i - 1] == '\n') &&
            text[i].isDigit() &&
            i + 1 < text.length &&
            text[i + 1] == '.' &&
            (i + 2 >= text.length || text[i + 2] == ' ') -> {
                // Encontrar el final del número
                var numEnd = i + 2
                while (numEnd < text.length && text[numEnd].isDigit()) {
                    numEnd++
                }
                if (numEnd < text.length && text[numEnd] == '.') {
                    numEnd++
                    if (numEnd < text.length && text[numEnd] == ' ') {
                        builder.append(text.substring(i, numEnd))
                        i = numEnd
                    } else {
                        builder.append(text[i])
                        i++
                    }
                } else {
                    builder.append(text[i])
                    i++
                }
            }
            // Carácter normal
            else -> {
                builder.append(text[i])
                i++
            }
        }
    }

    return builder.toAnnotatedString()
}
