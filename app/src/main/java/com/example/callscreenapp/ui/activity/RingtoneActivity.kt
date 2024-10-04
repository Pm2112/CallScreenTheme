package com.example.callscreenapp.ui.activity

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.callscreenapp.BaseActivity
import com.example.callscreenapp.Impl.ClickRateAppListener
import com.example.callscreenapp.Impl.DataFetchedListener
import com.example.callscreenapp.Impl.NetworkChangeListener
import com.example.callscreenapp.Impl.OnItemClickListener
import com.example.callscreenapp.R
import com.example.callscreenapp.adapter.ListRingtoneAdapter
import com.example.callscreenapp.ads.AdmobInterstitialAd
import com.example.callscreenapp.ads.AdmobOpenAd
import com.example.callscreenapp.ads.TYPE_ADS
import com.example.callscreenapp.model.ListRingtone
import com.example.callscreenapp.native_ads.NativeAdsLoader
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.process.getRateAppMainCreate
import com.example.callscreenapp.process.saveRingtone
import com.example.callscreenapp.ui.dialog.LoadingDialog
import com.example.callscreenapp.ui.dialog.RateAppDialog
import com.example.callscreenapp.ui.dialog.showNetworkCustomDialog
import com.example.callscreenapp.ui.fragment.play_ringtone.PlayRingtoneFragment
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class RingtoneActivity : BaseActivity(), OnItemClickListener, NetworkChangeListener, ClickRateAppListener {

    private lateinit var btnBack: ImageView
    private lateinit var btnSubmit: ImageView
    private lateinit var listRingtone: RecyclerView
    private var selectedRingtoneUrl: String? = null
    private var selectedRingtoneName: String? = null
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var rateAppDialog: RateAppDialog
    private lateinit var nativeAdsLoader: NativeAdsLoader
    private var listDataRingtone = mutableListOf<ListRingtone>()
    private lateinit var firebase: EvenFirebase

    override fun onNetworkUnavailable() {
        runOnUiThread {
            showNetworkCustomDialog(this)
        }
    }

    override fun closeAds(type_ads: TYPE_ADS?) {
        super.closeAds(type_ads)
        if (type_ads == TYPE_ADS.InterstitialAd) {
            finish()
        }
    }

    override fun onClickLater() {
        finish()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_ringtone)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ringtone_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadingDialog = LoadingDialog(this) // Khởi tạo LoadingDialog
        rateAppDialog = RateAppDialog(this, this@RingtoneActivity, this)

        nativeAdsLoader = NativeAdsLoader(this)
        nativeAdsLoader.loadNativeAd(R.id.ringtone_ads, R.layout.layout_native_ads, "ringtone activity")

        firebase = EvenFirebase(this)

        listRingtone = findViewById(R.id.ringtone_activity_list_ringtone)
        listRingtone.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Tạo danh sách mẫu
        val nameRingtone = listOf(
            ListRingtone("Visualisation", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/visualisation.m4r"),
            ListRingtone("High Tension Buildup", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/hightensionbuildup.m4r"),
            ListRingtone("Incoming Call", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/incomingcall.m4r"),
            ListRingtone("Mobile Ringtone", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/mobileringtone.m4r"),
            ListRingtone("klingelton", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/klingelton.m4r"),
            ListRingtone("Telefono Ringtone", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/telefonoringtone.m4r"),
            ListRingtone("Bell Phone 4", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/bellphone4.m4r"),
            ListRingtone("Bell Ping 2", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/bellping2.m4r"),
            ListRingtone("Phone Ringing 1", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/phoneringing1.m4r"),
            ListRingtone("Phone Ringing 2", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Most+Popular/phoneringing2.m4r"),

            // 11 - 20
            ListRingtone("Rush", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Out Of Space Dog", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/outofspacedog.m4r"),
            ListRingtone("Good Morning", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/goodmorning.m4r"),
            ListRingtone("The Little Dwarf", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/thelittledwarf.m4r"),
            ListRingtone("Who Are You", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/whoareyou.m4r"),
            ListRingtone("Wake Up, Will You?", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/wakeupwillyou.m4r"),
            ListRingtone("A Merry Little Christmas", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/amerrylittlechristmas.m4r"),
            ListRingtone("Vuvuzela Techno", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/vuvuzelatechno.m4r"),
            ListRingtone("Remembers Me Of Asia", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/remembersmeofasia.m4r"),
            ListRingtone("Beautiful Christmas Tune", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/beautifulchristmastune.m4r"),

            // 21 - 30
            ListRingtone("Rose Baba", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/rosebaba.m4r"),
            ListRingtone("La La La song", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/lalalasong.m4r"),
            ListRingtone("Marimba Roll", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/marimbaroll.m4r"),
            ListRingtone("Marimba Loop", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/marimbaloop.m4r"),
            ListRingtone("Marimba Loop 2", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/marimbaloop2.m4r"),
            ListRingtone("Marimba Descending", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/marimbadescending.m4r"),
            ListRingtone("Creepy Marimba", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/creepymarimba.m4r"),
            ListRingtone("Thinking Marimba", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/thinkingmarimba.m4r"),
            ListRingtone("Switzedland Station Chime", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/switzedlandstationchime.m4r"),
            ListRingtone("Bach Marimba", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Marimba+Remix/bachmarimba.m4r"),

            // 31 - 40
            ListRingtone("Frog", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/frog.m4r"),
            ListRingtone("Dolphin", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/dolphin.m4r"),
            ListRingtone("Lion", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/lion.m4r"),
            ListRingtone("Monster Scratch", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/monsterscratch.m4r"),
            ListRingtone("Cyber Duck", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/cyberduck.m4r"),
            ListRingtone("Crickets", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/crickets.m4r"),
            ListRingtone("Birds", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/birds.m4r"),
            ListRingtone("Kitty", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/kitty.m4r"),
            ListRingtone("Eagle", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/eagle.m4r"),
            ListRingtone("Bear Hit", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Animal+/bearhit.m4r"),

            // 41 - 50
            ListRingtone("Loco Contigo", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/LocoContigoInstrumental.m4r"),
            ListRingtone("Soch Na Sake Flute", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/Sochnasakeflute.m4r"),
            ListRingtone("Khaab Instrumental", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/KhaabInstrumental.m4r"),
            ListRingtone("Romantic Travel ", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/romantictravelringtone.m4r"),
            ListRingtone("Guitar For Love", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/GuitarForLoveInstrumental.m4r"),
            ListRingtone("Mohabbatein", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/mohabbatein.m4r"),
            ListRingtone("Duniya Instrumental", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/DuniyaInstrumental.m4r"),
            ListRingtone("Nice Ringtone ", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/Niceringtone2019.m4r"),
            ListRingtone("Kal Ho Na Hoo", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/kalhonahoo.m4r"),
            ListRingtone("Best Tamil Ringtone", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Instrumental/besttamilringtone.m4r"),

            // 51 - 60
            ListRingtone("Alarm Clock Ringing", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/alarmclockringing.m4r"),
            ListRingtone("Analog Alarm Clock", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/analogalarmclock.m4r"),
            ListRingtone("Alarm Clock 1", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/alarmclock1.m4r"),
            ListRingtone("Referee Whistle", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/refereewhistle.m4r"),
            ListRingtone("Car Horn", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/carhorn.m4r"),
            ListRingtone("Backup Truck", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/backuptruck.m4r"),
            ListRingtone("Bell School Ringing", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/bellschoolringing.m4r"),
            ListRingtone("Bell", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/bell.m4r"),
            ListRingtone("Notification Chime", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/notificationchime.m4r"),
            ListRingtone("Red Alert", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Alarm/redalert.m4r"),

            // 61 - 70
            ListRingtone("DoorBell", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Cell Phone Vibration", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Bell Alarm Remix", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Alien Alarm Broadcast", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Alarm ", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Beautiful Arabic Tone", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Jack Sparrow", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Funny Laugh", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Vanilla", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),
            ListRingtone("Crackle", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Best+Ringtones/rush.m4r"),

            // 71 - 80
            ListRingtone("Romantic Intro", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Sound+Effects/romanticintro.m4r"),
            ListRingtone("Sentimental", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Sound+Effects/sentimental.m4r"),
            ListRingtone("Magical Morning", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Sound+Effects/magicalmorning.m4r"),
            ListRingtone("Lifetime", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Sound+Effects/lifetime.m4r"),
            ListRingtone("Sexiest Romantic", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Sound+Effects/sexiestromantic.m4r"),
            ListRingtone("Happy Ringtone", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/happyringtone.m4r"),
            ListRingtone("Surpriser", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/surpriser.m4r"),
            ListRingtone("Happy Bot", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/happybot.m4r"),
            ListRingtone("Dubbio", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/dubbio.m4r"),
            ListRingtone("Revert", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/revert.m4r"),

            // 81 - 90
            ListRingtone("Cyber Bubbles", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/cyberbubbles.m4r"),
            ListRingtone("Comedic Boing", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/comedicboing.m4r"),
            ListRingtone("Ba Da Dum", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/badadum.m4r"),
            ListRingtone("Cartoon Siren Whistle", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/cartoonsirenwhistle.m4r"),
            ListRingtone("Dundundunnn", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Funny/dundundunnn.m4r"),
            ListRingtone("Message 1", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/message1.m4r"),
            ListRingtone("Notification 2", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/notification2.m4r"),
            ListRingtone("Dingaling", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/dingaling.m4r"),
            ListRingtone("Notification 1", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/notification1.m4r"),
            ListRingtone("Notification 3", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/notification3.m4r"),

            // 91 - 95
            ListRingtone("Max Bells", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/vmaxbells.m4r"),
            ListRingtone("Chord Alert ", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/chordalert.m4r"),
            ListRingtone("Messenger Notification", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/messengernotification.m4r"),
            ListRingtone("Alert Notification", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/alertnotification.m4r"),
            ListRingtone("Filling Your Inbox", "https://s3-us-west-1.amazonaws.com/com.ringtonemaker.app/Message/fillingyourinbox.m4r")
        )
        listRingtone.adapter = ListRingtoneAdapter(nameRingtone, this)

        btnBack = findViewById(R.id.ringtone_activity_icon_back)
        btnBack.setOnClickListener {
            showPopupLoadAds(TYPE_ADS.InterstitialAd)
        }

        btnSubmit = findViewById(R.id.ringtone_activity_icon_submit)
        btnSubmit.setOnClickListener {
            selectedRingtoneUrl?.let { url ->
                selectedRingtoneName?.let { name ->
                    DownloadAndSaveRingtoneTask(this, url, name).execute()
                }
            }
            firebase.logFirebaseEvent("ringtone")

            val (main, create) = getRateAppMainCreate(this)
            if (main == true) {
                rateAppDialog.show()
            }
        }
    }

    private fun fetchJsonData(url: String, dataFetchedListener: DataFetchedListener) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    body?.let {
                        val gson = Gson()
                        val itemType = object : TypeToken<List<ListRingtone>>() {}.type
                        val itemList: List<ListRingtone> = gson.fromJson(it, itemType)

                        itemList.forEach { item ->
                            listDataRingtone.add(item)
                        }

                        dataFetchedListener.onDataFetched()
                    }

                } else {
                    Log.e("xxxzzz", "fetchJsonData: Request failed")
                }
            }
        }
    }

    override fun onItemClick(position: Int, ringtoneUrl: String, nameRingtone: String) {
        setControlsEnabled(false)
        selectedRingtoneUrl = ringtoneUrl
        selectedRingtoneName = nameRingtone
        DownloadRingtoneTask(this, ringtoneUrl, nameRingtone).execute()
    }

    private fun setControlsEnabled(enabled: Boolean) {
        btnSubmit.isEnabled = enabled
        listRingtone.forEach { it.isEnabled = enabled }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        showPopupLoadAds(TYPE_ADS.InterstitialAd)
    }



    @Suppress("DEPRECATION")
    @SuppressLint("StaticFieldLeak")
    private inner class DownloadRingtoneTask(
        private val context: Context,
        private val ringtoneUrl: String,
        private val ringtoneName: String
    ) : AsyncTask<Void, Void, String>() {
        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            loadingDialog.show() // Hiển thị loading dialog
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): String? {
            return try {
                val url = URL(ringtoneUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return null
                }

                val file = File(context.cacheDir, "$ringtoneName.m4r")
                if (file.exists()) {
                    file.delete()
                }
                val outputStream = FileOutputStream(file)
                val inputStream: InputStream = connection.inputStream
                val buffer = ByteArray(4096)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()

                file.absolutePath
            } catch (e: Exception) {
                Log.e("DownloadRingtoneTask", "Error downloading ringtone", e)
                null
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            loadingDialog.dismiss() // Ẩn loading dialog
            if (result != null) {
                val playRingtone = PlayRingtoneFragment.newInstance(result, ringtoneName)
                playRingtone.show(supportFragmentManager, playRingtone.tag)
            }
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000L) // Delay 1 giây
                setControlsEnabled(true)
            }
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("StaticFieldLeak")
    private inner class DownloadAndSaveRingtoneTask(
        private val context: Context,
        private val ringtoneUrl: String,
        private val ringtoneName: String
    ) : AsyncTask<Void, Void, Boolean>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): Boolean {
            return try {
                val url = URL(ringtoneUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return false
                }
                Log.d("DownloadAndSaveRingtoneTask", "$ringtoneName.m4r")
                val file = File(context.filesDir, "$ringtoneName.m4r")
                if (file.exists()) {
                    file.delete()
                }
                val outputStream = FileOutputStream(file)
                val inputStream: InputStream = connection.inputStream
                val buffer = ByteArray(4096)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                val ringtoneUri = Uri.fromFile(file)
                saveRingtone(context, ringtoneUri.toString())
                outputStream.close()
                inputStream.close()

                true
            } catch (e: Exception) {
                Log.e("DownloadAndSaveRingtoneTask", "Error saving ringtone", e)
                false
            }
        }
    }
}
