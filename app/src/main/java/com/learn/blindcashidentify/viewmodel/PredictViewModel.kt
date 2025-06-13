package com.learn.blindcashidentify.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.blindcashidentify.dto.PredictResult
import com.learn.blindcashidentify.repository.PredictRepository
import kotlinx.coroutines.launch
import java.io.File

class PredictViewModel(
    private val repository: PredictRepository = PredictRepository() // default value
) : ViewModel() {
    var result by mutableStateOf<PredictResult?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun predict(image: File) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.detect(image)
                result = response
            } catch (e: Exception) {
                Log.e("PredictViewModel", "Exception: ${e.stackTraceToString()}")
            } finally {
                isLoading = false
            }
        }
    }
}
