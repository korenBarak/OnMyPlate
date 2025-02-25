package com.example.onmyplate.model

import android.graphics.Bitmap
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.example.onmyplate.BuildConfig
import com.example.onmyplate.base.MyApplication
import com.example.onmyplate.utils.extension.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CloudinaryModel {
    init {
        val config = mapOf(
            "cloud_name" to BuildConfig.CLOUD_NAME,
            "api_key" to BuildConfig.API_KEY,
            "api_secret" to BuildConfig.API_SECRET
        )

        MyApplication.Globals.context?.let {
            MediaManager.init(it, config)
            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()
        }
    }

    suspend fun uploadImages(
        bitmaps: List<Bitmap>, name: String,
        onSuccess: (List<String?>) -> Unit,
        onError: (List<String?>) -> Unit
    ) {
        val context = MyApplication.Globals.context ?: return

        val uploadedUrls = withContext(Dispatchers.IO) {
            bitmaps.mapIndexed { index, bitmap ->
                async {
                    val parsedFile = bitmap.toFile(context, name + index)
                    uploadImage(parsedFile)
                }
            }.awaitAll()
        }

        if (uploadedUrls.contains(null)) {
            onError(uploadedUrls)
        } else {
            onSuccess(uploadedUrls)
        }
    }

    private suspend fun uploadImage(file: File): String? = suspendCoroutine { continuation ->
        MediaManager.get().upload(file.path).option("folder", "image").callback(
            object : UploadCallback {
                override fun onStart(requestId: String?) {
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<*, *>) {
                    val url = resultData["secure_url"] as? String ?: ""
                    Log.d("TAG", "success")
                    continuation.resume(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.d("TAG", "error")
                    continuation.resume(null)
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                }

            },
        )
            .dispatch()

    }

}