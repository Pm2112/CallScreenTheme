package com.example.callscreenapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.callscreenapp.R
import com.example.callscreenapp.model.ListCallButton
import com.example.callscreenapp.model.ShowCall

class ShowCallAdapter (private val itemIcon: List<ShowCall>) : RecyclerView.Adapter<ShowCallAdapter.ShowCallViewHolder>() {

    private var selectedPosition = 0  // Lưu vị trí được chọn

    class ShowCallViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconCallShowGreen: ImageView = view.findViewById(R.id.show_image_activity_icon_green)
        val iconCallShowRed: ImageView = view.findViewById(R.id.show_image_activity_icon_red)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowCallViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_show_image, parent, false)
        return ShowCallViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowCallViewHolder, position: Int) {
        val itemBtn = itemIcon[position]
        Glide.with(holder.itemView.context).load(itemBtn.urlIconGreen).into(holder.iconCallShowGreen)
        Glide.with(holder.itemView.context).load(itemBtn.urlIconRed).into(holder.iconCallShowRed)
    }

    override fun getItemCount(): Int = itemIcon.size
}