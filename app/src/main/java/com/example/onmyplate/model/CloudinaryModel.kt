package com.example.onmyplate.model

import android.graphics.Bitmap
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.example.onmyplate.BuildConfig
import com.example.onmyplate.base.Constants
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

    fun uploadImage(
        bitmap: Bitmap,
        name: String,
        folderName: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val context = MyApplication.Globals.context ?: return
        val file: File = bitmap.toFile(context, name)

        MediaManager.get().upload(file.path).option("folder", folderName).callback(
            object : UploadCallback {
                override fun onStart(requestId: String?) {
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<*, *>) {
                    val url = resultData["secure_url"] as? String ?: ""
                    onSuccess(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "unknown error")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                }

            },
        )
            .dispatch()

    }

    suspend fun uploadPostImages(
        bitmaps: List<Bitmap>, name: String,
        onSuccess: (List<String?>) -> Unit,
        onError: (List<String?>) -> Unit
    ) {
        val context = MyApplication.Globals.context ?: return

        val uploadedUrls = withContext(Dispatchers.IO) {
            bitmaps.mapIndexed { index, bitmap ->
                async {
                    val parsedFile = bitmap.toFile(context, name + index)
                    suspendUploadImage(parsedFile)
                }
            }.awaitAll()
        }

        if (uploadedUrls.contains(null)) {
            onError(uploadedUrls)
        } else {
            onSuccess(uploadedUrls)
        }
    }

    private suspend fun suspendUploadImage(file: File): String? = suspendCoroutine { continuation ->
        MediaManager.get().upload(file.path).option("folder", Constants.CloudinaryFolders.IMAGE)
            .callback(
                object : UploadCallback {
                    override fun onStart(requestId: String?) {
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    }

                    override fun onSuccess(requestId: String?, resultData: MutableMap<*, *>) {
                        val url = resultData["secure_url"] as? String ?: ""
                        continuation.resume(url)
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        continuation.resume(null)
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    }

                },
            )
            .dispatch()

    }

    fun updateImage(
        bitmap: Bitmap,
        name: String,
        folderName: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val context = MyApplication.Globals.context ?: return
        val file: File = bitmap.toFile(context, "")

        MediaManager.get().upload(file.path).option("folder", folderName).option("public_id", name).callback(
            object : UploadCallback {
                override fun onStart(requestId: String?) {
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                }

                override fun onSuccess(requestId: String?, resultData: MutableMap<*, *>) {
                    val url = resultData["secure_url"] as? String ?: ""
                    onSuccess(url)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "unknown error")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                }

            },
        )
            .dispatch()

    }
}