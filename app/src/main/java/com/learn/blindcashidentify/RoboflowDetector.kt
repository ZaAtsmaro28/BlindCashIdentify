package com.learn.blindcashidentify

import android.os.AsyncTask
import android.util.Base64
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class RoboflowDetector(
    private val imagePath: String,
    private val callback: (String) -> Unit
) : AsyncTask<Void, Void, String>() {

    companion object {
        private const val API_KEY = "6KJJSxKAv8Uj88jlamCi"
        private const val PROJECT_ID = "deteksi_nominal-b844n"
        private const val VERSION = "3"
    }

    override fun doInBackground(vararg params: Void?): String {
        val urlString = "https://detect.roboflow.com/$PROJECT_ID/$VERSION?api_key=$API_KEY"

        try {
            val imageFile = File(imagePath)
            if (!imageFile.exists()) {
                return "Gambar tidak ditemukan di path: $imagePath"
            }

            // Baca file gambar dan encode ke base64
            val inputStream = FileInputStream(imageFile)
            val bytes = inputStream.readBytes()
            val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

            // Setup koneksi
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            // Kirim data
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.writeBytes(base64Image)
            outputStream.flush()
            outputStream.close()

            // Baca response
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                return response.toString()
            } else {
                return "Gagal. Kode: $responseCode, Pesan: ${connection.responseMessage}"
            }
        } catch (e: Exception) {
            return "Terjadi error: ${e.message}"
        }
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        callback(result)
    }
}