package com.example.callscreenapp.permission

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService


fun checkPhoneCallPermission(context: Context): Boolean {
    val permission = Manifest.permission.CALL_PHONE
    val permissionResult = ContextCompat.checkSelfPermission(context, permission)
    return permissionResult == PackageManager.PERMISSION_GRANTED
}

fun checkContactsPermission(context: Context): Boolean {
    val permission = Manifest.permission.READ_CONTACTS
    val permissionResult = ContextCompat.checkSelfPermission(context, permission)
    return permissionResult == PackageManager.PERMISSION_GRANTED
}

@RequiresApi(Build.VERSION_CODES.Q)
fun isDefaultDialer(context: Context): Boolean {
    val roleManager = getSystemService(context, RoleManager::class.java)
    return roleManager?.isRoleHeld(RoleManager.ROLE_DIALER) == true
}

fun hasWriteSettingsPermission(context: Context): Boolean {
    return Settings.System.canWrite(context)
}
