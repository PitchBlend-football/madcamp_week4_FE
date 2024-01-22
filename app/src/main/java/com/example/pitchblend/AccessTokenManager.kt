package com.example.pitchblend

import android.content.Context
import android.content.SharedPreferences

// AccessTokenManager.kt (AccessToken을 저장 및 불러오는 역할을 하는 클래스)
class AccessTokenManager(private val context: Context) {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences("AccessTokenPrefs", Context.MODE_PRIVATE)
    }

    fun saveAccessToken(accessToken: String) {
        preferences.edit().putString("access_token", accessToken).apply()
    }

    fun getAccessToken(): String? {
        return preferences.getString("access_token", null)
    }

    fun clearAccessToken() {
        preferences.edit().remove("access_token").apply()
    }
}
