package com.learn.blindcashidentify.analyzer

import MoneyValidator
import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class MoneyAnalyzer(
    private val context: Context,
    private val onResult: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val validator = MoneyValidator(context)

    override fun analyze(imageProxy: ImageProxy) {
        val bitmap = imageProxy.toBitmap()
        val input = validator.bitmapToByteBuffer(bitmap)
        val result = validator.validateMoney(input)
        onResult(result)
        imageProxy.close()
    }

}
