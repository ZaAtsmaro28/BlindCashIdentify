package com.learn.blindcashidentify.dto

data class PredictResult(
    val top:String, val confidence: Float, val predictions: List<PredictValue>
)
