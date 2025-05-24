package com.composemovie2.findmymovie.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Locale // For default region
import javax.inject.Inject
import javax.inject.Singleton

// Define DataStore instance at the top level (Context extension)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_movie_preferences")

data class UserWatchPreferences(
    val selectedRegion: String,
    val subscribedProviderIds: Set<String>
)

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_REGION = stringPreferencesKey("selected_watch_region")
        val SUBSCRIBED_PROVIDER_IDS = stringSetPreferencesKey("subscribed_provider_ids")
    }

    val userWatchPreferencesFlow: Flow<UserWatchPreferences> = context.dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val selectedRegion = preferences[PreferencesKeys.SELECTED_REGION] ?: Locale.getDefault().country.uppercase(Locale.US)
            val subscribedProviderIds = preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS] ?: emptySet()
            UserWatchPreferences(selectedRegion, subscribedProviderIds)
        }

    suspend fun updateSelectedRegion(region: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_REGION] = region.uppercase(Locale.US)
        }
    }

    suspend fun updateSubscribedProviders(providerIds: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS] = providerIds
        }
    }
    
    // Helper to add a single provider
    suspend fun addSubscribedProvider(providerId: String) {
        context.dataStore.edit { preferences ->
            val currentSubscribed = preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS] ?: emptySet()
            preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS] = currentSubscribed + providerId
        }
    }

    // Helper to remove a single provider
    suspend fun removeSubscribedProvider(providerId: String) {
        context.dataStore.edit { preferences ->
            val currentSubscribed = preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS] ?: emptySet()
            preferences[PreferencesKeys.SUBSCRIBED_PROVIDER_IDS] = currentSubscribed - providerId
        }
    }
}
