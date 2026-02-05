package com.jeromedusanter.restorik.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    val sortMode: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_SORT_MODE]
    }

    suspend fun setSortMode(sortMode: String) {
        dataStore.edit { preferences ->
            preferences[KEY_SORT_MODE] = sortMode
        }
    }

    val sortOrder: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_SORT_ORDER]
    }

    suspend fun setSortOrder(sortOrder: String) {
        dataStore.edit { preferences ->
            preferences[KEY_SORT_ORDER] = sortOrder
        }
    }

    private companion object {
        val KEY_SORT_MODE = stringPreferencesKey(name = "sort_mode")
        val KEY_SORT_ORDER = stringPreferencesKey(name = "sort_order")
    }
}
