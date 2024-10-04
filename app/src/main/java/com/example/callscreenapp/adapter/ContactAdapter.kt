package com.example.callscreenapp.adapter

import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.callscreenapp.Impl.ContactItemClickListener
import com.example.callscreenapp.R
import com.example.callscreenapp.model.Contact
import com.example.callscreenapp.model.ListAvatar
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store

class ContactAdapter(
    private val itemContact: List<Contact>,
    private val itemClickListener: ContactItemClickListener
) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION  // Lưu vị trí được chọn

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: ImageView = view.findViewById(R.id.contact_check_box)
        val contactName: TextView = view.findViewById(R.id.contact_name)
        val contactPhoneNumber: TextView = view.findViewById(R.id.contact_phone_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = itemContact[position]
        holder.contactName.text = item.name
        holder.contactPhoneNumber.text = item.phoneNumber
        holder.checkBox.isSelected = item.isSelected

        holder.itemView.setOnClickListener() {
            when (holder.checkBox.isSelected) {
                false -> {
                    holder.checkBox.isSelected = true
                    itemClickListener.onContactItemClicked(position)
                }

                true -> {
                    holder.checkBox.isSelected = false
                    itemClickListener.onRemoveContactItemClicked(position)
                }
            }

        }
    }


    override fun getItemCount(): Int = itemContact.size
}