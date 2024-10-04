package com.example.callscreenapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.view.WindowCompat
import com.bluewhaleyt.network.NetworkUtil
import com.bumptech.glide.Glide
import com.example.callscreenapp.Impl.DataFetchedListener
import com.example.callscreenapp.ads.AdmobOpenAd
import com.example.callscreenapp.data.AvatarSize
import com.example.callscreenapp.data.ButtonAllSize
import com.example.callscreenapp.data.ButtonCallSize
import com.example.callscreenapp.data.CategoryAllSize
import com.example.callscreenapp.data.CategoryAnimalSize
import com.example.callscreenapp.data.CategoryAnimeSize
import com.example.callscreenapp.data.CategoryCastleSize
import com.example.callscreenapp.data.CategoryFantasySize
import com.example.callscreenapp.data.CategoryGameSize
import com.example.callscreenapp.data.CategoryGifSize
import com.example.callscreenapp.data.CategoryLoveSize
import com.example.callscreenapp.data.CategoryNatureSize
import com.example.callscreenapp.data.CategorySeaSize
import com.example.callscreenapp.data.CategoryTechSize
import com.example.callscreenapp.data.CreateListAvatar
import com.example.callscreenapp.data.CreateListButtonAll
import com.example.callscreenapp.data.CreateListButtonCall
import com.example.callscreenapp.data.CreateListCategoryAll
import com.example.callscreenapp.data.CreateListCategoryAnimal
import com.example.callscreenapp.data.CreateListCategoryAnime
import com.example.callscreenapp.data.CreateListCategoryCastle
import com.example.callscreenapp.data.CreateListCategoryFantasy
import com.example.callscreenapp.data.CreateListCategoryGame
import com.example.callscreenapp.data.CreateListCategoryGif
import com.example.callscreenapp.data.CreateListCategoryLove
import com.example.callscreenapp.data.CreateListCategoryNature
import com.example.callscreenapp.data.CreateListCategorySea
import com.example.callscreenapp.data.CreateListCategoryTech
import com.example.callscreenapp.data.ListCategoryAll
import com.example.callscreenapp.databinding.ActivitySplashCustomBinding
import com.example.callscreenapp.firebase.FbFirestore
import com.example.callscreenapp.model.DataTheme
import com.example.callscreenapp.process.getOnboard
import com.example.callscreenapp.process.saveOnboard
import com.example.callscreenapp.ui.activity.MainActivity
import com.example.callscreenapp.ui.activity.OnboardActivity
import com.example.callscreenapp.utils.PreferencesManager
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Timer
import java.util.TimerTask
import kotlin.system.exitProcess

class SplashActivity : BaseActivity(), DataFetchedListener {
    private var binding: ActivitySplashCustomBinding? = null

    override fun onDataFetched() {
        Log.d("zzzxxx", "Create data")

        CreateListCategoryAll()
        CreateListCategoryAnime()
        CreateListCategoryLove()
        CreateListCategoryAnimal()
        CreateListCategoryNature()
        CreateListCategoryGame()
        CreateListCategoryCastle()
        CreateListCategoryFantasy()
        CreateListCategoryTech()
        CreateListCategorySea()
        CreateListCategoryGif()
        CreateListButtonCall()
        CreateListButtonAll()
        CreateListAvatar()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashCustomBinding.inflate(layoutInflater)
        setContentView(binding?.getRoot())
//        setStatusBarAndNavigationBar(R.color.background, R.color.background)
        /*Simple hold animation to hold ImageView in the centre of the screen at a slightly larger
        scale than the ImageView's original one.*/
        isSplash = true

        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        FbFirestore.Instance.getConfig {
            checkNetworkToLoadData()
        }

        Log.d("zzzxxx", "onCreate")
        val url = "https://callthemetest.s3.amazonaws.com/data.json"
        fetchJsonData(url, this@SplashActivity)
    }

    private fun fetchJsonData(url: String, dataFetchedListener: DataFetchedListener) {
        Log.d("zzzxxx", "fetchJsonData: fetchJsonData run")
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("zzzxxx", "fetchJsonData: CoroutineScope run")
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            Log.d("zzzxxx", "fetchJsonData: client $client")
            Log.d("zzzxxx", "fetchJsonData: request body ${request.body}, request url ${request.url}, request header ${request.headers}")

            try {
                client.newCall(request).execute().use { response ->
                    Log.d("zzzxxx", "fetchJsonData: response $response")
                    if (response.isSuccessful) {
                        Log.d("zzzxxx", "fetchJsonData: Request success")
                        val body = response.body?.string()
                        body?.let {
                            Log.d("xxxzzz", "fetchJsonData: ${it[0]}")
                            val gson = Gson()
                            val itemType = object : TypeToken<List<DataTheme>>() {}.type
                            val itemList: List<DataTheme> = gson.fromJson(it, itemType)

                            CategoryAnimeSize = itemList[0].number
                            CategoryLoveSize = itemList[1].number
                            CategoryAnimalSize = itemList[2].number
                            CategoryNatureSize = itemList[3].number
                            CategoryGameSize = itemList[4].number
                            CategoryCastleSize = itemList[5].number
                            CategoryFantasySize = itemList[6].number
                            CategoryTechSize = itemList[7].number
                            CategorySeaSize = itemList[8].number
                            ButtonCallSize = itemList[9].number
                            AvatarSize = itemList[10].number
                            CategoryAllSize = itemList[11].number
                            CategoryGifSize = itemList[12].number
                            ButtonAllSize = itemList[13].number

                            withContext(Dispatchers.Main) {
                                dataFetchedListener.onDataFetched()
                            }
                        }
                    } else {
                        Log.e("zzzxxx", "fetchJsonData: Request failed with code ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.e("zzzxxx", "fetchJsonData: Exception occurred", e)
            }
        }
    }

    private fun checkNetworkToLoadData() {
        if (NetworkUtil.isNetworkAvailable(this)) {
            loadSub(true)
        } else {
            showPopupNetworkError()
        }
    }

    override fun clickPopupNetworkErrorButtonOK() {
        super.clickPopupNetworkErrorButtonOK()
        checkNetworkToLoadData()
    }

    override fun nextScreen() {
        super.nextScreen()
        MainScope().launch {
            delay(2000)
            Log.d("nextScreen", "nextScreen")
            showActivity()
        }
    }

    private fun showActivity() {
        // broiad
        if (NetworkUtil.isNetworkAvailable(this)) {
            val (onboard, onboard1) = getOnboard(this@SplashActivity)
            val destinationActivity = if(onboard == true) (OnboardActivity::class.java) else (MainActivity::class.java)
            val intent = Intent(
                this@SplashActivity,
                destinationActivity
            )
            startActivity(intent)
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        } else {
            showPopupNetworkError()
        }
    }
}

