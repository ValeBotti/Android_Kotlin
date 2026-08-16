package com.example.valentinabotti_kotlin.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.valentinabotti_kotlin.data.local.PreferencesDataStore
import com.example.valentinabotti_kotlin.data.remote.ApiCalls
import com.example.valentinabotti_kotlin.model.Screen
import com.example.valentinabotti_kotlin.model.UidSid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val _sidUid = MutableLiveData<UidSid?>()
    val sidUid: LiveData<UidSid?> get() = _sidUid

    fun retriveSidUid(context: Context) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val sid = PreferencesDataStore.getSid(context)
                    val uid = PreferencesDataStore.getUid(context)

                    if (!sid.isNullOrBlank() && uid != null && uid > 0) {
                        Log.d("MainActivity_ViewModel", "SID: $sid, UID: $uid")
                        UidSid(sid, uid)
                    } else {
                        Log.d("MainActivity_ViewModel", "Failed to retrieve SID and UID from preferenceStorage.")
                        createUser(context)
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity_ViewModel", "Error during sid and uid retrieval: ${e.message}")
                    null
                }
            }
            _sidUid.postValue(result)
        }
    }

    private suspend fun createUser(context: Context): UidSid? {
        return try {
            val uidSid = ApiCalls.postCreateUser()
            Log.d("MainActivity_ViewModel", "sid and uid retrieved from server: $uidSid")

            if (uidSid.sid != null && uidSid.uid != null) {
                PreferencesDataStore.saveSid(context, uidSid.sid)
                PreferencesDataStore.saveUid(context, uidSid.uid)
            } else {
                Log.d("MainActivity_ViewModel", "sid or uid null (server): $uidSid")
            }
            uidSid
        } catch (e: Exception) {
            Log.e("MainActivity_ViewModel", "Error during user creation: ${e.message}")
            null
        }
    }
}

//SALVA LA PAGINA CORRENTE QUANDO L'APPLICAZIONE VA IN BACKGROUND
fun saveCurrentPage(context: Context, currentPage: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            if (currentPage != Screen.Splash.route && currentPage.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    PreferencesDataStore.savePage(context, currentPage)
                }
                Log.d("NavigationObserver", "Current page saved: $currentPage")
            } else {
                Log.d("NavigationObserver", "Current page is not saved because it is the splash screen or empty")
            }
        } catch (e: Exception) {
            Log.e("NavigationObserver", "Error during page save: ${e.message}")
        }
    }
}

//RECUPERA LA PAGINA SLAVATA QUANDO L'APPLICAZIONE TORNA IN FOREGROUND
fun retrieveCurrentPage(context: Context, onResult: (String?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = try {
            val currentPageResult = PreferencesDataStore.getPage(context)
            currentPageResult.getOrNull()?.takeIf { it != Screen.Splash.route }
        } catch (e: Exception) {
            Log.e("NavigationObserver", "Error during page retrieval: ${e.message}")
            null
        }
        withContext(Dispatchers.Main) {
            onResult(result)
        }
    }
}
