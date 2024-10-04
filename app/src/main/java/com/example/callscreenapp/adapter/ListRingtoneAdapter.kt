package com.example.callscreenapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.callscreenapp.Impl.OnItemClickListener
import com.example.callscreenapp.R
import com.example.callscreenapp.model.ListAvatar
import com.example.callscreenapp.model.ListCallButton
import com.example.callscreenapp.model.ListRingtone
import com.example.callscreenapp.model.ListTopic
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store

class ListRingtoneAdapter (private val nameItem: List<ListRingtone>, private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ListRingtoneAdapter.ListRingtoneViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    class ListRingtoneViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameRingtone: TextView = view.findViewById(R.id.list_ringtone_title)
        val iconCheck: ImageView = view.findViewById(R.id.list_ringtone_icon_check)
        val iconPlay: ImageView = view.findViewById(R.id.list_ringtone_icon_play)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRingtoneViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_ringtone, parent, false)
        return ListRingtoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListRingtoneViewHolder, position: Int) {
        val itemTopic = nameItem[position]


        holder.nameRingtone.text = itemTopic.nameRingtone

        holder.iconCheck.isSelected = (position == selectedPosition)
        holder.iconCheck.setOnClickListener() {
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            val previousItem = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousItem)
            notifyItemChanged(selectedPosition)
        }

        holder.iconPlay.setOnClickListener() {
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            val previousItem = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousItem)
            notifyItemChanged(selectedPosition)

            itemClickListener.onItemClick(holder.adapterPosition, itemTopic.ringtoneUrl, itemTopic.nameRingtone)
        }

        holder.nameRingtone.setOnClickListener() {
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            val previousItem = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousItem)
            notifyItemChanged(selectedPosition)

            itemClickListener.onItemClick(holder.adapterPosition, itemTopic.ringtoneUrl, itemTopic.nameRingtone)
        }


//        holder.itemView.setOnClickListener() {
//            val currentPosition = holder.adapterPosition
//            if (currentPosition == RecyclerView.NO_POSITION) {
//                return@setOnClickListener
//            }
//
//            val previousItem = selectedPosition
//            selectedPosition = holder.adapterPosition
//            notifyItemChanged(previousItem)
//            notifyItemChanged(selectedPosition)
//
////            itemClickListener.onItemClick(holder.adapterPosition, itemTopic.ringtoneUrl, itemTopic.nameRingtone)
//        }
    }

    override fun getItemCount(): Int = nameItem.size
}