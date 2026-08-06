package com.example.panaqet.features.seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaqet.core.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    onAddProductClick: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: SellerDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSellerProducts("s1")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Inventory") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProductClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is SellerDashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SellerDashboardUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is SellerDashboardUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.products) { product ->
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
            }
        }
    }
}
