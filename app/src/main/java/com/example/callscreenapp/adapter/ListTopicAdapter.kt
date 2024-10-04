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
import com.example.callscreenapp.Impl.CategoryAdsClickListener
import com.example.callscreenapp.Impl.CategoryClickListener
import com.example.callscreenapp.R
import com.example.callscreenapp.model.ListTopic
import com.example.callscreenapp.process.saveCountClickCategory

class ListTopicAdapter(
    private val itemTopic: List<ListTopic>,
    private val categoryClickListener: CategoryClickListener
) : RecyclerView.Adapter<ListTopicAdapter.ListTopicViewHolder>() {

    private var selectedPosition = 0  // Lưu vị trí được chọn

    class ListTopicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.list_topic_card_text)
        val iconViewImage: ImageView = view.findViewById(R.id.list_topic_card_icon)
        val layout: CardView = view.findViewById(R.id.list_topic_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListTopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_topic_image, parent, false)
        return ListTopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListTopicViewHolder, position: Int) {
        val itemTopic = itemTopic[position]
        holder.textViewName.text = itemTopic.nameTopic

        // Set background based on selection
        val cardColor = if (position == selectedPosition) R.color.topic_active else R.color.white
        holder.layout.setCardBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                cardColor
            )
        )
        val textColor = if (position == selectedPosition) R.color.white else R.color.topic_text
        holder.textViewName.setTextColor(
            ContextCompat.getColorStateList(
                holder.itemView.context,
                textColor
            )
        )
        val iconTopic =
            if (position == selectedPosition) R.drawable.icon_topic_active else R.drawable.icon_topic
        holder.iconViewImage.setImageDrawable(
            ContextCompat.getDrawable(
                holder.itemView.context,
                iconTopic
            )
        )

        // Handle item clicks
        holder.itemView.setOnClickListener {
            val previousItem = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousItem)  // Refresh the previously selected item
            notifyItemChanged(selectedPosition)  // Refresh the currently selected item

            categoryClickListener.onCategoryItemClicked(itemTopic.nameTopic)
            // Cuộn RecyclerView để mục được chọn hiển thị ở giữa
            (holder.itemView.parent as? RecyclerView)?.layoutManager?.let {
                if (it is LinearLayoutManager) {
                    it.scrollToPositionWithOffset(
                        position,
                        150
                    )  // Giá trị 150 là offset để căn chỉnh mục đích
                }
            }
        }
    }

    override fun getItemCount(): Int = itemTopic.size
}