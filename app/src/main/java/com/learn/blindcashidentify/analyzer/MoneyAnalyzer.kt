package com.learn.blindcashidentify.analyzer

import MoneyValidator
import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import android.graphics.Bitmap
import android.util.Log


class MoneyAnalyzer(
    private val context: Context,
    private val onResult: (Bitmap?, String) -> Unit
) : ImageAnalysis.Analyzer {

    private val validator = MoneyValidator(context)
    private val classifier = MoneyClassifier(context)
    private val detector = MoneyDetection(context)

    override fun analyze(imageProxy: ImageProxy) {
        Log.d("COBA", "TEST")
        val bitmap = imageProxy.toBitmap()
        Log.d("COBA", "TEST1")
        // Deteksi bounding box uang
//        val boundingBox = detector.detectMoney(bitmap)
        Log.d("COBA", "TEST2")
//        if (boundingBox != null) {
//            val cropped = cropBitmap(bitmap, boundingBox)
            val input = validator.bitmapToByteBuffer(bitmap)
            val resultValidation = validator.validateMoney(input)
            val resultNominal = classifier.classifyMoney(input)

            onResult(bitmap, "Keaslian: $resultValidation\nNominal: $resultNominal")
//        } else {
//            onResult(null, "Uang tidak terdeteksi.")
//        }
        Log.d("COBA", "TEST3")
        imageProxy.close()
    }

    private fun cropBitmap(source: Bitmap, boundingBox: android.graphics.RectF): Bitmap {
        val left = boundingBox.left.toInt().coerceAtLeast(0)
        val top = boundingBox.top.toInt().coerceAtLeast(0)
        val right = boundingBox.right.toInt().coerceAtMost(source.width)
        val bottom = boundingBox.bottom.toInt().coerceAtMost(source.height)

        return Bitmap.createBitmap(source, left, top, right - left, bottom - top)
    }
}
