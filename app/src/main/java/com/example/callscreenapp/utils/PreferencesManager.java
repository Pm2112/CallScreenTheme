package com.example.callscreenapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.callscreenapp.AppOwner;

public class PreferencesManager {

    private SharedPreferences sharedPrefs;

    public static String checkLifetime() {
        SharedPreferences sharedPref = getPrefs();
        return sharedPref.getString("APP_IAP_LIFETIME", null);
    }

    public static String checkRemoveAds() {
        SharedPreferences sharedPref = getPrefs();
        return sharedPref.getString("APP_REMOVE_ADS", null);
    }

    public static String checkSUB() {
        SharedPreferences sharedPref = getPrefs();
        return sharedPref.getString("APP_SUB_PRO", null);
    }

    public static void purchaseLifetime() {
        SharedPreferences sharedPref = getPrefs();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("APP_IAP_LIFETIME", "IAP_SUCCESS");
        editor.apply();
    }

    public static void purchaseAndRestoreSuccess(){
        SharedPreferences sharedPref = getPrefs();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("APP_SUB_PRO", "SUB_SUCCESS");
        editor.putString("APP_REMOVE_ADS", "REMOVE");
        editor.apply();
    }

    public static void purchaseFailed() {
        SharedPreferences sharedPref = getPrefs();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("APP_SUB_PRO");
        editor.remove("APP_REMOVE_ADS");
        editor.remove("APP_IAP_LIFETIME");
        editor.apply();
    }

    public static void saveCounterAds(String key, long count){
        SharedPreferences preferences = getPrefs();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, count);
        editor.apply();
    }
    public static long getCounterAds(String key) {
        SharedPreferences preferences = getPrefs();
        return preferences.getLong(key, 0);
    }

    public static void saveShowOnBoard(boolean isSave){
        SharedPreferences preferences = getPrefs();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isShowOnBoard", isSave);
        editor.apply();
    }
    public static boolean isShowOnBoard() {
        SharedPreferences preferences = getPrefs();
        return preferences.getBoolean("isShowOnBoard", false);
    }

    public static void deleteCounterAds(String key) {
        SharedPreferences preferences = getPrefs();
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void saveTimeShowAppOpen(long count){
        SharedPreferences preferences = getPrefs();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("TimeShowAppOpen", count);
        editor.apply();
    }
    public static long getTimeShowAppOpen() {
        SharedPreferences preferences = getPrefs();
        return preferences.getLong("TimeShowAppOpen", 0);
    }
    public static void saveCountRewardAds(int id, long count){
        SharedPreferences preferences = getPrefs();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("CountRewardAds" + id, count);
        editor.apply();
    }
    public static long getCountRewardAds(int id) {
        SharedPreferences preferences = getPrefs();
        return preferences.getLong("CountRewardAds" + id, 0);
    }

    public static void saveTotalRewardAds(int id, long count){
        SharedPreferences preferences = getPrefs();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("TotalRewardAds" + id, count);
        editor.apply();
    }

    public static long getTotalRewardAds(int id) {
        SharedPreferences preferences = getPrefs();
        return preferences.getLong("TotalRewardAds" + id, 5);
    }

    public static SharedPreferences getPrefs() {
        return getPrefs("aivirtualfriend");//PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    public static SharedPreferences getPrefs(String key) {
        return AppOwner.Companion.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
    }

}
