package com.learn.blindcashidentify.repository

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.learn.blindcashidentify.dto.PredictResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File

open class PredictRepository {
    open suspend fun detect(imageFile: File): PredictResult? = withContext(Dispatchers.IO) {
        val apiKey = "6KJJSxKAv8Uj88jlamCi"
        val projectId = "deteksi_nominal-b844n"
        val version = "3"
        val url = "https://detect.roboflow.com/$projectId/$version?api_key=$apiKey"

        try {
            if (!imageFile.exists()) {
                Log.e("Detect", "File tidak ditemukan.")
                return@withContext null
            }

            val imageBytes = imageFile.readBytes()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            val requestBody = RequestBody.create(
                "application/x-www-form-urlencoded".toMediaTypeOrNull(),
                base64Image
            )

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("gambar", "Response: $responseBody")

                responseBody?.let {
                    val gson = Gson()
                    return@withContext gson.fromJson(it, PredictResult::class.java)
                } ?: run {
                    Log.e("gambar", "Response body kosong")
                    return@withContext null
                }
            } else {
                Log.e("gambar", "Gagal. Kode: ${response.code}, Pesan: ${response.message}")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e("gambar", "Terjadi error: ${e.message}")
            return@withContext null
        }
    }
}
