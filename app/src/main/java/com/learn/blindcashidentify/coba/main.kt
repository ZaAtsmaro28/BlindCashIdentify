package com.learn.blindcashidentify.coba

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.util.Base64 // gunakan java.util.Base64 untuk project non-Android

fun main() {
    val apiKey = "6KJJSxKAv8Uj88jlamCi"
    val projectId = "deteksi_nominal-b844n"
    val version = "3"
    val imagePath = "D:/BlindCashIdentify/app/src/main/java/com/learn/blindcashidentify/coba/test10.jpg" // Ganti dengan path gambar kamu
    val url = "https://detect.roboflow.com/$projectId/$version?api_key=$apiKey"

    try {
        val imageFile = File(imagePath)
        if (!imageFile.exists()) {
            println("Gambar tidak ditemukan di path: $imagePath")
            return
        }

        val imageBytes = imageFile.readBytes()
        val base64Image = Base64.getEncoder().encodeToString(imageBytes)

        val requestBody = RequestBody.create("application/x-www-form-urlencoded".toMediaTypeOrNull(), base64Image)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            println("Hasil deteksi:\n${response.body?.string()}")
        } else {
            println("Gagal. Kode: ${response.code}, Pesan: ${response.message}")
        }

    } catch (e: Exception) {
        println("Terjadi error: ${e.message}")
    }
}
