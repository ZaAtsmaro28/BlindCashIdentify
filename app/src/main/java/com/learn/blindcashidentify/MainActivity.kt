package com.learn.blindcashidentify

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.learn.blindcashidentify.analyzer.MoneyAnalyzer
import com.learn.blindcashidentify.ui.theme.BlindCashIdentifyTheme
import com.learn.blindcashidentify.utils.AutoFlashlightOn
import com.learn.blindcashidentify.utils.CameraPreview

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(this, CAMERAX_PERMISSIONS, 0)
        }

        setContent {
            BlindCashIdentifyTheme {
                cameraScreen()
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun cameraScreen(){
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val scaffoldState = rememberBottomSheetScaffoldState()
            var result by remember { mutableStateOf("Menunggu hasil...") }

            val controller = remember {
                LifecycleCameraController(applicationContext).apply {
                    setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                    setImageAnalysisAnalyzer(
                        ContextCompat.getMainExecutor(this@MainActivity),
                        MoneyAnalyzer(applicationContext) {
                            result = it
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                // Preview Kamera
                CameraPreview(
                    controller = controller,
                    modifier = Modifier.fillMaxSize()
                )

                // Hasil deteksi
                Box(
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.Center)
                        .padding(16.dp)
                        .semantics {
                            contentDescription = "Mengaktifkan Kamera"
                        }
                ) {
                    Text(
                        text = "Hasil: $result",
                        fontSize = 20.sp,
                        color = Color.Yellow
                    )
                }
            }


            AutoFlashlightOn(controller)
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}
