package com.example.panaqet.features.affiliate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.panaqet.core.util.shareText
import com.example.panaqet.domain.model.AffiliateStats
import com.example.panaqet.core.util.CurrencyUtils
import com.example.panaqet.core.ui.components.PanaQetStatCard
import com.example.panaqet.core.ui.components.PanaQetTopBar

@Composable
fun AffiliateDashboardScreen(
    onBrowseProducts: () -> Unit,
    viewModel: AffiliateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            PanaQetTopBar(title = "Affiliate Dashboard")
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is AffiliateUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AffiliateUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is AffiliateUiState.Success -> {
                    AffiliateContent(
                        stats = state.stats,
                        onBrowseProducts = onBrowseProducts,
                        onGenerateMockLink = {
                            viewModel.generateLink("prod_1", "Sample Product") { link ->
                                context.shareText("Check out this product: $link")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AffiliateContent(
    stats: AffiliateStats,
    onBrowseProducts: () -> Unit,
    onGenerateMockLink: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Business Performance", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PanaQetStatCard(
                    label = "Earnings",
                    value = CurrencyUtils.formatCedi(stats.earnings),
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
                PanaQetStatCard(
                    label = "Referrals",
                    value = stats.totalReferrals.toString(),
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PanaQetStatCard(
                    label = "Code",
                    value = stats.referralCode,
                    icon = Icons.Default.QrCode,
                    modifier = Modifier.weight(1f)
                )
                PanaQetStatCard(
                    label = "Pending",
                    value = CurrencyUtils.formatCedi(stats.pendingCommission),
                    icon = Icons.Default.Info,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Promote Products", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Select a product from the marketplace to generate your tracking link.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onBrowseProducts,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Browse Products to Promote")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onGenerateMockLink,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share Quick Promo Link")
            }
        }
    }
}
