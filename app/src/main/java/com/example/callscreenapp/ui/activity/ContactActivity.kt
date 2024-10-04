package com.example.callscreenapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.callscreenapp.BaseActivity
import com.example.callscreenapp.Impl.ContactItemClickListener
import com.example.callscreenapp.Impl.NetworkChangeListener
import com.example.callscreenapp.R
import com.example.callscreenapp.adapter.ContactAdapter
import com.example.callscreenapp.ads.AdmobInterstitialAd
import com.example.callscreenapp.ads.AdmobOpenAd
import com.example.callscreenapp.ads.TYPE_ADS
import com.example.callscreenapp.model.Contact
import com.example.callscreenapp.model.ContactData
import com.example.callscreenapp.model.MyTheme
import com.example.callscreenapp.native_ads.NativeAdsLoader
import com.example.callscreenapp.process.DataManager.Companion.EffectButton
import com.example.callscreenapp.process.appendObjectToJson
import com.example.callscreenapp.process.downloadImageAndSave
import com.example.callscreenapp.process.jsonToList
import com.example.callscreenapp.process.saveDialerData
import com.example.callscreenapp.process.updateObjectIfExistsInJson
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store
import com.example.callscreenapp.ui.dialog.showNetworkCustomDialog
import com.google.gson.Gson
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ContactActivity : BaseActivity(), ContactItemClickListener, NetworkChangeListener {
    private var contactList = mutableListOf<Contact>()
    private val saveContact = mutableListOf<Contact>()
    private lateinit var listContact: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var nativeAdsLoader: NativeAdsLoader

    override fun onNetworkUnavailable() {
        runOnUiThread {
            showNetworkCustomDialog(this)
        }
    }

    enum class EVENT_CLICK() {
        NONE,
        BACK,
        SUBMIT
    }

    var event_click: EVENT_CLICK = EVENT_CLICK.NONE

    override fun closeAds(type_ads: TYPE_ADS?) {
        super.closeAds(type_ads)
        if (type_ads == TYPE_ADS.InterstitialAd) {
            when (event_click) {
                EVENT_CLICK.BACK -> {
                    val intent = Intent(this@ContactActivity, ShowImageActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                EVENT_CLICK.SUBMIT -> {
                    val intent = Intent(this, NotificationActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                EVENT_CLICK.NONE -> {}
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gson = Gson()

        listContact = findViewById(R.id.activity_contact_list_phone_number)
        searchInput = findViewById(R.id.edit_text_input)

        listContact.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        fetchContacts(false)

        val btnSelect: ImageView = findViewById(R.id.check_box_all)
        btnSelect.setOnClickListener() {
            val isSelected = !it.isSelected
            it.isSelected = isSelected
            fetchContacts(isSelected)
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Không cần xử lý trước sự thay đổi văn bản
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Không cần xử lý trong quá trình thay đổi văn bản
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                filterContacts(searchText)
            }
        })

        nativeAdsLoader = NativeAdsLoader(this)
        nativeAdsLoader.loadNativeAd(
            R.id.contact_ads,
            R.layout.layout_native_ads,
            "contact activity"
        )

        val btnBack: ImageView = findViewById(R.id.activity_contact_btn_back)
        btnBack.setOnClickListener() {
            showPopupLoadAds(TYPE_ADS.InterstitialAd)
            event_click = EVENT_CLICK.BACK
        }

        val btnSubmit: ImageView = findViewById(R.id.activity_contact_btn_submit)
        btnSubmit.setOnClickListener() {
            Log.d("zzzxxx", "btnsubmit")
            val urlAvatar = store.state.avatarUrl
            val urlItem = store.state.backgroundUrl
            val urlBtnCallGreen = store.state.iconCallShowGreen
            val urlBtnCallRed = store.state.iconCallShowRed
            val fileName = "${System.currentTimeMillis()}"
            Log.d(
                "zzzxxx",
                "urlAvatar: $urlAvatar, urlItem: $urlItem, urlBtnCallGreen: $urlBtnCallGreen, urlBtnCallRed: $urlBtnCallRed"
            )
            downloadImageAndSave(this, urlItem, "contact_background_theme_${fileName}")
            downloadImageAndSave(this, urlAvatar, "contact_avatar_theme_${fileName}")
            downloadImageAndSave(this, urlBtnCallGreen, "contact_icon_answer_${fileName}")
            downloadImageAndSave(this, urlBtnCallRed, "contact_icon_reject_${fileName}")

            saveContact.forEach {
                val contact = ContactData(
                    "contact_background_theme_${fileName}",
                    "contact_avatar_theme_${fileName}",
                    "contact_icon_answer_${fileName}",
                    "contact_icon_reject_${fileName}",
                    it.phoneNumber,
                    it.name,
                    EffectButton.value.toString()
                )
                Log.d("zzzxxx", "contact: $contact")
                updateObjectIfExistsInJson(
                    this,
                    contact,
                    "contact.json"
                ) { it1 -> it1.phoneNumber == contact.phoneNumber }
            }

            val contactJson: List<ContactData> = jsonToList(this, "contact.json") ?: emptyList()
            contactJson.forEach {
                Log.d("zzzxxx", "Contact: ${it.phoneNumber}, ${it.backgroundTheme}, ${it.animation}")
            }

            val myTheme = MyTheme(urlItem, urlAvatar, urlBtnCallGreen, urlBtnCallRed)
            appendObjectToJson(this, myTheme, "mytheme.json")

//            val intent = Intent(this, NotificationActivity::class.java)
//            startActivity(intent)
//            finish()
            MainScope().launch {
                delay(500)
                Log.d("zzzxxx", "delay")
                showPopupLoadAds(TYPE_ADS.InterstitialAd)
                event_click = EVENT_CLICK.SUBMIT
            }
        }
    }

    @SuppressLint("Range")
    private fun fetchContacts(isSelected: Boolean) {
        contactList.clear()
        val cursor: Cursor? =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val name: String =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val id: String = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val phoneCursor: Cursor? = contentResolver.query(
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

        filterContacts(searchInput.text.toString())
    }

    private fun filterContacts(searchText: String) {
        val filteredList = if (searchText.isNotEmpty()) {
            contactList.filter { contact ->
                contact.name.contains(searchText, ignoreCase = true) ||
                        contact.phoneNumber.contains(searchText)
            }
        } else {
            contactList
        }
        listContact.adapter = ContactAdapter(filteredList, this)
    }

    override fun onContactItemClicked(position: Int) {
        // Xử lý dữ liệu được gửi từ Adapter

        val contact = contactList[position]
        saveContact.add(contact)
        Log.d("ContactActivityPosition", "Clicked on contact: $saveContact")
    }

    override fun onRemoveContactItemClicked(position: Int) {
        val contact = contactList[position]
        saveContact.remove(contact)
        Log.d("ContactActivityPosition", "Clicked on contact: $saveContact")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
    }
}
