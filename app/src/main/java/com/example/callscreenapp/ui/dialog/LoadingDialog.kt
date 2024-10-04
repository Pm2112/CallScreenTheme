package com.example.callscreenapp.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import com.example.callscreenapp.R

@SuppressLint("InflateParams")
class LoadingDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_loading, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)  // Ngăn người dùng hủy bỏ dialog bằng cách chạm bên ngoài
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}