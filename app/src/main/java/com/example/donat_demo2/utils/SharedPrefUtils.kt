package com.example.donatdemo1.utils

import android.content.Context

class SharedPrefUtils(var context: Context) {

    private val SHAREDPREF = "shared_pref"

    fun saveUser(userID: String) {
        val sharedPreferences = context.getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userID", userID)
        editor.putBoolean("loggedIn", true)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        val sharedPreferences = context.getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("loggedIn", false)
    }

    fun logOut() {
        val sharedPreferences = context.getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}