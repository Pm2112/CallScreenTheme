package com.example.callscreenapp.process

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

fun getBitmapFromGallery(context: Context, uri: Uri): Bitmap? {
    var inputStream: InputStream? = null
    return try {
        inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        inputStream?.close()
    }
}

fun Context.saveImageFromUri(uri: Uri): String? {
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val fileName = "${System.currentTimeMillis()}.jpg"
    val directory = filesDir  // Sử dụng bộ nhớ trong
    val file = File(directory, fileName)

    var outputStream: OutputStream? = null

    try {
        outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        inputStream?.close()
        outputStream?.close()
    }

    return file.absolutePath
}

class DownLoadImage {
    fun downloadImageAndSave(context: Context, imageUrl: String, fileName: String) {
        Log.d("zzzxxx", "Downloading image from: $imageUrl")
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE) // Không sử dụng cache đĩa
            .skipMemoryCache(true) // Không sử dụng cache bộ nhớ
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Log.d("zzzxxx", "Image is downloaded, now saving...")
//                    saveBitmapToFile(context, resource, fileName)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Thực hiện dọn dẹp nếu cần thiết
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Log.e("zzzxxx", "Failed to download image.")
                }
            })
    }
}

fun downloadImageAndSave(context: Context, imageUrl: String, fileName: String) {
    Log.d("zzzxxx", "Downloading image from: $imageUrl")

    // Sử dụng Glide để tải file gốc mà không chuyển đổi thành bitmap
    Glide.with(context)
        .downloadOnly()
        .load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.DATA) // Sử dụng cache đĩa để tối ưu hóa tải file
        .skipMemoryCache(true) // Không sử dụng cache bộ nhớ
        .into(object : CustomTarget<File>() {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                Log.d("zzzxxx", "Image is downloaded, now saving...")
                saveFileToInternalStorage(context, resource, fileName) // Lưu file gốc
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // Dọn dẹp nếu cần thiết
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                Log.e("zzzxxx", "Failed to download image.")
            }
        })
}

// Hàm lưu file ảnh gốc từ file tải về vào bộ nhớ trong
private fun saveFileToInternalStorage(context: Context, sourceFile: File, fileName: String) {
    val destinationFile = File(context.filesDir, fileName)
    try {
        // Tạo output stream để lưu file
        FileInputStream(sourceFile).use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream) // Sao chép file gốc
            }
        }
        Log.d("zzzxxx", "Image saved successfully: $fileName at ${destinationFile.absolutePath}")
    } catch (e: Exception) {
        Log.e("zzzxxx", "Error while saving image: $fileName", e)
    }
}

// Hàm hiển thị ảnh từ bộ nhớ trong (bao gồm cả ảnh động)
fun displayImageFromInternalStorage(context: Context, imageView: ImageView, fileName: String) {
    val filePath = File(context.filesDir, fileName)
    if (filePath.exists()) {
        // Sử dụng Glide để hiển thị hình ảnh từ file đã lưu (bao gồm cả ảnh động)
        Glide.with(context)
            .load(filePath) // Glide tự nhận diện và hiển thị đúng định dạng ảnh
            .diskCacheStrategy(DiskCacheStrategy.NONE) // Không sử dụng cache đĩa
            .skipMemoryCache(true) // Không sử dụng cache bộ nhớ
            .into(imageView) // Hiển thị trên ImageView

        Log.d("zzzxxx", "Image displayed successfully from: ${filePath.absolutePath}")
    } else {
        Log.e("zzzxxx", "File does not exist: $fileName")
        // Đặt ảnh placeholder nếu file không tồn tại
    }
}