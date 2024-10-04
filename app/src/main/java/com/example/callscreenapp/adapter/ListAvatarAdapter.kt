package com.example.callscreenapp.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.callscreenapp.R
import com.example.callscreenapp.model.ListAvatar
import com.example.callscreenapp.model.ListRingtone
import com.example.callscreenapp.model.ListTopic
import com.example.callscreenapp.process.displayImageFromInternalStorage
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store

class ListAvatarAdapter(
    private val itemAvatar: List<ListAvatar>,
    private val pickImageLauncher: ActivityResultLauncher<String>
) :
    RecyclerView.Adapter<ListAvatarAdapter.ListAvatarViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION  // Lưu vị trí được chọn

    class ListAvatarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconAvatar: ImageView = view.findViewById(R.id.list_avatar_image_icon)
        val selectionIcon: ImageView = view.findViewById(R.id.list_avatar_image_icon_active)
        val border: View = view.findViewById(R.id.selection_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAvatarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_avatar_image, parent, false)
        return ListAvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListAvatarViewHolder, position: Int) {
        val item = itemAvatar[position]
        if (position != 0) {
            if (isHttpsUrl(item.urlAvatar)) {
                Log.d("ListAvatarAdapter", "Image Uri: ${item.urlAvatar}")
                Glide.with(holder.itemView.context).load(item.urlAvatar).into(holder.iconAvatar)
            } else {
                Glide.with(holder.itemView.context).load(item.urlAvatar).into(holder.iconAvatar)
            }
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.icon_plus_small)
                .into(holder.iconAvatar)
        }

        // Update the selection icon and border based on the current selected position
        holder.selectionIcon.setImageDrawable(
            if (position == selectedPosition && position != 0) ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.icon_btn_avatar_active
            )
            else null
        )
        holder.border.isSelected = (position == selectedPosition && position != 0)

        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }

            if (currentPosition == 0) {
                // Launch the image picker to select an image if the first item is clicked
                pickImageLauncher.launch("image/*")
            } else {
                // Update selected position and refresh items to reflect selection changes
                val previousItem = selectedPosition
                selectedPosition = currentPosition
                notifyItemChanged(previousItem)  // Refresh the previously selected item
                notifyItemChanged(selectedPosition)  // Refresh the currently selected item

                store.dispatch(AppAction.SetAvatarUrl(item.urlAvatar))
            }
        }
    }

    private fun isHttpsUrl(url: String): Boolean {
        return url.startsWith("https", ignoreCase = true)
    }

    override fun getItemCount(): Int = itemAvatar.size
}