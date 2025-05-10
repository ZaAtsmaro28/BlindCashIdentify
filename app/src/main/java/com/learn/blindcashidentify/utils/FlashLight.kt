package com.learn.blindcashidentify.utils

import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
fun AutoFlashlightOn(controller: LifecycleCameraController) {
    DisposableEffect(Unit) {
        controller.enableTorch(true)
        onDispose {
            controller.enableTorch(false)
        }
    }
}

