package com.example.panaqet.features.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.panaqet.domain.model.CartItem
import com.example.panaqet.core.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onCheckoutClick: (Double) -> Unit,
    onBack: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Shopping Cart") })
        },
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: ${CurrencyUtils.formatCedi(uiState.totalPrice)}",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Button(onClick = { onCheckoutClick(uiState.totalPrice) }) {
                            Text("Checkout")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(uiState.items) { item ->
                    CartItemRow(
                        item = item,
                        onQuantityChange = { viewModel.updateQuantity(item.id, it) },
                        onRemove = { viewModel.removeItem(item) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.name,
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(text = CurrencyUtils.formatCedi(item.price), style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
                    Text("-")
                }
                Text(text = item.quantity.toString())
                IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                    Text("+")
                }
            }
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}
