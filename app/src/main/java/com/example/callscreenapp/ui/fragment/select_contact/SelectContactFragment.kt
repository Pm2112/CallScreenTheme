package com.example.callscreenapp.ui.fragment.select_contact

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.Impl.ShowAdsClickListener
import com.example.callscreenapp.R
import com.example.callscreenapp.ads.TYPE_ADS
import com.example.callscreenapp.model.Contact
import com.example.callscreenapp.model.ContactData
import com.example.callscreenapp.model.MyTheme
import com.example.callscreenapp.process.DataManager.Companion.EffectButton
import com.example.callscreenapp.process.appendObjectToJson
import com.example.callscreenapp.process.clearJsonFile
import com.example.callscreenapp.process.downloadImageAndSave
import com.example.callscreenapp.process.jsonToList
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store
import com.example.callscreenapp.ui.activity.ContactActivity
import com.example.callscreenapp.ui.activity.ShowImageActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SelectContactFragment(private val itemClickListener: ShowAdsClickListener) : Fragment() {

    private var contactList = mutableListOf<Contact>()
    private lateinit var adLoader: AdLoader

    companion object {
        fun newInstance(itemClickListener: ShowAdsClickListener) =
            SelectContactFragment(itemClickListener)
    }

    private val viewModel: SelectContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_select_contact, container, false)
        val overlayView: View = rootView.findViewById(R.id.fragment_select_contact_overlay)
        val btnAllContact: CardView =
            rootView.findViewById(R.id.fragment_select_contact_all_contact_card)
        val btnSpecificContact: CardView =
            rootView.findViewById(R.id.fragment_select_contact_specific_contact_card_view)
        val iconAllContact: ImageView =
            rootView.findViewById(R.id.fragment_select_contact_all_contact_icon)
        val iconSpecificContact: ImageView =
            rootView.findViewById(R.id.fragment_select_contact_specific_contact_icon)
        val btnSubmit: ImageView = rootView.findViewById(R.id.fragment_select_contact_btn_submit)

        val sharedPref = requireContext().getSharedPreferences("save_animation", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        fetchContacts(false)

        val adContainer: FrameLayout = rootView.findViewById(R.id.select_ads)
        adLoader = AdLoader.Builder(requireActivity(), AppOwner.getContext().getString(R.string.native_ad_unit_id))
            .forNativeAd { nativeAd ->
                val adView =
                    layoutInflater.inflate(R.layout.layout_native_ads, null) as NativeAdView
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

        overlayView.setOnClickListener {
            // Ẩn fragment khi click vào overlay
            requireActivity().supportFragmentManager.beginTransaction().hide(this).commit()
        }
        iconAllContact.isSelected = true
        btnAllContact.setOnClickListener() {
            iconAllContact.isSelected = true
            iconSpecificContact.isSelected = false
        }
        btnSpecificContact.setOnClickListener() {
            iconSpecificContact.isSelected = true
            iconAllContact.isSelected = false
        }
        btnSubmit.setOnClickListener() {
            if (iconAllContact.isSelected) {

                val urlAvatar = store.state.avatarUrl
                val urlItem = store.state.backgroundUrl
                val urlBtnCallGreen = store.state.iconCallShowGreen
                val urlBtnCallRed = store.state.iconCallShowRed



                downloadImageAndSave(requireContext(), urlItem, "background_theme")
                downloadImageAndSave(requireContext(), urlAvatar, "avatar_theme")
                downloadImageAndSave(requireContext(), urlBtnCallGreen, "icon_answer")
                downloadImageAndSave(requireContext(), urlBtnCallRed, "icon_reject")

                clearJsonFile(requireContext(), "contact.json")

                contactList.forEach {
                    val contactData = ContactData(
                        "background_theme",
                        "avatar_theme",
                        "icon_answer",
                        "icon_reject",
                        it.phoneNumber,
                        it.name,
                        EffectButton.value.toString()
                    )
                    Log.d("save_animation", EffectButton.value.toString())
                    editor.putString("save_animation", EffectButton.value.toString())
                    editor.apply()
                    Log.d("zzzxxx", "contact: $contactData")
                    appendObjectToJson(requireContext(), contactData, "contact.json")
                }

                val contactJson: List<ContactData> = jsonToList(requireContext(), "contact.json") ?: emptyList()
                contactJson.forEach {
                    Log.d("ContactActivityJson", "Contact: ${it.phoneNumber}, ${it.animation}")
                }

                val myTheme = MyTheme(urlItem, urlAvatar, urlBtnCallGreen, urlBtnCallRed)
                appendObjectToJson(requireContext(), myTheme, "mytheme.json")
                store.dispatch(AppAction.Refresh(true))

                MainScope().launch {
                    delay(500)
                    Log.d("zzzxxx", "delay")
                    itemClickListener.checkShowAds("Notification")
                }


            } else {
                val intent = Intent(requireContext(), ContactActivity::class.java)
                startActivity(intent)
                (activity as ShowImageActivity).finish()
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    @SuppressLint("Range")
    private fun fetchContacts(isSelected: Boolean) {
        contactList.clear()
        val cursor: Cursor? = requireActivity().contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name: String =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val id: String = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val phoneCursor: Cursor? = requireActivity().contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null
                )

                phoneCursor?.use { phoneCursorInner ->
                    if (phoneCursorInner.moveToNext()) {
                        val phoneNumber: String = phoneCursorInner.getString(
                            phoneCursorInner.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        )
                        val contact = Contact(name, phoneNumber, isSelected)
                        contactList.add(contact)
                    }
                }
                phoneCursor?.close()
            }
        }
        cursor?.close()
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

}