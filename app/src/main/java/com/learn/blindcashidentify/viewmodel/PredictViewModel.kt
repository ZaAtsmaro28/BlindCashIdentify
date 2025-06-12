package com.learn.blindcashidentify.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.blindcashidentify.api.ApiClient
import com.learn.blindcashidentify.dto.PredictResult
import com.learn.blindcashidentify.repository.PredictRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream

class PredictViewModel: ViewModel() {
//    private val repository = PredictRepository(ApiClient.retrofit)
    private val repository = PredictRepository()

    var result by mutableStateOf<PredictResult?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun predict(image: File) {
        viewModelScope.launch {
            isLoading = true
            Log.d("PredictViewModel", image.name)
            try {
                val response = repository.detect(image) // ambil hasilnya
                result = response // simpan ke state
                Log.d("PredictViewModel", "Result: $response")
            } catch (e: HttpException) {
                Log.e("PredictViewModel", "HTTP Error", e)
                Log.d("PredictViewModel", "Error Body: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                Log.e("PredictViewModel", "Exception: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }

}