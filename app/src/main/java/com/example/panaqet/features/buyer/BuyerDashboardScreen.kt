package com.example.panaqet.features.buyer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaqet.core.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerDashboardScreen(
    onOrderClick: (String) -> Unit,
    viewModel: BuyerDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Buyer Dashboard") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is BuyerDashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is BuyerDashboardUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is BuyerDashboardUiState.Success -> {
                    if (state.orders.isEmpty()) {
                        Text(
                            text = "You haven't placed any orders yet",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.orders) { order ->
                                Card(
                                    onClick = { onOrderClick(order.id) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ListItem(
                                        headlineContent = { Text("Order #${order.id.takeLast(6)}") },
                                        supportingContent = { Text("Total: ${CurrencyUtils.formatCedi(order.totalAmount)} - ${order.status}") },
                                        trailingContent = {
                                            Text(text = order.timestamp.take(10), style = MaterialTheme.typography.labelSmall)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
