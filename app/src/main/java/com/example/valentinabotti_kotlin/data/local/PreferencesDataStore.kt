package com.example.valentinabotti_kotlin.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object PreferencesDataStore {
    // Estensione per il DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    // Chiavi
    private val SID_KEY = stringPreferencesKey("sid")
    private val UID_KEY = intPreferencesKey("uid")
    private val PAGE_KEY = stringPreferencesKey("page")
    private val MID_KEY = intPreferencesKey("mid")
    private val OID_KEY = intPreferencesKey("oid")

    // Funzione per salvare il sid nel preferences
    suspend fun saveSid(context: Context, sid: String): Result<Unit> {
        return try {
            context.dataStore.edit { settings ->
                settings[SID_KEY] = sid
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Funzione per leggere il sid nel preferences
    suspend fun getSid(context: Context): String? {
        return try {
            val preferences = context.dataStore.data.first()
            val sid = preferences[SID_KEY]
            Log.d("PreferencesDataStore", "SID retrieved: $sid")
            sid
        } catch (e: Exception) {
            Log.e("PreferencesDataStore", "Error retrieving SID", e)
            null
        }
    }

    // Funzione per salvare l'uid nel preferences
    suspend fun saveUid(context: Context, uid: Int): Result<Unit> {
        return try {
            context.dataStore.edit { settings ->
                settings[UID_KEY] = uid
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Funzione per leggere l'uid nel preferences
    suspend fun getUid(context: Context): Int? {
        return try {
            val preferences = context.dataStore.data.first()
            val uid = preferences[UID_KEY]
            Log.d("PreferencesDataStore", "UID retrieved: $uid")
            uid
        } catch (e: Exception) {
            Log.e("PreferencesDataStore", "Error retrieving UID", e)
            null
        }
    }

    //salva la pagina corrente nel preferences
    suspend fun savePage(context: Context, page: String): Result<Unit> {
        return try {
            context.dataStore.edit { settings ->
                settings[PAGE_KEY] = page
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Funzione per leggere la pagina nel preferences
    suspend fun getPage(context: Context): Result<String?> {
        return try {
            val preferences = context.dataStore.data.first()
            Result.success(preferences[PAGE_KEY])
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveMid(context: Context, mid: Int): Result<Unit> {
        return try {
            context.dataStore.edit { settings ->
                settings[MID_KEY] = mid
            }
            Log.d("PreferencesDataStore", "MID saved: $mid")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMid(context: Context): Int? {
        return try {
            val preferences = context.dataStore.data.first()
            val mid = preferences[MID_KEY]
            Log.d("PreferencesDataStore", "MID retrieved: $mid")
            mid
        } catch (e: Exception) {
            Log.e("PreferencesDataStore", "Error retrieving MID", e)
            null
        }
    }

    suspend fun saveOid(context: Context, oid: Int): Result<Unit> {
        return try {
            context.dataStore.edit { settings ->
                settings[OID_KEY] = oid
            }
            Log.d("PreferencesDataStore", "OID saved: $oid")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOid(context: Context): Int? {
        return try {
            val preferences = context.dataStore.data.first()
            val oid = preferences[OID_KEY]
            Log.d("PreferencesDataStore", "OID retrieved: $oid")
            oid
        } catch (e: Exception) {
            Log.e("PreferencesDataStore", "Error retrieving OID", e)
            null
        }
    }
}