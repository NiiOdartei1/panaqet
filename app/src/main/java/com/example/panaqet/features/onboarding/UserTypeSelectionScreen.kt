package com.example.panaqet.features.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.panaqet.R
import com.example.panaqet.domain.model.UserRole

@Composable
fun UserTypeSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    onBackToLogin: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.panaqet_logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            alpha = 0.1f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to PanaQet",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "How would you like to use the platform?",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            RoleCard(
                title = "I want to Shop",
                description = "Browse and buy products/services",
                icon = Icons.Default.ShoppingCart,
                onClick = { onRoleSelected(UserRole.BUYER) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RoleCard(
                title = "I want to Sell",
                description = "List your products and services",
                icon = Icons.Default.AccountCircle,
                onClick = { onRoleSelected(UserRole.SELLER) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RoleCard(
                title = "I want to Promote",
                description = "Promote items and earn commissions",
                icon = Icons.Default.Star,
                onClick = { onRoleSelected(UserRole.AFFILIATE) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TextButton(onClick = onBackToLogin) {
                Text("Already have an account? Login")
            }
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
