package com.example.callscreenapp.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.callscreenapp.R

class InternetDialog {
    fun showNetworkCustomDialog(context: Context) {
        // Tạo builder cho dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_internet, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<TextView>(R.id.dialog_title).text = "Lost internet connection"
        dialogView.findViewById<TextView>(R.id.dialog_message).text =
            "You need to reconnect to the network to continue using"
        // Thiết lập các sự kiện click cho các nút trong dialog

        val btnSubmit: CardView = dialogView.findViewById(R.id.btn_ok)
        btnSubmit.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show() // Hiển thị dialog
    }

}

public fun showNetworkCustomDialog(context: Context) {
    // Tạo builder cho dialog
    val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_internet, null)
    val builder = AlertDialog.Builder(context)
    builder.setView(dialogView)
    val alertDialog = builder.create()
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialogView.findViewById<TextView>(R.id.dialog_title).text = "Lost internet connection"
    dialogView.findViewById<TextView>(R.id.dialog_message).text =
        "You need to reconnect to the network to continue using"
    // Thiết lập các sự kiện click cho các nút trong dialog

    val btnSubmit: CardView = dialogView.findViewById(R.id.btn_ok)
    btnSubmit.setOnClickListener {
        alertDialog.dismiss()
    }

    alertDialog.show() // Hiển thị dialog
}