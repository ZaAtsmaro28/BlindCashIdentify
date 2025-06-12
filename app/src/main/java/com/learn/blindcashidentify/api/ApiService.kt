package com.learn.blindcashidentify.api

import com.learn.blindcashidentify.dto.PredictRequest
import com.learn.blindcashidentify.dto.PredictResult
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("3?api_key=6KJJSxKAv8Uj88jlamCi")
    @Headers("Content-Type: application/x-www-form-urlencoded")  // Important for raw string
    suspend fun detect(@Body base64Image: String): PredictResult
}