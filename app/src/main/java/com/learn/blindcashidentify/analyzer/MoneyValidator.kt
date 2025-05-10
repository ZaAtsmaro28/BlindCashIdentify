import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MoneyValidator(context: Context) {
    private val interpreter: Interpreter

    init {
        val assetFileDescriptor = context.assets.openFd("validation_model.tflite")
        val fileInputStream = assetFileDescriptor.createInputStream()
        val modelByteBuffer = fileInputStream.channel.map(
            java.nio.channels.FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
        interpreter = Interpreter(modelByteBuffer)
    }

     fun validateMoney(inputImageBuffer: ByteBuffer): String {
        val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), org.tensorflow.lite.DataType.FLOAT32)
        inputFeature.loadBuffer(inputImageBuffer)

        val outputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 4), org.tensorflow.lite.DataType.FLOAT32)

        interpreter.run(inputFeature.buffer, outputFeature.buffer.rewind())

        val outputArray = outputFeature.floatArray
        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
        val labels = listOf("Asli", "Palsu", "Buram", "Tidak Dikenali")
        return labels.getOrElse(maxIndex) { "Tidak diketahui" }

    }

    fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(224 * 224)
        resizedBitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        return byteBuffer
    }

}
