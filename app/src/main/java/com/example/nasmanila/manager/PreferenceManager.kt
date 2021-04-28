package com.example.nasmanila.manager

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {
    private val sharedPrefNas = "NAS_MANILA"
    fun setIsFirstLaunch(context: Context, result: Boolean) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(sharedPrefNas, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("is_first_launch", result)
        editor.apply()
    }
    fun getIsFirstLaunch(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(sharedPrefNas, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_first_launch", true)
    }
    fun getUserId(context: Context): String {
        var userId: String = " "
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(sharedPrefNas, Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "").toString()
        return userId
    }

    fun setUserID(context: Context?, userID: String) {
        val sharedPreferences: SharedPreferences =
                context!!.getSharedPreferences(sharedPrefNas, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("userId", userID)
        editor.apply()
    }
}