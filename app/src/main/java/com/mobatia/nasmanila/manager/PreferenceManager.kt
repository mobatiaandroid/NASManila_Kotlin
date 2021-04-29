package com.mobatia.nasmanila.manager

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
    fun setUserID(context: Context?, userID: String) {
        val sharedPreferences: SharedPreferences =
            context!!.getSharedPreferences(sharedPrefNas, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("userId", userID)
        editor.apply()
    }
    fun getUserId(context: Context): String {
        var userId: String
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(sharedPrefNas, Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "").toString()
        return userId
    }
    fun setAccessToken(context: Context?, accessToken: String) {
        val sharedPreferences: SharedPreferences =
            context!!.getSharedPreferences(sharedPrefNas, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("access_token", accessToken)
        editor.apply()
    }
    fun getAccessToken(context: Context?): String {
        var tokenValue: String
        val sharedPreferences: SharedPreferences =
            context!!.getSharedPreferences(sharedPrefNas, Context.MODE_PRIVATE)
        tokenValue = sharedPreferences.getString("access_token", "").toString()
        return tokenValue
    }

    fun setUserEmail(context: Context?, emailID: String) {
            val sharedPreferences = context!!.getSharedPreferences(sharedPrefNas,
                    Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("user_email", emailID)
            editor.apply()

    }

}