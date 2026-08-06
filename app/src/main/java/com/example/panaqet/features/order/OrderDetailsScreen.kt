package com.example.panaqet.features.order

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
fun OrderDetailsScreen(
    orderId: String,
    onBack: () -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.getOrder(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Order Details") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is OrderDetailsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is OrderDetailsUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is OrderDetailsUiState.Success -> {
                    val order = state.order
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Order #${order.id.takeLast(6)}", style = MaterialTheme.typography.titleLarge)
                                    Text(text = "Status: ${order.status}", color = MaterialTheme.colorScheme.primary)
                                    Text(text = "Date: ${order.timestamp.take(16).replace("T", " ")}")
                                    if (order.deliveryStatus != null) {
                                        Text(text = "Delivery: ${order.deliveryStatus}", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }

                        item {
                            Text(text = "Items", style = MaterialTheme.typography.titleMedium)
                        }

                        items(order.items) { item ->
                            ListItem(
                                headlineContent = { Text(item.productName ?: "Product") },
                                supportingContent = { Text("Qty: ${item.quantity} x ${CurrencyUtils.formatCedi(item.totalPrice / item.quantity)}") },
                                trailingContent = { Text(CurrencyUtils.formatCedi(item.totalPrice)) }
                            )
                        }

                        item {
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total", style = MaterialTheme.typography.headlineSmall)
                                Text(text = CurrencyUtils.formatCedi(order.totalAmount), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
