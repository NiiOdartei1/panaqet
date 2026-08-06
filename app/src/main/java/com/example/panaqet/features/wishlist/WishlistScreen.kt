package com.example.panaqet.features.wishlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaqet.core.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onProductClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Wishlist") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is WishlistUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WishlistUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is WishlistUiState.Success -> {
                    if (state.products.isEmpty()) {
                        Text(
                            text = "Your wishlist is empty",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
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
                                        supportingContent = { Text(CurrencyUtils.formatCedi(product.price)) },
                                        trailingContent = {
                                            Row {
                                                IconButton(onClick = { viewModel.addToCart(product.id) }) {
                                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Add to Cart")
                                                }
                                                IconButton(onClick = { viewModel.removeFromWishlist(product.id) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                                }
                                            }
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
