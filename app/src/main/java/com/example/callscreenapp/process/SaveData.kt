package com.example.callscreenapp.process

import android.content.Context

fun saveDialerData(context: Context, avatar: String, background: String, btnAnswer: String, btnReject: String) {
    val sharedPreferences = context.getSharedPreferences("DialerPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("avatar", avatar)
    editor.putString("background", background)
    editor.putString("btnAnswer", btnAnswer)
    editor.putString("btnReject", btnReject)
    editor.apply()
}

fun getDialerDataOne(context: Context): Pair<String?, String?> {
    val sharedPreferences = context.getSharedPreferences("DialerPref", Context.MODE_PRIVATE)
    val avatar = sharedPreferences.getString("avatar", null)
    val background = sharedPreferences.getString("background", null)
    return Pair(avatar, background)
}

fun getDialerDataTwo(context: Context): Pair<String?, String?> {
    val sharedPreferences = context.getSharedPreferences("DialerPref", Context.MODE_PRIVATE)
    val btnAnswer = sharedPreferences.getString("btnAnswer", null)
    val btnReject = sharedPreferences.getString("btnReject", null)
    return Pair(btnAnswer, btnReject)
}

fun saveVibrateSetting(context: Context, vibrate: Boolean) {
    val sharedPreferences = context.getSharedPreferences("vibratePref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("vibrate", vibrate)
    editor.putBoolean("vibrate", vibrate)
    editor.apply()
}

fun getVibrateSetting(context: Context): Pair<Boolean?, Boolean?> {
    val sharedPreferences = context.getSharedPreferences("vibratePref", Context.MODE_PRIVATE)
    val vibrate = sharedPreferences.getBoolean("vibrate", false)
    val vibrate1 = sharedPreferences.getBoolean("vibrate", false)
    return Pair(vibrate, vibrate1)
}

fun saveFlashSetting(context: Context, flash: Boolean) {
    val sharedPreferences = context.getSharedPreferences("flashPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("flash", flash)
    editor.putBoolean("flash", flash)
    editor.apply()
}

fun getFlashSetting(context: Context): Pair<Boolean?, Boolean?> {
    val sharedPreferences = context.getSharedPreferences("flashPref", Context.MODE_PRIVATE)
    val flash = sharedPreferences.getBoolean("flash", false)
    val flash1 = sharedPreferences.getBoolean("flash", false)
    return Pair(flash, flash1)
}

fun saveOnboard(context: Context, onboard: Boolean) {
    val sharedPreferences = context.getSharedPreferences("onboardPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("onboard", onboard)
    editor.putBoolean("onboard", onboard)
    editor.apply()
}
fun getOnboard(context: Context): Pair<Boolean?, Boolean?> {
    val sharedPreferences = context.getSharedPreferences("onboardPref", Context.MODE_PRIVATE)
    val onboard = sharedPreferences.getBoolean("onboard", true)
    val onboard1 = sharedPreferences.getBoolean("onboard", true)
    return Pair(onboard, onboard1)
}

fun saveRingtone(context: Context, ringtone: String) {
    val sharedPreferences = context.getSharedPreferences("ringtonePref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("ringtone", ringtone)
    editor.putString("ringtone", ringtone)
    editor.apply()
}
fun getRingtone(context: Context): Pair<String?, String?> {
    val sharedPreferences = context.getSharedPreferences("ringtonePref", Context.MODE_PRIVATE)
    val ringtone = sharedPreferences.getString("ringtone", null)
    val ringtone1 = sharedPreferences.getString("ringtone", null)
    return Pair(ringtone, ringtone1)
}

fun saveCountClickCategory(context: Context, count: Int) {
    val sharedPreferences = context.getSharedPreferences("countClickCategoryPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt("count", count)
    editor.putInt("count", count)
    editor.apply()
}

fun getCountClickCategory(context: Context): Pair<Int?, Int?> {
    val sharedPreferences = context.getSharedPreferences("countClickCategoryPref", Context.MODE_PRIVATE)
    val count = sharedPreferences.getInt("count", 0)
    val count1 = sharedPreferences.getInt("count", 0)
    return Pair(count, count1)
}



fun saveRateApp(context: Context, main: Boolean, create: Int) {
    val sharedPreferences = context.getSharedPreferences("rate_dialog", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("main", main)
    editor.putInt("create", create)
    editor.apply()
}

fun getRateAppMainCreate(context: Context): Pair<Boolean?, Int?> {
    val sharedPreferences = context.getSharedPreferences("rate_dialog", Context.MODE_PRIVATE)
    val avatar = sharedPreferences.getBoolean("main", true)
    val background = sharedPreferences.getInt("create", 0)
    return Pair(avatar, background)
}