package com.example.donat_demo2.utils

import android.content.Context
import com.example.donat_demo2.model.UserModel

class SharedPrefUtils(var context: Context) {

    private val SHAREDPREF = "shared_pref"

    fun saveUser(userID: String, phoneNumber: String) {
        val sharedPreferences = context.getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userID", userID)
        editor.putString("phoneNumber", phoneNumber)
        editor.putBoolean("loggedIn", true)
        editor.apply()
    }


    fun getUserNumber(): UserModel {
        val sharedPreferences = context.getSharedPreferences(SHAREDPREF, Context.MODE_PRIVATE)
        val userID = sharedPreferences.getString("userID", "")
        val phoneNumber = sharedPreferences.getString("phoneNumber", "")
        return UserModel(userID, phoneNumber)
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