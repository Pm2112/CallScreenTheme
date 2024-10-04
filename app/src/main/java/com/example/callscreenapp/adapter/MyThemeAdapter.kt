package com.example.callscreenapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.Impl.ShowAdsClickListener
import com.example.callscreenapp.R
import com.example.callscreenapp.ads.TYPE_ADS
//import com.example.callscreenapp.data.realm
import com.example.callscreenapp.database.BackgroundTheme
import com.example.callscreenapp.model.MyTheme
import com.example.callscreenapp.process.removeObjectFromJson
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store
import com.example.callscreenapp.service.NetworkChangeReceiver
import com.example.callscreenapp.ui.activity.MainActivity
import com.example.callscreenapp.ui.activity.ShowImageActivity
import com.example.callscreenapp.ui.dialog.showNetworkCustomDialog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.Gson
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults


class MyThemeAdapter(
    private val images: List<MyTheme>,
    private val context: Context

) :
    RecyclerView.Adapter<MyThemeAdapter.MyThemeViewHolder>() {


    private var selectedPosition = RecyclerView.NO_POSITION  // Lưu vị trí được chọn
    private val activity: MainActivity = context as MainActivity
    private lateinit var adLoader: AdLoader

    class MyThemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.list_call_theme_image)
        val iconAnswer: ImageView = view.findViewById(R.id.list_call_theme_icon_answer)
        val iconReject: ImageView = view.findViewById(R.id.list_call_theme_icon_reject)
        val btnDelete: ImageView = view.findViewById(R.id.my_theme_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyThemeViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_my_theme, parent, false)
        return MyThemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyThemeViewHolder, position: Int) {
        val imageItem = images[position]
        val currentPosition = holder.adapterPosition
        Glide.with(holder.itemView.context).load(imageItem.backgroundUrl).into(holder.imageView)
        Glide.with(holder.itemView.context).load(imageItem.iconAnswer).into(holder.iconAnswer)
        Glide.with(holder.itemView.context).load(imageItem.iconReject).into(holder.iconReject)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val networkChangeReceiver = NetworkChangeReceiver(null)
            if (!networkChangeReceiver.isNetworkAvailable(context)) {
                showNetworkCustomDialog(context)
                return@setOnClickListener
            } else {
                val intent = Intent(holder.itemView.context, ShowImageActivity::class.java)
                store.dispatch(AppAction.SetAvatarUrl(imageItem.avatarUrl))
                store.dispatch(AppAction.SetBackgroundUrl(imageItem.backgroundUrl))
                store.dispatch(AppAction.SetIconCallShowId(imageItem.iconAnswer, imageItem.iconReject))
                holder.itemView.context.startActivity(intent)
            }
        }


        holder.btnDelete.setOnClickListener() {
            val context = holder.itemView.context
            val networkChangeReceiver = NetworkChangeReceiver(null)
            if (!networkChangeReceiver.isNetworkAvailable(context)) {
                showNetworkCustomDialog(context)
                return@setOnClickListener
            } else {
                showCustomDialog(imageItem)
            }

        }
    }

    private fun showCustomDialog(imageItem: MyTheme) {
        // Tạo builder cho dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val adContainer: FrameLayout = dialogView.findViewById(R.id.dialog_ads)
        adLoader = AdLoader.Builder(context, AppOwner.getContext().getString(R.string.native_ad_unit_id))
            .forNativeAd { nativeAd ->
                val adView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_native_ads, null) as NativeAdView
                populateNativeAdView(nativeAd, adView)
                adContainer.removeAllViews()
                adContainer.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdLoader", "Failed to load ad: ${adError.message}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

//        reviewManager = ReviewManagerFactory.create(context)

        dialogView.findViewById<TextView>(R.id.dialog_title).text = "DELETE"
        // Thiết lập các sự kiện click cho các nút trong dialog
        dialogView.findViewById<CardView>(R.id.btn_yes).setOnClickListener {
            // Thực hiện hành động khi chọn "Có"
            removeObjectFromJson(context, "mytheme.json", imageItem)
            store.dispatch(AppAction.Refresh(true))
            alertDialog.dismiss()

            activity.showPopupLoadAds(TYPE_ADS.InterstitialAd)
        }

        dialogView.findViewById<CardView>(R.id.btn_no).setOnClickListener {
            // Đóng dialog khi chọn "Không"
            alertDialog.dismiss()
        }

        alertDialog.show() // Hiển thị dialog
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.mediaView = adView.findViewById(R.id.ad_media)

        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.mediaView as MediaView).mediaContent = nativeAd.mediaContent

        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        (adView.callToActionView as TextView).text = nativeAd.callToAction

        adView.setNativeAd(nativeAd)
    }

    override fun getItemCount() = images.size
}