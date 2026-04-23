package com.nathan.quailspotter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.ui.camera.PeekabooCamera

@Composable
actual fun CameraScreen(onCapture: (ByteArray) -> Unit) {
    PeekabooCamera(
        onCapture = { bytes ->
            bytes?.let { onCapture(it) }
        },
        modifier = Modifier.fillMaxSize(),
        captureIcon = { onClick ->
            Button(onClick = onClick) {
                Text("Capture")
            }
        }
    )
}

@Composable
actual fun rememberGalleryLauncher(onResult: (ByteArray) -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    val launcher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { images: List<ByteArray> ->
            images.firstOrNull()?.let {
                onResult(it)
            }
        }
    )
    return { launcher.launch() }
}

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    return object : CameraPermissionState {
        override val isGranted: Boolean get() = true // Peekaboo handles iOS permissions
        override fun launchPermissionRequest() {}
    }
}
