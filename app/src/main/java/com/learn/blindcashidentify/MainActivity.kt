package com.learn.blindcashidentify

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.learn.blindcashidentify.viewmodel.PredictViewModel
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DebugScreen()
        }
    }
}

fun announceForAccessibility(context: Context, message: String) {
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    if (accessibilityManager.isEnabled) {
        accessibilityManager.interrupt()
        accessibilityManager.sendAccessibilityEvent(
            AccessibilityEvent.obtain().apply {
                eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
                className = context.javaClass.name
                packageName = context.packageName
                text.add(message)
            }
        )
    }
}

@Composable
fun DebugScreen() {
    val predictViewModel: PredictViewModel = viewModel()
    val result = predictViewModel.result
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(result) {
        result?.let {
            val nominal = it.top
            val message = "Hasil Deteksi. Nominal $nominal."
            announceForAccessibility(context, message)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        bitmap = bmp
        bmp?.let {
            imageFile = saveBitmapToCache(context.cacheDir, it)

            if (imageFile != null){
                predictViewModel.predict(imageFile!!)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { launcher.launch() },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Yellow,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Ambil Gambar",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        bitmap?.let {
            AsyncImage(
                model = it,
                contentDescription = "Hasil",
                modifier = Modifier
                    .size(220.dp)
                    .border(2.dp, Color.White, RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        result?.let {
            Text(
                text = "Hasil Deteksi:",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Nominal: ${formatWithUnderscore(it.top.toInt())}",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}


fun saveBitmapToCache(cacheDir: File, bitmap: Bitmap): File? {
    return try {
        val file = File(cacheDir, "preview_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun formatWithUnderscore(number: Int): String {
    return number.toString()
        .reversed()
        .chunked(3)
        .joinToString("_")
        .reversed()
}
