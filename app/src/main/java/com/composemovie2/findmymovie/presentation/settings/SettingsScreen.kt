package com.composemovie2.findmymovie.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.composemovie2.findmymovie.domain.model.Country
import com.composemovie2.findmymovie.domain.model.ProviderInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var countryDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Region Selection
            item {
                Text("Watch Region", style = MaterialTheme.typography.titleMedium)
                Box {
                    OutlinedTextField(
                        value = uiState.countries.find { it.isoCode == uiState.currentPreferences.selectedRegion }?.name ?: uiState.currentPreferences.selectedRegion,
                        onValueChange = {}, // Not directly editable
                        readOnly = true,
                        label = { Text("Selected Region") },
                        trailingIcon = { Icon(if (countryDropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, "Expand") },
                        modifier = Modifier.fillMaxWidth().clickable { countryDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = countryDropdownExpanded,
                        onDismissRequest = { countryDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f) // Adjust width
                    ) {
                        if (uiState.isLoadingCountries) {
                            DropdownMenuItem(text = { Text("Loading countries...") }, onClick = {})
                        }
                        uiState.countries.forEach { country ->
                            DropdownMenuItem(
                                text = { Text(country.name) },
                                onClick = {
                                    viewModel.updateSelectedRegion(country.isoCode)
                                    countryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                if (uiState.countriesError != null) {
                    Text("Error loading countries: ${uiState.countriesError}", color = MaterialTheme.colorScheme.error)
                }
            }

            // Subscribed Providers Selection
            item {
                Text("My Subscribed Streaming Services", style = MaterialTheme.typography.titleMedium)
                if (uiState.isLoadingProviders) {
                    CircularProgressIndicator()
                }
                if (uiState.providersError != null) {
                    Text("Error loading providers: ${uiState.providersError}", color = MaterialTheme.colorScheme.error)
                }
                // Display providers in a couple of columns using FlowRow or similar if many, or just LazyColumn items
                // For simplicity, using simple column items. A FlowRow would be better for many.
                Column {
                     uiState.providers.filter { it.logoPath.isNotBlank() }.take(20).forEach { provider -> // Show some, filter ones with logos
                        ProviderSubscriptionItem(
                            provider = provider,
                            isSelected = uiState.currentPreferences.subscribedProviderIds.contains(provider.providerId.toString()),
                            onToggle = { viewModel.toggleSubscriptionProvider(provider.providerId.toString()) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderSubscriptionItem(
    provider: ProviderInfo,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            AsyncImage(
                model = provider.logoPath,
                contentDescription = provider.providerName,
                modifier = Modifier.size(40.dp).padding(end = 8.dp),
                contentScale = ContentScale.Fit
            )
            Text(provider.providerName, style = MaterialTheme.typography.bodyLarge)
        }
        if (isSelected) {
            Icon(Icons.Filled.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
        }
    }
}
