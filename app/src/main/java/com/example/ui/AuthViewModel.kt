package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("levelup_prefs", Context.MODE_PRIVATE)

    // Using simple local state instead of Firebase Auth
    private val _userName = MutableStateFlow(prefs.getString("user_name", null))
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _avatarUri = MutableStateFlow(prefs.getString("avatar_uri", null))
    val avatarUri: StateFlow<String?> = _avatarUri.asStateFlow()

    fun signInLocal(name: String) {
        if (name.isNotBlank()) {
            prefs.edit().putString("user_name", name).apply()
            _userName.value = name
        }
    }

    fun updateAvatar(photoUri: android.net.Uri) {
        val uriString = photoUri.toString()
        prefs.edit().putString("avatar_uri", uriString).apply()
        _avatarUri.value = uriString
    }

    fun signOut(context: Context) {
        // Clear local preferences for "Change User"
        prefs.edit().clear().apply()
        _userName.value = null
        _avatarUri.value = null
    }
}
