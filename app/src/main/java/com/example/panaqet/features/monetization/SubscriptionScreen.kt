package com.example.panaqet.features.monetization

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
fun SubscriptionScreen(
    onBack: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Growth Plans") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is SubscriptionUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SubscriptionUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is SubscriptionUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.current != null) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = "Current Plan: ${state.current.name}", style = MaterialTheme.typography.titleMedium)
                                        Text(text = "Price: ${CurrencyUtils.formatCedi(state.current.price)}/mo")
                                    }
                                }
                            }
                        }

                        item {
                            Text(text = "Available Plans", style = MaterialTheme.typography.titleLarge)
                        }

                        items(state.plans) { plan ->
                            SubscriptionItem(
                                plan = plan,
                                isCurrent = state.current?.id == plan.id,
                                onSubscribe = { viewModel.subscribe(plan.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionItem(
    plan: com.example.panaqet.domain.model.Subscription,
    isCurrent: Boolean,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = plan.name, style = MaterialTheme.typography.titleMedium)
                Text(text = CurrencyUtils.formatCedi(plan.price), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = plan.description ?: "", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onSubscribe,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCurrent
            ) {
                Text(if (isCurrent) "Current Plan" else "Subscribe Now")
            }
        }
    }
}
