package com.example.panaqet.features.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaqet.core.ui.components.PanaQetProductCard
import com.example.panaqet.core.ui.components.PanaQetTopBar
import com.example.panaqet.features.cart.CartViewModel

@Composable
fun MarketplaceScreen(
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: MarketplaceViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartUiState by cartViewModel.uiState.collectAsState()
    val cartItemCount = cartUiState.items.sumOf { it.quantity }

    Scaffold(
        topBar = {
            PanaQetTopBar(
                title = "PanaQet Shop",
                actions = {
                    IconButton(onClick = onMessagesClick) {
                        Icon(Icons.Default.Email, contentDescription = "Messages")
                    }
                    IconButton(onClick = onWishlistClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Wishlist")
                    }
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge { Text(cartItemCount.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is MarketplaceUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MarketplaceUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MarketplaceUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.products) { product ->
                            PanaQetProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
