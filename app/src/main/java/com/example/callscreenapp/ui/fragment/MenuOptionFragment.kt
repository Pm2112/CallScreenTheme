package com.example.callscreenapp.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import com.example.callscreenapp.R
import com.example.callscreenapp.process.DataManager.Companion.EffectButton
import com.example.callscreenapp.process.DataManager.Companion.isAvatar
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.process.getFlashSetting
import com.example.callscreenapp.process.getVibrateSetting
import com.example.callscreenapp.process.saveFlashSetting
import com.example.callscreenapp.process.saveVibrateSetting
import com.example.callscreenapp.ui.activity.RingtoneActivity
import com.example.callscreenapp.ui.fragment.select_effect.SelectEffectFragment
import com.google.android.material.switchmaterial.SwitchMaterial

class MenuOptionFragment : Fragment() {

    companion object {
        fun newInstance() = MenuOptionFragment()
    }

    private lateinit var firebase: EvenFirebase

    private val viewModel: MenuOptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menu_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebase = EvenFirebase(requireActivity())

        val btnRingtone: CardView = view.findViewById(R.id.menu_option_ringtone)
        btnRingtone.setOnClickListener(){
            val intent = Intent(context, RingtoneActivity::class.java)
            startActivity(intent)
        }

        val btnVibrate: SwitchMaterial = view.findViewById(R.id.fragment_menu_option_vibrate)
        val (isVibrate, isVibrate1) = getVibrateSetting(requireContext())
        btnVibrate.isChecked = isVibrate == true
        btnVibrate.setOnClickListener {
            if (btnVibrate.isChecked) {
                saveVibrateSetting(requireContext(), true)
//                Log.d("btnVibrate", "btnVibrate true")
                firebase.logFirebaseEvent("vibrate")
            } else {
                saveVibrateSetting(requireContext(), false)
//                Log.d("btnVibrate", "btnVibrate false")
            }
        }

        val btnFlash: SwitchMaterial = view.findViewById(R.id.fragment_menu_option_flash)
        val (isFlash, isFlash1) = getFlashSetting(requireContext())
        btnFlash.isChecked = isFlash == true
        btnFlash.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                saveFlashSetting(requireContext(), true)
                firebase.logFirebaseEvent("flash")
            } else {
                saveFlashSetting(requireContext(), false)
            }
        }

        val btnEffect: CardView = view.findViewById(R.id.menu_option_effect)
        btnEffect.setOnClickListener {
            // Gọi SelectEffectFragment khi nhấn vào nút
            val selectEffectFragment = SelectEffectFragment.newInstance()
            selectEffectFragment.show(parentFragmentManager, "SelectEffectFragment")
        }

        val btnAvatar: SwitchMaterial = view.findViewById(R.id.fragment_menu_option_avatar)

        val sharedPref = requireContext().getSharedPreferences("save_show_avatar", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val iAvatar = sharedPref.getBoolean("save_show_avatar", true)
        btnAvatar.isChecked = iAvatar

        btnAvatar.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isAvatar.value = true
                editor.putBoolean("save_show_avatar", true)
                editor.apply()
            } else {
                isAvatar.value = false
                editor.putBoolean("save_show_avatar", false)
                editor.apply()
            }
        }
    }
}