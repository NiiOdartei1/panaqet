package com.example.panaqet.features.marketplace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.panaqet.core.util.shareText
import com.example.panaqet.core.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.getProduct(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Product Details") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is ProductDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProductDetailUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is ProductDetailUiState.Success -> {
                    val product = state.product
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = product.name, style = MaterialTheme.typography.headlineMedium)
                            Text(
                                text = CurrencyUtils.formatCedi(product.price),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = product.description ?: "", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Button(
                                onClick = { viewModel.addToCart(product) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add to Cart")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedButton(
                                onClick = { viewModel.addToWishlist(product.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save to Wishlist")
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedButton(
                                onClick = { /* TODO: Navigate to Chat */ },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Message Seller")
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedButton(
                                onClick = {
                                    val link = "https://panaqet.com/p/${product.id}?ref=affiliate_123"
                                    context.shareText("Promote ${product.name}: $link")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Promote this Product")
                            }
                        }
                    }
                }
            }
        }
    }
}
