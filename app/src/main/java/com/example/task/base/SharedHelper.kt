package com.example.task.base

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import javax.inject.Inject

class SharedHelper @Inject constructor(private val application: Application){

    companion object {
        private lateinit var preferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private const val PREF_USER = "USER"
        private const val PREF_CACHE = "CACHE"
    }

    private var gson = Gson()

    fun putInUser(Key: String, Value: String) {
        preferences = application.getSharedPreferences(PREF_USER, Activity.MODE_PRIVATE)
        editor = preferences.edit()
        editor.putString(Key, Value)
        editor.apply()
    }


    fun getFromUser(Key: String): String {
        preferences = application.getSharedPreferences(PREF_USER, Activity.MODE_PRIVATE)
        return preferences.getString(Key, "")!!
    }

    fun clearUser() {
        preferences = application.getSharedPreferences(PREF_USER, Activity.MODE_PRIVATE)
        preferences.edit().clear().apply()
    }

    fun putInCache(Key: String, Value: String) {
        preferences = application.getSharedPreferences(PREF_USER, Activity.MODE_PRIVATE)
        editor = preferences.edit()
        editor.putString(Key, Value)
        editor.apply()
    }

    fun getFromCache(Key: String): String {
        preferences = application.getSharedPreferences(PREF_USER, Activity.MODE_PRIVATE)
        return preferences.getString(Key, "")!!
    }

    fun clearCache() {
        preferences = application.getSharedPreferences(PREF_CACHE, Activity.MODE_PRIVATE)
        preferences.edit().clear().apply()
    }

    fun putModel(model: Any, Key: String) {
        preferences = application.getSharedPreferences(PREF_USER, Activity.MODE_PRIVATE)
        editor = preferences.edit()
        val type = gson.toJson(model)
        editor.putString(Key, type)
        editor.apply()

    }

    fun getModel(Key: String): String {
        preferences = application.getSharedPreferences(PREF_USER, Activity.MODE_PRIVATE)
        return preferences.getString(Key, "")!!
    }
}