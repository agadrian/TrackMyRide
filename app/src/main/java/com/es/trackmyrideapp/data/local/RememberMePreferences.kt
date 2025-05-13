package com.es.trackmyrideapp.data.local

import android.content.Context

class RememberMePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun setRememberMe(value: Boolean) {
        prefs.edit().putBoolean("remember_me", value).apply()
    }

    fun isRememberMe(): Boolean {
        return prefs.getBoolean("remember_me", false)
    }

    fun clearRememberMe() {
        prefs.edit().putBoolean("remember_me", false).apply()
    }
}