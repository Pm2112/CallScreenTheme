package com.example.callscreenapp.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.callscreenapp.Impl.NetworkChangeListener
import com.example.callscreenapp.R
import com.example.callscreenapp.model.DataCate
import com.example.callscreenapp.model.PhoneCallListImage
import com.example.callscreenapp.permission.checkContactsPermission
import com.example.callscreenapp.permission.checkPhoneCallPermission
import com.example.callscreenapp.permission.hasWriteSettingsPermission
import com.example.callscreenapp.permission.isDefaultDialer
import com.example.callscreenapp.process.DataManager.Companion.dataCategory
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store
import com.example.callscreenapp.service.NetworkChangeReceiver
import com.example.callscreenapp.ui.activity.MainActivity
import com.example.callscreenapp.ui.activity.ShowImageActivity
import com.example.callscreenapp.ui.dialog.showNetworkCustomDialog
import com.example.callscreenapp.ui.fragment.premission.PremissionSheetFragment
import com.example.callscreenapp.ui.fragment.show_image.ShowImageFragment

class PhoneCallListImageAdapter(
    private val images: List<PhoneCallListImage>,
    private val fragmentManager: FragmentManager
) :
    RecyclerView.Adapter<PhoneCallListImageAdapter.ListImageViewHolder>() {

    private var storeSubscription: (() -> Unit)? = null
    private var selectedPosition = RecyclerView.NO_POSITION  // Lưu vị trí được chọn
    private lateinit var firebase: EvenFirebase
    class ListImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.list_call_theme_image)
        val iconAnswer: ImageView = view.findViewById(R.id.list_call_theme_icon_answer)
        val iconReject: ImageView = view.findViewById(R.id.list_call_theme_icon_reject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListImageViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_call_themes_image, parent, false)


        return ListImageViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ListImageViewHolder, position: Int) {
        val imageItem = images[position]

        val currentPosition = holder.adapterPosition
        // Sử dụng Glide để tải hình ảnh từ URL vào ImageView
        Glide.with(holder.itemView.context).load(imageItem.backgroundUrl).into(holder.imageView)
        Glide.with(holder.itemView.context).load(imageItem.iconAnswer).into(holder.iconAnswer)
        Glide.with(holder.itemView.context).load(imageItem.iconReject).into(holder.iconReject)

        firebase = EvenFirebase(holder.itemView.context)

        // Đặt sự kiện click cho view của ViewHolder
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val networkChangeReceiver = NetworkChangeReceiver(null)
            if (!networkChangeReceiver.isNetworkAvailable(context)) {
                showNetworkCustomDialog(context)
                return@setOnClickListener
            }

            val isPhoneCallPermissionGranted = checkPhoneCallPermission(holder.itemView.context)
            val isContactsPermissionGranted = checkContactsPermission(holder.itemView.context)
            val isDefaultDialer = isDefaultDialer(holder.itemView.context)
            val isWriteSettingsPermission = hasWriteSettingsPermission(holder.itemView.context)

            if (isPhoneCallPermissionGranted && isContactsPermissionGranted && isDefaultDialer && isWriteSettingsPermission) {
                val intent = Intent(holder.itemView.context, ShowImageActivity::class.java)

                store.dispatch(AppAction.SetAvatarUrl(imageItem.avatarUrl))
                store.dispatch(AppAction.SetBackgroundUrl(imageItem.backgroundUrl))
                store.dispatch(AppAction.SetIconCallShowId(imageItem.iconAnswer, imageItem.iconReject))
                holder.itemView.context.startActivity(intent)
//                store.dispatch(AppAction.ShowImageFragment(true))
//                showCustomDialog(holder.itemView.context, imageItem.backgroundUrl)
            } else {
                store.dispatch(AppAction.SetAvatarUrl(imageItem.avatarUrl))
                store.dispatch(AppAction.SetBackgroundUrl(imageItem.backgroundUrl))
                store.dispatch(AppAction.SetIconCallShowId(imageItem.iconAnswer, imageItem.iconReject))
                val permissionSheet = PremissionSheetFragment()
                permissionSheet.show(fragmentManager, permissionSheet.tag)
            }

//             Update selected position and refresh items to reflect selection changes
            val previousItem = selectedPosition
            selectedPosition = currentPosition
            notifyItemChanged(previousItem)  // Refresh the previously selected item
            notifyItemChanged(selectedPosition)  // Refresh the currently selected item

            val themeType = extractSegmentFromUrl(imageItem.backgroundUrl)

            firebase.logFirebaseParamEvent("select_theme",  "theme_type","$themeType")
        }
    }

    fun extractSegmentFromUrl(url: String): String? {
        // Kiểm tra nếu URL chứa phần đặc biệt "Data_background/All/all"
        return if (url.contains("Data_background/All/all")) {
            // Gọi hàm riêng để xử lý khi URL chứa đoạn "Data_background/All/all"
            handleSpecialCase(url)
        } else {
            // Tiếp tục xử lý bình thường nếu không chứa "Data_background/All/all"
            val start = "https://callthemetest.s3.amazonaws.com/Data_background/"
            val startIndex = url.indexOf(start)

            // Nếu không tìm thấy start, trả về null
            if (startIndex == -1) return null

            // Cắt chuỗi từ sau phần "https://callthemetest.s3.amazonaws.com/Data_Background/"
            val remainingUrl = url.substring(startIndex + start.length)

            // Tìm vị trí dấu "/" đầu tiên sau đoạn đã cắt
            val endIndex = remainingUrl.indexOf("/")

            // Nếu không có "/", trả về null
            if (endIndex == -1) return null

            // Lấy chuỗi từ đầu đến trước dấu "/"
            remainingUrl.substring(0, endIndex)
        }
    }

    // Hàm xử lý khi gặp "Data_background/All/all"
    fun handleSpecialCase(url: String): String? {
        val start = url.substringAfterLast("_")
        val number = start.substringBefore(".webp").toIntOrNull() ?: return null // Chuyển đổi thành Int

        // Lấy tên từ dataCategory ở vị trí number - 1
        return dataCategory.value?.getOrNull(number - 1)?.name ?: "Unknown"
    }

    override fun getItemCount() = images.size

}