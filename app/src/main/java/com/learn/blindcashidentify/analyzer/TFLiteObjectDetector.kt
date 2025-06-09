import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions

class TFLiteObjectDetector(context: Context) {
    private var detector: ObjectDetector? = null

    init {
        try {
            // Konfigurasi options
            val options = ObjectDetectorOptions.builder()
                .setMaxResults(5) // Jumlah maksimum hasil deteksi
                .setScoreThreshold(0.5f) // Min confidence (0-1)
                .build()

            // Inisialisasi detector
            detector = ObjectDetector.createFromFileAndOptions(
                context,
                "cash_model.tflite", // Nama file model
                options
            )
        } catch (e: Exception) {
            Log.e("deteksi", "Gagal memuat model", e)
        }
    }

    fun detect(bitmap: Bitmap): List<DetectionResult> {
        Log.d("deteksi", "call detect")

        if (detector == null) return emptyList()

        // Konversi Bitmap ke TensorImage
        val image = TensorImage.fromBitmap(bitmap)

        // Deteksi objek
        val results = detector?.detect(image)

        // Map hasil ke format sederhana
        return results?.map { detection ->
            DetectionResult(
                label = detection.categories[0].label,
                confidence = detection.categories[0].score,
                boundingBox = detection.boundingBox
            )
        } ?: emptyList()
    }

    data class DetectionResult(
        val label: String,
        val confidence: Float,
        val boundingBox:RectF)
}