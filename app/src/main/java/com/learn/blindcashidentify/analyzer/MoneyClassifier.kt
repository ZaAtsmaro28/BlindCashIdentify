package com.learn.blindcashidentify.analyzer

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class MoneyClassifier(context: Context) {
    private val interpreter: Interpreter

    init {
        val assetFileDescriptor = context.assets.openFd("nominal_model.tflite")
        val fileInputStream = assetFileDescriptor.createInputStream()
        val modelByteBuffer = fileInputStream.channel.map(
            java.nio.channels.FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
        interpreter = Interpreter(modelByteBuffer)
    }

    fun classifyMoney(inputImageBuffer: ByteBuffer): String {
        val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), org.tensorflow.lite.DataType.FLOAT32)
        inputFeature.loadBuffer(inputImageBuffer)

        val outputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 7), org.tensorflow.lite.DataType.FLOAT32)

        interpreter.run(inputFeature.buffer, outputFeature.buffer.rewind())

        val outputArray = outputFeature.floatArray
        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

        return when (maxIndex) {
            0 -> "1.000"
            1 -> "2.000"
            2 -> "5.000"
            3 -> "10.000"
            4 -> "20.000"
            5 -> "50.000"
            6 -> "100.000"
            else -> "Tidak Dikenali"
        }
    }
}
