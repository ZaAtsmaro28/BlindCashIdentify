package com.learn.blindcashidentify

import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.learn.blindcashidentify.analyzer.MoneyDetection

class DebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DebugScreen()
        }
    }
}

@Composable
fun DebugScreen() {
    val context = LocalContext.current
    val moneyDetector = remember { MoneyDetection(context) }

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var boundingBox by remember { mutableStateOf<RectF?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            originalBitmap = bitmap
            val resultBox = moneyDetector.detectMoney(bitmap)
            boundingBox = resultBox
            if (resultBox != null) {
                croppedBitmap = cropBitmap(bitmap, resultBox)
                Log.d("DebugCompose", "Deteksi uang: $resultBox")
            } else {
                croppedBitmap = null
                Log.d("DebugCompose", "Tidak ada uang terdeteksi.")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { cameraLauncher.launch() }) {
            Text("Ambil Gambar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        croppedBitmap?.let {
            Text("Hasil Deteksi (Cropped):")
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Gambar Uang Terdeteksi",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        } ?: originalBitmap?.let {
            Text("Gambar Asli:")
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Gambar Asli",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        boundingBox?.let {
            Text("Bounding Box: (${it.left.toInt()}, ${it.top.toInt()}, ${it.right.toInt()}, ${it.bottom.toInt()})")
        }
    }
}

private fun cropBitmap(source: Bitmap, boundingBox: RectF): Bitmap {
    val left = boundingBox.left.toInt().coerceAtLeast(0)
    val top = boundingBox.top.toInt().coerceAtLeast(0)
    val right = boundingBox.right.toInt().coerceAtMost(source.width)
    val bottom = boundingBox.bottom.toInt().coerceAtMost(source.height)

    val width = right - left
    val height = bottom - top

    return if (width > 0 && height > 0) {
        Bitmap.createBitmap(source, left, top, width, height)
    } else {
        source
    }
}
