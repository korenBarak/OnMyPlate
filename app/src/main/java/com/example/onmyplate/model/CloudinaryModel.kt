package com.example.onmyplate.model

import android.graphics.Bitmap
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.example.onmyplate.base.MyApplication
import com.example.onmyplate.utils.extension.toFile
import java.io.File

class CloudinaryModel {
    init {
        val config = mapOf(
            "cloud_name" to "dqdtczdeq",
            "api_key" to "965486319616164",
            "api_secret" to "Gw5aTQZD2ZDVk4ZyN40F5cM-ia4"
        )

        MyApplication.Globals.context?.let {
            MediaManager.init(it, config)
            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()
        }
    }

    fun uploadImage(
        bitmap: Bitmap,
        name: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val context = MyApplication.Globals.context ?: return
        val file : File = bitmap.toFile(context, name)

        MediaManager.get().upload(file.path).option("folder", "image").callback(
            object : UploadCallback {
                override fun onStart(requestId: String?) {
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<*, *>) {
                    val url = resultData["secure_url"] as? String ?: ""
                    Log.d("TAG", "success")
                    onSuccess(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.d("TAG", "error")
                    onError(error?.description ?: "unknown error")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                }

            },
        )
            .dispatch()

    }

}