package com.example.callscreenapp.redux.action

import androidx.fragment.app.Fragment
import com.example.callscreenapp.model.ListAvatar

sealed class AppAction {
    data class ExampleAction(val updateValue: String) : AppAction()
    data class SetIconCallShowId(val updateValueGreen: String, val updateValueRed: String) : AppAction()
    data class SetAvatarUrl(val updateValue: String) : AppAction()
    data class SetAvatarPosition(val updateValue: Int) : AppAction()
    data class SetCategory(val updateValue: String) : AppAction()
    data class SetBackgroundUrl(val updateValue: String) : AppAction()
    data class SetPhoneNumber(val updateValue: String) : AppAction()
    data class SetAvatarCustomize(val updateValue: String) : AppAction()
    data class SetButtonCallCustomize(val updateValueGreen: String, val updateValueRed: String) : AppAction()
    data class SetBackgroundCustomize(val updateValue: String) : AppAction()
    data class ShowImageFragment(val updateValue: Boolean): AppAction()
    data class Refresh(val updateValue: Boolean) : AppAction()
    data class IsDefaultDialer(val updateValue: Boolean) : AppAction()
}