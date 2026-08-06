package com.example.panaqet.features.seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaqet.core.util.CurrencyUtils
import com.example.panaqet.core.ui.components.PanaQetStatCard
import com.example.panaqet.core.ui.components.PanaQetTopBar

@Composable
fun SellerDashboardScreen(
    onAddProductClick: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: SellerDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val stats = remember(uiState) {
        if (uiState is SellerDashboardUiState.Success) {
            val s = (uiState as SellerDashboardUiState.Success).stats
            listOf(
                DashboardStat("Total Sales", CurrencyUtils.formatCedi(s?.totalSales ?: 0.0), Icons.Default.Payments),
                DashboardStat("Active Products", s?.activeProducts?.toString() ?: "0", Icons.AutoMirrored.Filled.List),
                DashboardStat("Pending Orders", s?.pendingOrders?.toString() ?: "0", Icons.Default.PendingActions),
                DashboardStat("Total Products", s?.totalProducts?.toString() ?: "0", Icons.Default.Inventory)
            )
        } else {
            listOf(
                DashboardStat("Total Sales", "GH₵ 0.00", Icons.Default.Payments),
                DashboardStat("Active Products", "0", Icons.AutoMirrored.Filled.List),
                DashboardStat("Pending Orders", "0", Icons.Default.PendingActions),
                DashboardStat("Total Products", "0", Icons.Default.Inventory)
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getSellerProducts("s1")
    }

    Scaffold(
        topBar = {
            PanaQetTopBar(title = "Seller Dashboard")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(text = "Business Overview", style = MaterialTheme.typography.titleLarge)
            }
            
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PanaQetStatCard(stats[0].label, stats[0].value, stats[0].icon, Modifier.weight(1f))
                        PanaQetStatCard(stats[1].label, stats[1].value, stats[1].icon, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PanaQetStatCard(stats[2].label, stats[2].value, stats[2].icon, Modifier.weight(1f))
                        PanaQetStatCard(stats[3].label, stats[3].value, stats[3].icon, Modifier.weight(1f))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Recent Products", style = MaterialTheme.typography.titleLarge)
            }

            when (val state = uiState) {
                is SellerDashboardUiState.Loading -> {
                    item { CircularProgressIndicator() }
                }
                is SellerDashboardUiState.Error -> {
                    item { Text(text = state.message, color = MaterialTheme.colorScheme.error) }
                }
                is SellerDashboardUiState.Success -> {
                    items(state.products.take(5)) { product ->
                        Card(
                            onClick = { onProductClick(product.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ListItem(
                                headlineContent = { Text(product.name) },
                                supportingContent = { Text("${CurrencyUtils.formatCedi(product.price)} - Stock: ${product.stock}") },
                                trailingContent = {
                                    Switch(
                                        checked = product.isAvailable,
                                        onCheckedChange = { viewModel.toggleAvailability(product.id, it) }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Recent Orders", style = MaterialTheme.typography.titleLarge)
            }

            item {
                Text(text = "No recent orders", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAddProductClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Product")
                }
            }
        }
    }
}

data class DashboardStat(val label: String, val value: String, val icon: ImageVector)
