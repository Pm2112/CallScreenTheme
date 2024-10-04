package com.example.callscreenapp.process

import android.content.Context
import android.content.SharedPreferences

object SharePreferenceHelper {
    private const val PREF_NAME = "app_preferences"

    // Hàm khởi tạo SharedPreferences
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Hàm lưu String
    fun saveString(context: Context, key: String, value: String) {
        val sharedPreferences = getPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Hàm lấy String
    fun getString(context: Context, key: String, defaultValue: String = ""): String {
        val sharedPreferences = getPreferences(context)
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    // Hàm lưu Boolean
    fun saveBoolean(context: Context, key: String, value: Boolean) {
        val sharedPreferences = getPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    // Hàm lấy Boolean
    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        val sharedPreferences = getPreferences(context)
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Hàm lưu Int
    fun saveInt(context: Context, key: String, value: Int) {
        val sharedPreferences = getPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    // Hàm lấy Int
    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        val sharedPreferences = getPreferences(context)
        return sharedPreferences.getInt(key, defaultValue)
    }

    // Hàm lưu Float
    fun saveFloat(context: Context, key: String, value: Float) {
        val sharedPreferences = getPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    // Hàm lấy Float
    fun getFloat(context: Context, key: String, defaultValue: Float = 0f): Float {
        val sharedPreferences = getPreferences(context)
        return sharedPreferences.getFloat(key, defaultValue)
    }

    // Hàm xóa một giá trị theo key
    fun remove(context: Context, key: String) {
        val sharedPreferences = getPreferences(context)
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    // Hàm xóa tất cả dữ liệu
    fun clearAll(context: Context) {
        val sharedPreferences = getPreferences(context)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}