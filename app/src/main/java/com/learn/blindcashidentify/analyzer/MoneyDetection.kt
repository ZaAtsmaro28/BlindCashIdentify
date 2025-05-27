package com.learn.blindcashidentify.analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.Detection

class MoneyDetection(context: Context) {

    private val detector: ObjectDetector

    init {
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(1) // fokus pada satu objek uang saja
            .setScoreThreshold(0.5f) // confidence minimum
            .build()

        detector = ObjectDetector.createFromFileAndOptions(
            context,
            "money_detection_model.tflite",
            options
        )
    }

    fun detectMoney(bitmap: Bitmap): RectF? {
        // Buat TensorImage dengan tipe FLOAT32
        val inputImage = TensorImage(DataType.FLOAT32)
        inputImage.load(bitmap)

        // Lakukan normalisasi: ubah pixel dari [0..255] ke [0..1]
        val imageProcessor = ImageProcessor.Builder()
            .add(NormalizeOp(0.0f, 255.0f))
            .build()
        val normalizedImage = imageProcessor.process(inputImage)

        val results: List<Detection> = detector.detect(normalizedImage)

        results.firstOrNull()?.let {
            println("Detected with score: ${it.categories.firstOrNull()?.score}")
        }

        return results.firstOrNull()?.boundingBox
    }
}
