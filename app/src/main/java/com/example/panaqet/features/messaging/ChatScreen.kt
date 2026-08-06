package com.example.panaqet.features.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chat") })
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") }
                    )
                    IconButton(onClick = {
                        viewModel.sendMessage(conversationId, messageText)
                        messageText = ""
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is ChatUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ChatUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is ChatUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.messages) { msg ->
                            MessageBubble(msg)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: com.example.panaqet.domain.model.Message) {
    // Basic bubble implementation
    val alignment = if (msg.senderRole == "buyer") Alignment.Start else Alignment.End
    val color = if (msg.senderRole == "buyer") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Surface(
            color = color,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = msg.content,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(text = msg.timestamp.takeLast(5), style = MaterialTheme.typography.labelSmall)
    }
}
