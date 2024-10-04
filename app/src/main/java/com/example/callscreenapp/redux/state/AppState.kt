package com.example.callscreenapp.redux.state

import com.example.callscreenapp.model.ListAvatar
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.ui.fragment.show_image.ShowImageFragment


data class AppState(
    val exampleProperty: String = "initial state",
    val iconCallShowGreen: String = "",
    val iconCallShowRed: String = "",
    val avatarUrl: String = "",
    val avatarPosition: Int = 0,
    val categoryName: String = "All",
    val backgroundUrl: String = "",
    val phoneNumber: String = "",
    val avatarCustomize: String ="",
    val btnCallCustomizeGreen: String = "",
    val btnCallCustomizeRed: String = "",
    val backgroundCustomize: String = "",
    val showImageFragment: Boolean = false,
    val refresh: Boolean = false,
    val isDefaultDialer: Boolean = false
)
