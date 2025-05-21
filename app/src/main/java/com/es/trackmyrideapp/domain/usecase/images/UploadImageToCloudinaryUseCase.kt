package com.es.trackmyrideapp.domain.usecase.images

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.es.trackmyrideapp.BuildConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class UploadImageToCloudinaryUseCase @Inject constructor() {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(uri: Uri): String? = suspendCancellableCoroutine { cont ->
        val unsignedPreset = BuildConfig.CLOUDINARY_UNSIGNED_PRESET

        MediaManager.get().upload(uri)
            .unsigned(unsignedPreset)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}
                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    cont.resume(url, null)
                }
                override fun onError(requestId: String?, error: ErrorInfo?) {
                    cont.resume(null, null)
                }
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    cont.resume(null, null)
                }
            })
            .dispatch()
    }
}
