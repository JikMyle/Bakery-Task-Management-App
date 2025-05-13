package com.example.bakerytaskmanagementapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

class AdminDataStore(
    private val context: Context
) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("admin_settings")
        private val PASSWORD_KEY = stringPreferencesKey("password")
    }

    suspend fun changePassword(password:String) {
        context.dataStore.edit { preferences ->
            preferences[PASSWORD_KEY] = password
        }
    }

    suspend fun checkIfPasswordMatch(password: String): Boolean {
        return context.dataStore.data.firstOrNull()?.get(PASSWORD_KEY) == password
    }
}