package com.example.callscreenapp.process

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.callscreenapp.model.Animation
import com.example.callscreenapp.model.DataCate

class DataManager {
    companion object {
        private var jsonManager = JsonManager()

        var BackgroundUrlShow: MutableLiveData<String> = MutableLiveData()
        var ListAnimationSave: MutableLiveData<List<Animation>> = MutableLiveData()
        var EffectButton: MutableLiveData<String> = MutableLiveData()

        var isAvatar: MutableLiveData<Boolean> = MutableLiveData()
        var dataCategory: MutableLiveData<List<DataCate>> = MutableLiveData()

        fun updateEffectButtonFromBackgroundUrl() {
            // Lấy giá trị hiện tại của BackgroundUrlShow
            val backgroundUrl = BackgroundUrlShow.value ?: return

            // Lấy danh sách hiện tại từ ListAnimationSave
            val currentList = ListAnimationSave.value.orEmpty()

            // Tìm đối tượng có url trùng với BackgroundUrlShow
            val matchingAnimation = currentList.find { it.url == backgroundUrl }

            // Kiểm tra nếu tìm thấy đối tượng
            if (matchingAnimation != null) {
                // Gán animation của đối tượng đó vào EffectButton
                EffectButton.value = matchingAnimation.animation
                Log.d("DataManager", "EffectButton updated with animation: ${matchingAnimation.animation}")
            } else {
                EffectButton.value = "default"
            }
        }

        fun addAnimationToListAnimationSave(context: Context, newQuickResponse: Animation) {
            // Lấy danh sách hiện tại hoặc tạo danh sách mới nếu danh sách trống
            val currentList = ListAnimationSave.value.orEmpty().toMutableList()

            // Tìm xem có đối tượng nào với URL đã tồn tại trong danh sách hay không
            val existingAnimation = currentList.find { it.url == newQuickResponse.url }

            if (existingAnimation != null) {
                // Nếu đã có URL trong danh sách, cập nhật animation mới cho đối tượng đó
                currentList[currentList.indexOf(existingAnimation)] = existingAnimation.copy(animation = newQuickResponse.animation)
            } else {
                // Nếu URL chưa tồn tại, thêm đối tượng mới vào đầu danh sách
                currentList.add(0, newQuickResponse)
            }

            // Cập nhật giá trị cho ListAnimationSave
            ListAnimationSave.value = currentList

            // Lưu danh sách vào file JSON
            jsonManager.saveListToJsonFile(context, "save_animation.json", currentList)
        }
        fun updateAnimationToListAnimationSave(
            context: Context,
            oldQuickResponse: Animation,
            newQuickResponse: Animation
        ) {
            // Lấy danh sách hiện tại từ ListHistoryScan
            val currentList = ListAnimationSave.value.orEmpty().toMutableList()

            // Tìm vị trí của oldQuickResponse trong danh sách
            val index = currentList.indexOfFirst { it == oldQuickResponse }

            // Kiểm tra nếu tìm thấy oldQuickResponse trong danh sách
            if (index != -1) {
                // Thay thế oldQuickResponse bằng newQuickResponse
                currentList[index] = newQuickResponse

                // Cập nhật lại giá trị của ListHistoryScan
                ListAnimationSave.value = currentList

                // Lưu danh sách đã cập nhật vào file JSON
                jsonManager.saveListToJsonFile(context, "save_animation.json", currentList)
            } else {
                // Xử lý trường hợp không tìm thấy oldQuickResponse, nếu cần
                Log.e("save_animation", "Item not found in the list")
            }
        }
        fun removeAnimationToListAnimationSave(context: Context, quickResponseToRemove: Animation) {
            val currentList = ListAnimationSave.value.orEmpty().toMutableList()
            val itemRemoved = currentList.remove(quickResponseToRemove)
            if (itemRemoved) {
                ListAnimationSave.value = currentList
                jsonManager.saveListToJsonFile(context, "save_animation.json", currentList)
            }
        }
    }


}