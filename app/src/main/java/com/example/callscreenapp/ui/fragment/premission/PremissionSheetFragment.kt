package com.example.callscreenapp.ui.fragment.premission

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.R
import com.example.callscreenapp.permission.hasWriteSettingsPermission
import com.example.callscreenapp.permission.isDefaultDialer
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.ui.activity.ShowImageActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.switchmaterial.SwitchMaterial

class PremissionSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = PremissionSheetFragment()

        const val WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 1002
    }
    private val viewModel: PremissionSheetViewModel by viewModels()

    private lateinit var adLoader: AdLoader
    private lateinit var firebase: EvenFirebase

    @RequiresApi(Build.VERSION_CODES.Q)
    private val dialerRequestLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                Log.d("requestDefaultDialerCheck", "1")
                setDialerPermission(requireView())
                checkAllPermission(requireView())
            } else {
                Log.d("requestDefaultDialerCheck", "2")
                setDialerPermission(requireView())
                checkAllPermission(requireView())
            }
        }

    private val writeSettingsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Settings.System.canWrite(requireContext())) {
                setWriteSettingsPermission(requireView())
                checkAllPermission(requireView())
            } else {
                setWriteSettingsPermission(requireView())
                checkAllPermission(requireView())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_premission_sheet, container, false)
        firebase = EvenFirebase(requireActivity())
        // Initialize UI components and AdLoader here
        val adContainer: FrameLayout = view.findViewById(R.id.permission_ads)
        adLoader = AdLoader.Builder(requireContext(), getString(R.string.native_ad_unit_id))
            .forNativeAd { nativeAd ->
                if (isAdded) {
                    val adView =
                        layoutInflater.inflate(R.layout.layout_native_ads, null) as NativeAdView
                    populateNativeAdView(nativeAd, adView)
                    adContainer.removeAllViews()
                    adContainer.addView(adView)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdLoader", "Failed to load ad: ${adError.message}")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    firebase.logFirebaseParamEvent("ad_native_impress", "location", "Permission Sheet")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    firebase.logFirebaseParamEvent("ad_native_click", "location", "Permission Sheet")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        return view
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isDefaultDialer = isDefaultDialer(requireContext())
        var isWriteSettingsPermission = hasWriteSettingsPermission(requireContext())

        val permissionDefaultDialer: SwitchMaterial =
            view.findViewById(R.id.permission_phone_default)
        val permissionWriteSettingsPermission: SwitchMaterial =
            view.findViewById(R.id.permission_system_setting)
        val permissionAll: SwitchMaterial = view.findViewById(R.id.permission_all)

        permissionAll.isChecked = isDefaultDialer && isWriteSettingsPermission

        permissionDefaultDialer.isChecked = isDefaultDialer
        permissionWriteSettingsPermission.isChecked = isWriteSettingsPermission

        permissionDefaultDialer.setOnClickListener {
            if (!isDefaultDialer) {
                requestDefaultDialer(requireContext())
            }
        }

        permissionWriteSettingsPermission.setOnClickListener {
            if (!isWriteSettingsPermission) {
                requestWriteSettingsPermission(requireActivity())
                isWriteSettingsPermission = hasWriteSettingsPermission(requireContext())
                permissionWriteSettingsPermission.isChecked = isWriteSettingsPermission
            }
        }

        permissionAll.setOnClickListener {
            setAllPermission(view)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setDialerPermission(view: View) {
        val permissionDefaultDialer: SwitchMaterial = view.findViewById(R.id.permission_phone_default)
        val isDefaultDialer = isDefaultDialer(requireContext())
        if (isDefaultDialer) {
            firebase.logFirebaseEvent("set_as_default")
        }
        permissionDefaultDialer.isChecked = isDefaultDialer
    }

    private fun setWriteSettingsPermission(view: View) {
        val permissionWriteSettingsPermission: SwitchMaterial =
            view.findViewById(R.id.permission_system_setting)
        val isWriteSettingsPermission = hasWriteSettingsPermission(requireContext())
        permissionWriteSettingsPermission.isChecked = isWriteSettingsPermission
        if (isWriteSettingsPermission) {
            firebase.logFirebaseEvent("grant_permission")
        }
    }

    private fun checkAllPermission(view: View) {
        val permissionDefaultDialer: SwitchMaterial = view.findViewById(R.id.permission_phone_default)
        val permissionWriteSettingsPermission: SwitchMaterial =
            view.findViewById(R.id.permission_system_setting)
        val permissionAll: SwitchMaterial = view.findViewById(R.id.permission_all)
        permissionAll.isChecked = permissionDefaultDialer.isChecked && permissionWriteSettingsPermission.isChecked

        if (permissionAll.isChecked) {
            val intent = Intent(context, ShowImageActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setAllPermission(view: View) {
        val permissionAll: SwitchMaterial = view.findViewById(R.id.permission_all)
        requestDefaultDialer(requireContext())
        requestWriteSettingsPermission(requireActivity())
        val permissionDefaultDialer: SwitchMaterial =
            view.findViewById(R.id.permission_phone_default)
        val permissionWriteSettingsPermission: SwitchMaterial =
            view.findViewById(R.id.permission_system_setting)
        permissionAll.isChecked = permissionDefaultDialer.isChecked && permissionWriteSettingsPermission.isChecked
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestDefaultDialer(context: Context) {
        context.let { ctx ->
            val telecomManager =
                ctx.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = ctx.getSystemService(Context.ROLE_SERVICE) as? RoleManager
                roleManager?.let {
                    val intent = it.createRequestRoleIntent(RoleManager.ROLE_DIALER)
                    dialerRequestLauncher.launch(intent)
                }
            } else {
                Log.d("requestDefaultDialerCheck", "2")
                val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).apply {
                    putExtra(
                        TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                        ctx.packageName
                    )
                }
                ctx.startActivity(intent)
            }
        }
    }


    private fun requestWriteSettingsPermission(activity: Activity) {
        if (!Settings.System.canWrite(activity.applicationContext)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + activity.packageName)
            writeSettingsPermissionLauncher.launch(intent)
        }
    }

    fun FragmentActivity.showCustomBottomSheet() {
        PremissionSheetFragment().show(this.supportFragmentManager, PremissionSheetFragment::class.java.simpleName)
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
