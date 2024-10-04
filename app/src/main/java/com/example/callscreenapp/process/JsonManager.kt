package com.example.callscreenapp.process

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

val gson = Gson()

// Tạo hoặc khởi tạo tệp JSON
class JsonManager {
    fun createJsonFileIfNotExist(context: Context, fileName: String) {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            val defaultContent = "[]" // Khởi tạo với một mảng rỗng
            saveJsonToFile(context, defaultContent, fileName)
        }
    }

    fun createMultipleJsonFilesIfNotExists(context: Context, fileNames: List<String>) {
        for (fileName in fileNames) {
            val file = File(context.filesDir, fileName)

            if (file.exists()) {
                // File đã tồn tại, không cần tạo lại
                continue
            }

            try {
                FileWriter(file).use { writer ->
                    writer.write("[]")
                }
                println("JSON file '$fileName' created successfully.")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun <T> saveListToJsonFile(context: Context, fileName: String, list: List<T>) {
        val file = File(context.filesDir, fileName)
        val gson = Gson()
        val jsonString = gson.toJson(list)

        try {
            FileWriter(file).use { writer ->
                writer.write(jsonString)
            }
            println("JSON file '$fileName' updated with list data.")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    inline fun <reified T> loadListFromJsonFile(context: Context, fileName: String): List<T>? {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            val jsonString = file.readText()
            val gson = Gson()
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            null
        }
    }
}


// Lưu dữ liệu JSON vào tệp
fun saveJsonToFile(context: Context, json: String, fileName: String): Boolean {
    return try {
        val fileOutputStream: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        fileOutputStream.use {
            it.write(json.toByteArray())
        }

        // Kiểm tra xem file đã được lưu thành công hay chưa
        val file = context.getFileStreamPath(fileName)
        if (file.exists()) {
            // Đọc lại nội dung của file để đảm bảo dữ liệu đã lưu đúng
            val fileInputStream: FileInputStream = context.openFileInput(fileName)
            val savedJson = fileInputStream.bufferedReader().use { it.readText() }
            if (savedJson == json) {
                Log.d("saveJsonToFile", "Lưu file thành công!")
                true
            } else {
                Log.e("saveJsonToFile", "Dữ liệu lưu không khớp!")
                false
            }
        } else {
            Log.e("saveJsonToFile", "File không tồn tại!")
            false
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("saveJsonToFile", "Lỗi khi lưu file: ${e.message}")
        false
    }
}

// Đọc dữ liệu JSON từ tệp
fun readJsonFromFile(context: Context, fileName: String): String? {
    return try {
        val fileInputStream: FileInputStream = context.openFileInput(fileName)
        fileInputStream.bufferedReader().use {
            it.readText()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

// Chuyển đổi JSON thành danh sách đối tượng
inline fun <reified T> jsonToList(context: Context, fileName: String): MutableList<T>? {
    val json = readJsonFromFile(context, fileName)
    return json?.let {
        val listType = object : TypeToken<MutableList<T>>() {}.type
        gson.fromJson(it, listType)
    }
}

// Thêm đối tượng vào tệp JSON
inline fun <reified T> appendObjectToJson(context: Context, newObject: T, fileName: String) {
    val currentList: MutableList<T> = jsonToList(context, fileName) ?: mutableListOf()
    currentList.add(newObject)
    val updatedJson = gson.toJson(currentList)
    saveJsonToFile(context, updatedJson, fileName)
}

// Sửa đối tượng trong tệp JSON
inline fun <reified T> updateObjectInJson(context: Context, updatedObject: T, fileName: String, matchCondition: (T) -> Boolean) {
    val currentList: MutableList<T> = jsonToList(context, fileName) ?: mutableListOf()
    val index = currentList.indexOfFirst(matchCondition)
    if (index != -1) {
        currentList[index] = updatedObject
        val updatedJson = gson.toJson(currentList)
        saveJsonToFile(context, updatedJson, fileName)
    }
}

// Cập nhật đối tượng trong tệp JSON nếu đối tượng trùng nhau
inline fun <reified T> updateObjectIfExistsInJson(context: Context, newObject: T, fileName: String, matchCondition: (T) -> Boolean) {
    val currentList: MutableList<T> = jsonToList(context, fileName) ?: mutableListOf()
    val index = currentList.indexOfFirst(matchCondition)
    if (index != -1) {
        currentList[index] = newObject
    } else {
        currentList.add(newObject)
    }
    val updatedJson = gson.toJson(currentList)
    saveJsonToFile(context, updatedJson, fileName)
}

// Xóa đối tượng từ tệp JSON
inline fun <reified T> removeObjectFromJson(context: Context, fileName: String, targetObject: T) {
    val currentList: MutableList<T> = jsonToList(context, fileName) ?: mutableListOf()
    currentList.removeAll { it == targetObject }
    val updatedJson = gson.toJson(currentList)
    saveJsonToFile(context, updatedJson, fileName)
}

// Xóa toàn bộ dữ liệu trong tệp JSON
fun clearJsonFile(context: Context, fileName: String) {
    val emptyJson = "[]" // Khởi tạo với một mảng rỗng
    saveJsonToFile(context, emptyJson, fileName)
}