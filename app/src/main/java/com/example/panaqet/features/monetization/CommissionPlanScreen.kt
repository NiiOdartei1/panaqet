package com.example.panaqet.features.monetization

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommissionPlanScreen(
    onBack: () -> Unit,
    viewModel: CommissionPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Commission Plans") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Plan")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is CommissionUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CommissionUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is CommissionUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.plans) { plan ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                ListItem(
                                    headlineContent = { Text(plan.planName) },
                                    supportingContent = { Text("${plan.commissionRate}% - ${plan.description ?: ""}") },
                                    trailingContent = {
                                        Badge(containerColor = if (plan.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error) {
                                            Text(if (plan.isActive) "Active" else "Inactive")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (showDialog) {
                CreatePlanDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { name, rate, desc ->
                        viewModel.createPlan(name, rate, desc)
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun CreatePlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Commission Plan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Plan Name") })
                OutlinedTextField(value = rate, onValueChange = { rate = it }, label = { Text("Commission Rate (%)") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                val rateDouble = rate.toDoubleOrNull() ?: 0.0
                onConfirm(name, rateDouble, desc) 
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
