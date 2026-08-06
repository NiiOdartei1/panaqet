package com.example.panaqet.features.checkout

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paystack.android.ui.paymentsheet.PaymentSheet
import com.paystack.android.ui.paymentsheet.PaymentSheetResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    totalPrice: Double,
    onOrderSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val paymentSheet = remember {
        PaymentSheet(context as ComponentActivity) { result ->
            if (result is PaymentSheetResult.Completed) {
                viewModel.onPaymentSuccess()
            }
        }
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is CheckoutUiState.PaymentReady) {
            paymentSheet.launch(state.accessCode)
        } else if (state is CheckoutUiState.Success) {
            onOrderSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Total: GH₵ ${String.format("%.2f", totalPrice)}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email for Receipt") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            if (uiState is CheckoutUiState.Error) {
                Text(
                    text = (uiState as CheckoutUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { viewModel.preparePayment(email, totalPrice) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is CheckoutUiState.Loading
            ) {
                if (uiState is CheckoutUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Pay with Paystack")
                }
            }
        }
    }
}
