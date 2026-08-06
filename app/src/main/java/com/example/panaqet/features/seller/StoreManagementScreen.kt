package com.example.panaqet.features.seller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaqet.domain.model.StoreProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreManagementScreen(
    viewModel: StoreManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getProfile("s1")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Store Management") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is StoreManagementUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is StoreManagementUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is StoreManagementUiState.Success -> {
                    StoreProfileForm(
                        profile = state.profile,
                        onSave = { viewModel.updateProfile("s1", it) }
                    )
                }
            }
        }
    }
}

@Composable
fun StoreProfileForm(
    profile: StoreProfile,
    onSave: (StoreProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile.storeName) }
    var description by remember { mutableStateOf(profile.description ?: "") }
    var address by remember { mutableStateOf(profile.address ?: "") }
    var policies by remember { mutableStateOf(profile.policies ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Store Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Store Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Business Address") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        OutlinedTextField(
            value = policies,
            onValueChange = { policies = it },
            label = { Text("Store Policies") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Button(
            onClick = { onSave(StoreProfile(name, profile.logoUrl, address, description, policies)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}
