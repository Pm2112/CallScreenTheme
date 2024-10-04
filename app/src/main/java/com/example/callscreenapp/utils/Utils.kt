package com.example.callscreenapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView

fun Context.getDimension(value: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value, this.resources.displayMetrics).toInt()
}

fun TextView.showStrikeThrough(show: Boolean) {
    paintFlags =
        if (show) paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
}

object DisplayUtils {

    data class DisplaySize(
        val height: Int,
        val width: Int,
    )

    private fun Context.getDeviceDisplaySize(): DisplaySize {
        val height: Int
        val width: Int
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = wm.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            height = metrics.bounds.height() - insets.bottom - insets.top
            width = metrics.bounds.width() - insets.left - insets.right

        } else {
            val displayMetrics = DisplayMetrics()

            @Suppress("DEPRECATION")
            val display = wm.defaultDisplay // deprecated in API 30
            @Suppress("DEPRECATION")
            display.getMetrics(displayMetrics) // deprecated in API 30

            height = displayMetrics.heightPixels
            width = displayMetrics.widthPixels
        }
        return DisplaySize(height = height, width = width)
    }

    fun Context.getDeviceDisplayWidth(reductionPercent: Float = 0.16f): Int {
        val size = getDeviceDisplaySize()
        return (size.width - (reductionPercent * size.width)).toInt()
    }

    fun Context.getDeviceDisplayHeight(reductionPercent: Float = 0.16f): Int {
        val size = getDeviceDisplaySize()
        return (size.height - (reductionPercent * size.height)).toInt()
    }

}