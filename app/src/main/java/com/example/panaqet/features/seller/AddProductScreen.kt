package com.example.panaqet.features.seller

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaqet.core.util.toFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddProductViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("New") }
    var location by remember { mutableStateOf("") }
    var isPackage by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(uiState) {
        if (uiState is AddProductUiState.Success) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add New Product") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Condition selection (Simplified for now)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Condition: ")
                FilterChip(
                    selected = condition == "New",
                    onClick = { condition = "New" },
                    label = { Text("New") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = condition == "Used",
                    onClick = { condition = "Used" },
                    label = { Text("Used") }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isPackage, onCheckedChange = { isPackage = it })
                Text("Is Package Product")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { launcher.launch("image/*") }) {
                Text(if (selectedImageUri == null) "Select Image" else "Image Selected")
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState is AddProductUiState.Error) {
                Text(
                    text = (uiState as AddProductUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    val file = selectedImageUri?.toFile(context)
                    viewModel.addProduct(
                        name, description, price, category, stock, file,
                        condition, location, isPackage
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AddProductUiState.Loading
            ) {
                if (uiState is AddProductUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("List Product")
                }
            }
        }
    }
}
