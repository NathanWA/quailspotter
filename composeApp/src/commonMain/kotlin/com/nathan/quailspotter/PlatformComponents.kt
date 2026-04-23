package com.nathan.quailspotter

import androidx.compose.runtime.Composable

@Composable
expect fun CameraScreen(onCapture: (ByteArray) -> Unit)

@Composable
expect fun rememberGalleryLauncher(onResult: (ByteArray) -> Unit): () -> Unit

interface CameraPermissionState {
    val isGranted: Boolean
    fun launchPermissionRequest()
}

@Composable
expect fun rememberCameraPermissionState(): CameraPermissionState
