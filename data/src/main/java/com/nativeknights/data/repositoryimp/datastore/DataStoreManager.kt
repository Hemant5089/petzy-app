package com.nativeknights.data.repositoryimp.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.userDataStore by preferencesDataStore("user_prefs")

class DataStoreManager @Inject constructor(
          private val context: Context
) {
    companion object {
         private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val isLoggedIn : Flow<Boolean> = context.userDataStore.data.map {
         it[IS_LOGGED_IN] ?: false
    }

    suspend fun setLoggedIn(value: Boolean){
         context.userDataStore.edit { prefs ->
              prefs[IS_LOGGED_IN] = value
         }
    }
}