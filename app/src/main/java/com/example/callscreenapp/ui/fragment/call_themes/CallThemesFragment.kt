package com.example.callscreenapp.ui.fragment.call_themes



import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.callscreenapp.Impl.CategoryAdsClickListener
import com.example.callscreenapp.Impl.CategoryClickListener
import com.example.callscreenapp.Impl.OnItemClickListener
import com.example.callscreenapp.R
import com.example.callscreenapp.adapter.ListTopicAdapter
import com.example.callscreenapp.adapter.PhoneCallListImageAdapter
import com.example.callscreenapp.data.ListCategoryAll
import com.example.callscreenapp.data.ListCategoryAnimal
import com.example.callscreenapp.data.ListCategoryAnime
import com.example.callscreenapp.data.ListCategoryCastle
import com.example.callscreenapp.data.ListCategoryFantasy
import com.example.callscreenapp.data.ListCategoryGame
import com.example.callscreenapp.data.ListCategoryGif
import com.example.callscreenapp.data.ListCategoryLove
import com.example.callscreenapp.data.ListCategoryNature
import com.example.callscreenapp.data.ListCategorySea
import com.example.callscreenapp.data.ListCategoryTech
import com.example.callscreenapp.model.ListTopic
import com.example.callscreenapp.model.PhoneCallListImage
import com.example.callscreenapp.ui.activity.SettingActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CallThemesFragment() : Fragment(), CategoryClickListener {
    private var listImage: List<PhoneCallListImage> = ListCategoryAll
    private lateinit var countCategoryAdsListener: CategoryAdsClickListener
    private var countClickCategory: Int = 0
    companion object {
        private const val ARG_LISTENER = "countCategoryAdsListener"

        fun newInstance(countCategoryAdsListener: CategoryAdsClickListener): CallThemesFragment {
            return CallThemesFragment().apply {
                arguments = Bundle().apply {
                    // Sử dụng Parcelable hoặc Serializable để truyền đối số nếu cần thiết
                    // Truyền các tham số vào Bundle
                }
                this.countCategoryAdsListener = countCategoryAdsListener
            }
        }
    }

    private val viewModel: CallThemesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCategoryItemClicked(category: String) {
        countCategoryAdsListener.onCategoryCountClicked()
        Log.d("zzzxxx", category)
        setListImage(requireView(), when (category) {
            "All" -> ListCategoryAll
            "Gif" -> ListCategoryGif
            "Anime" -> ListCategoryAnime
            "Animal" -> ListCategoryAnimal
            "Love" -> ListCategoryLove
            "Nature" -> ListCategoryNature
            "Game" -> ListCategoryGame
            "Castle" -> ListCategoryCastle
            "Fantasy" -> ListCategoryFantasy
            "Tech" -> ListCategoryTech
            "Sea" -> ListCategorySea
            else -> ListCategoryAll // default to All if category not found
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_call_themes, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listTopic: RecyclerView = view.findViewById(R.id.call_themes_fragment_list_topic)
//        val listImageView: RecyclerView = view.findViewById(R.id.call_themes_fragment_list_image)
        listTopic.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val nameTopic = listOf(
            ListTopic("All"),
            ListTopic("Gif"),
            ListTopic("Anime"),
            ListTopic("Animal"),
            ListTopic("Love"),
            ListTopic("Nature"),
            ListTopic("Game"),
            ListTopic("Castle"),
            ListTopic("Fantasy"),
            ListTopic("Tech"),
            ListTopic("Sea")
        )
        listTopic.adapter = ListTopicAdapter(nameTopic, this)

        val btnSetting: ImageView =
            view.findViewById(R.id.call_themes_fragment_icon_setting)
        btnSetting.setOnClickListener {
//            showPermissionSheet()
            val intent = Intent(context, SettingActivity::class.java)
            startActivity(intent)
        }

        setListImage(view, listImage)

    }

    fun setListImage(view: View, listImage: List<PhoneCallListImage>) {
        Log.d("zzzxxx", "setListImage: $listImage")
        val listImageView: RecyclerView = view.findViewById(R.id.call_themes_fragment_list_image)
        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }

        // Xóa tất cả ItemDecorations hiện tại để tránh cộng dồn
        while (listImageView.itemDecorationCount > 0) {
            listImageView.removeItemDecorationAt(0)
        }

        // Thêm ItemDecoration mới để tạo khoảng cách giữa các phần tử
        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                // Tạo khoảng cách 5dp giữa các hình ảnh
                outRect.set(7, 0, 7, 0)
            }
        }

        listImageView.addItemDecoration(itemDecoration)
        listImageView.layoutManager = layoutManager

        listImageView.adapter = PhoneCallListImageAdapter(listImage, childFragmentManager)
    }

}
