package com.example.callscreenapp.ui.fragment.customize

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.callscreenapp.Impl.ChangeButtonCallListener
import com.example.callscreenapp.R
import com.example.callscreenapp.adapter.CustomizeAdapter
import com.example.callscreenapp.adapter.ListAvatarAdapter
import com.example.callscreenapp.adapter.ListCallButtonAdapter
import com.example.callscreenapp.data.ListAvatar
import com.example.callscreenapp.data.ListButtonCall
import com.example.callscreenapp.data.ListCategoryAll
import com.example.callscreenapp.model.Avatar
import com.example.callscreenapp.model.Background
import com.example.callscreenapp.model.ListAvatar
import com.example.callscreenapp.model.PhoneCallListImage
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.process.appendObjectToJson
import com.example.callscreenapp.process.jsonToList
import com.example.callscreenapp.process.saveImageFromUri
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store
import com.example.callscreenapp.service.NetworkChangeReceiver
import com.example.callscreenapp.ui.activity.ShowImageActivity
import com.example.callscreenapp.ui.dialog.showNetworkCustomDialog
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CustomizeFragment : Fragment(), ChangeButtonCallListener {

    companion object {
        fun newInstance() = CustomizeFragment()
    }

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var pickBackgroundLauncher: ActivityResultLauncher<String>
    private lateinit var firebase: EvenFirebase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun changeButtonCall(answerUrl: String, dejectUrl: String) {
        store.dispatch(AppAction.SetIconCallShowId(answerUrl, dejectUrl))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_customize, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebase = EvenFirebase(requireActivity())
        firebase.logFirebaseEvent("customize")

        val urlAvatar = ListAvatar
        val listAvatar: RecyclerView = view.findViewById(R.id.customize_fragment_list_avatar)
        listAvatar.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        store.dispatch(AppAction.SetAvatarUrl(""))

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val imagePath = requireContext().saveImageFromUri(it)
                    val avatar = Avatar(imagePath ?: "")
                    appendObjectToJson(requireContext(), avatar, "avatar.json")

                    val items: List<Avatar> =
                        jsonToList(requireContext(), "avatar.json") ?: emptyList()
                    items.last().let { item ->
                        urlAvatar.add(1, ListAvatar(item.avatarUrl))
                    }
                    listAvatar.adapter = ListAvatarAdapter(urlAvatar, pickImageLauncher)
                }
            }


        listAvatar.adapter = ListAvatarAdapter(urlAvatar, pickImageLauncher)


        val listCallIcon: RecyclerView = view.findViewById(R.id.customize_fragment_list_icon_call)
        listCallIcon.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        val snapHelper = PagerSnapHelper()
//        snapHelper.attachToRecyclerView(listCallIcon)

        // Tạo danh sách mẫu
        val nameIconCall = ListButtonCall
        listCallIcon.adapter = ListCallButtonAdapter(nameIconCall, this)

        val listImageView: RecyclerView =
            view.findViewById(R.id.customize_fragment_list_background_image)

        val listImage = ListCategoryAll

        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.SPACE_BETWEEN
        }
        listImageView.layoutManager = layoutManager

        pickBackgroundLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val imagePath = requireContext().saveImageFromUri(it)

                    val background = Background(imagePath ?: "")
                    appendObjectToJson(requireContext(), background, "background.json")

                    val items: List<Background> =
                        jsonToList(requireContext(), "background.json") ?: emptyList()
                    items.last().let { item ->
                        listImage.add(1, PhoneCallListImage(item.backgroundUrl, "", "", ""))
                    }

                    listImageView.adapter =
                        CustomizeAdapter(listImage, childFragmentManager, pickBackgroundLauncher)
                }
            }

        listImageView.adapter =
            CustomizeAdapter(listImage, childFragmentManager, pickBackgroundLauncher)
        
    }

    fun setAvatar(view: View, urlAvatar: MutableList<ListAvatar>) {
        val listAvatar: RecyclerView = view.findViewById(R.id.customize_fragment_list_avatar)
        listAvatar.adapter = ListAvatarAdapter(urlAvatar, pickImageLauncher)
    }

    fun setButtonView(view: View) {
        val listCallIcon: RecyclerView = view.findViewById(R.id.customize_fragment_list_icon_call)

        // Tạo danh sách mẫu
        val nameIconCall = ListButtonCall
        listCallIcon.adapter = ListCallButtonAdapter(nameIconCall, this)
    }

    fun setListImage(view: View) {
        val listImageView: RecyclerView =
            view.findViewById(R.id.customize_fragment_list_background_image)

        val listImage = ListCategoryAll

        listImageView.adapter =
            CustomizeAdapter(listImage, childFragmentManager, pickBackgroundLauncher)
    }
}