package com.example.callscreenapp.ui.fragment.my_themes

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.callscreenapp.Impl.ShowAdsClickListener
import com.example.callscreenapp.R
import com.example.callscreenapp.adapter.MyThemeAdapter
import com.example.callscreenapp.adapter.PhoneCallListImageAdapter
import com.example.callscreenapp.data.AvatarSize
import com.example.callscreenapp.data.HttpUrlAvatar
import com.example.callscreenapp.data.ListCategoryAll
import com.example.callscreenapp.data.ListCategoryAnimal
import com.example.callscreenapp.data.ListCategoryAnime
import com.example.callscreenapp.data.ListCategoryCastle
import com.example.callscreenapp.data.ListCategoryFantasy
import com.example.callscreenapp.data.ListCategoryGame
import com.example.callscreenapp.data.ListCategoryLove
import com.example.callscreenapp.data.ListCategoryNature
import com.example.callscreenapp.data.ListCategorySea
import com.example.callscreenapp.data.ListCategoryTech
//import com.example.callscreenapp.data.realm
import com.example.callscreenapp.database.Avatar
import com.example.callscreenapp.database.BackgroundTheme
import com.example.callscreenapp.model.MyTheme
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.process.jsonToList
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

class MyThemesFragment : Fragment() {
    private var storeSubscription: (() -> Unit)? = null
    private lateinit var firebase: EvenFirebase

    companion object {
        fun newInstance() = MyThemesFragment()
    }

    private val viewModel: MyThemesViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_themes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebase = EvenFirebase(requireActivity())
        firebase.logFirebaseEvent("my_theme")

        val listMyTheme: RecyclerView = view.findViewById(R.id.my_themes_fragment_list_image)
//        val items: RealmResults<BackgroundTheme> = realm.query<BackgroundTheme>().find()

        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.SPACE_BETWEEN
        }
        listMyTheme.layoutManager = layoutManager

//        val listMyThemeData = mutableListOf<MyTheme>().apply {
//            for (item in items) {
//                add(MyTheme(item.backgroundTheme, item.avatarUrl, item.iconAnswer, item.iconReject))
//            }
//        }


        val listMyThemeData: List<MyTheme> = jsonToList(requireContext(), "mytheme.json") ?: emptyList()
//        Log.d("zzzxxx", listMyThemeData.toString())
//        Log.d("listMyThemeData", listMyThemeData.toString())
//
        listMyTheme.adapter = MyThemeAdapter(listMyThemeData, requireContext())

        storeSubscription = store.subscribe {
            val refresh = store.state.refresh
            requireActivity().runOnUiThread {
                when (refresh) {
                    true -> {
                        refreshAdapter(listMyTheme)
                    }
                    false -> {

                    }
                }
            }
        }
    }

    private fun refreshAdapter(listMyTheme: RecyclerView) {
        val listMyThemeData: List<MyTheme> = jsonToList(requireContext(), "mytheme.json") ?: emptyList()
        listMyTheme.adapter = MyThemeAdapter(listMyThemeData, requireContext())
    }
}