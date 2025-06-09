package com.learn.blindcashidentify.analyzer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

class YOLOv8TFLiteDetector(context: Context) {

    private val interpreter: Interpreter
    private val inputImageSize = 640
    private val numClasses = 7
    private val labels = listOf("1000", "2000", "5000", "10000", "20000", "50000", "100000")
    private val outputBuffer = Array(1) { Array(8400) { FloatArray(7 + numClasses) } }

    init {
        interpreter = Interpreter(loadModelFile(context, "cash_model.tflite"))
    }

    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    fun detect(bitmap: Bitmap): List<DetectionResult> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputImageSize, inputImageSize, true)
        val input = preprocess(resizedBitmap)

        interpreter.run(input, outputBuffer)

//        return postprocess(outputBuffer[0], bitmap.width, bitmap.height)
        return emptyList()
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(1 * inputImageSize * inputImageSize * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputImageSize * inputImageSize)
        bitmap.getPixels(intValues, 0, inputImageSize, 0, 0, inputImageSize, inputImageSize)

        for (pixel in intValues) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }
        buffer.rewind()
        return buffer
    }

    private fun postprocess(output: Array<FloatArray>, origWidth: Int, origHeight: Int): List<DetectionResult> {
        val results = mutableListOf<DetectionResult>()

        for (i in output.indices) {
            val row = output[i]
            val x = row[0]
            val y = row[1]
            val w = row[2]
            val h = row[3]
            val objectness = row[4]

            if (objectness < 0.4f) continue

            val classes = row.copyOfRange(5, 5 + numClasses)
            val maxClassScore = classes.maxOrNull() ?: 0f
            val classIndex = classes.withIndex().maxByOrNull { it.value }?.index ?: -1


            val confidence = objectness * maxClassScore
            if (confidence < 0.5f) continue

            val x0 = (x - w / 2) * origWidth / inputImageSize
            val y0 = (y - h / 2) * origHeight / inputImageSize
            val x1 = (x + w / 2) * origWidth / inputImageSize
            val y1 = (y + h / 2) * origHeight / inputImageSize

            results.add(
                DetectionResult(
                    label = labels[classIndex],
                    confidence = confidence,
                    boundingBox = RectF(x0, y0, x1, y1)
                )
            )
        }

        return nonMaxSuppression(results, 0.5f)
    }

    private fun nonMaxSuppression(detections: List<DetectionResult>, iouThreshold: Float): List<DetectionResult> {
        val output = mutableListOf<DetectionResult>()
        val sorted = detections.sortedByDescending { it.confidence }.toMutableList()

        while (sorted.isNotEmpty()) {
            val best = sorted.removeAt(0)
            output.add(best)

            val it = sorted.iterator()
            while (it.hasNext()) {
                val other = it.next()
                if (iou(best.boundingBox, other.boundingBox) > iouThreshold) {
                    it.remove()
                }
            }
        }

        return output
    }

    private fun iou(a: RectF, b: RectF): Float {
        val intersection = RectF(
            max(a.left, b.left),
            max(a.top, b.top),
            min(a.right, b.right),
            min(a.bottom, b.bottom)
        )
        val interArea = max(0f, intersection.width()) * max(0f, intersection.height())
        val unionArea = a.width() * a.height() + b.width() * b.height() - interArea
        return interArea / unionArea
    }

    data class DetectionResult(
        val label: String,
        val confidence: Float,
        val boundingBox: RectF
    )
}
