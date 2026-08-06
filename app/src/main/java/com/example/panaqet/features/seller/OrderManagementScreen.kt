package com.example.panaqet.features.seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaqet.domain.model.Order
import com.example.panaqet.domain.model.OrderStatus
import com.example.panaqet.core.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderManagementScreen(
    onOrderClick: (String) -> Unit,
    viewModel: OrderManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getOrders("s1")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Order Management") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is OrderManagementUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is OrderManagementUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is OrderManagementUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.orders) { order ->
                            OrderItem(
                                order = order,
                                onStatusUpdate = { viewModel.updateStatus(order.id, it) },
                                onDeliveryUpdate = { viewModel.updateDeliveryStatus(order.id, it) },
                                onClick = { onOrderClick(order.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(
    order: Order,
    onStatusUpdate: (OrderStatus) -> Unit,
    onDeliveryUpdate: (String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Order #${order.id.takeLast(6)}", style = MaterialTheme.typography.titleMedium)
                StatusBadge(status = order.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            val firstItem = order.items.firstOrNull()
            if (firstItem != null) {
                Text(text = "${firstItem.productName ?: "Product"} ${if (order.items.size > 1) "+${order.items.size - 1} more" else ""}", style = MaterialTheme.typography.bodyLarge)
            }
            Text(text = "Buyer ID: ${order.buyerId.takeLast(6)}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "Total: ${CurrencyUtils.formatCedi(order.totalAmount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (order.deliveryStatus != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Delivery: ${order.deliveryStatus}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (order.status == OrderStatus.PENDING) {
                    Button(onClick = { onStatusUpdate(OrderStatus.CONFIRMED) }) {
                        Text("Confirm")
                    }
                    OutlinedButton(
                        onClick = { onStatusUpdate(OrderStatus.CANCELLED) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Decline")
                    }
                }
                if (order.status == OrderStatus.CONFIRMED) {
                    Button(onClick = { onStatusUpdate(OrderStatus.SHIPPED) }) {
                        Text("Mark Shipped")
                    }
                    
                    OutlinedButton(onClick = { onDeliveryUpdate("Picked up by Courier") }) {
                        Text("Update Delivery")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: OrderStatus) {
    val color = when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.tertiary
        OrderStatus.CONFIRMED -> MaterialTheme.colorScheme.primary
        OrderStatus.SHIPPED -> MaterialTheme.colorScheme.secondary
        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primaryContainer
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
