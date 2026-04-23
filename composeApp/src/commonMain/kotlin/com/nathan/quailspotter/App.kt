package com.nathan.quailspotter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nathan.quailspotter.domain.QuailDetection
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun App(viewModel: QuailViewModel = viewModel { QuailViewModel() }) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberCameraPermissionState()

    MaterialTheme {
        val galleryLauncher = rememberGalleryLauncher(
            onResult = { byteArrays ->
                viewModel.onImageCaptured(byteArrays)
            }
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("QuailSpotter") }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (uiState.appState == AppState.CAMERA) {
                    CameraScreen(
                        onCapture = { 
                            viewModel.onImageCaptured(it)
                        }
                    )
                } else {
                    MainContent(
                        uiState = uiState,
                        onLaunchCamera = {
                            if (cameraPermissionState.isGranted) {
                                viewModel.onNavigateToCamera()
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        },
                        onLaunchGallery = { galleryLauncher() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainContent(
    uiState: QuailUiState,
    onLaunchCamera: () -> Unit,
    onLaunchGallery: () -> Unit
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var noteText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.capturedImage) {
        if (uiState.capturedImage != null) {
            imageBitmap = uiState.capturedImage.decodeToImageBitmap()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Half: Image and Buttons
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.2f))
        ) {
            if (imageBitmap != null) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val bitmap = imageBitmap!!
                    val imageWidth = bitmap.width.toFloat()
                    val imageHeight = bitmap.height.toFloat()

                    val scale = minOf(canvasWidth / imageWidth, canvasHeight / imageHeight)
                    val dstWidth = imageWidth * scale
                    val dstHeight = imageHeight * scale
                    val offsetX = (canvasWidth - dstWidth) / 2
                    val offsetY = (canvasHeight - dstHeight) / 2

                    drawImage(
                        image = bitmap,
                        dstOffset = androidx.compose.ui.unit.IntOffset(offsetX.toInt(), offsetY.toInt()),
                        dstSize = androidx.compose.ui.unit.IntSize(dstWidth.toInt(), dstHeight.toInt())
                    )

                    uiState.detections.forEach { detection ->
                        val rect = detection.boundingBox
                        val left = offsetX + (rect.left * dstWidth / 224f)
                        val top = offsetY + (rect.top * dstHeight / 224f)
                        val right = offsetX + (rect.right * dstWidth / 224f)
                        val bottom = offsetY + (rect.bottom * dstHeight / 224f)

                        drawRect(
                            color = if (detection.sex == com.nathan.quailspotter.domain.QuailSex.MALE) Color.Blue else Color.Magenta,
                            topLeft = androidx.compose.ui.geometry.Offset(left, top),
                            size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Capture or select a quail image", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (uiState.isProcessing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Small Floating Buttons using Emojis as symbols
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = onLaunchCamera,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text("📷")
                }
                SmallFloatingActionButton(
                    onClick = onLaunchGallery,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text("🖼️")
                }
            }
        }

        // Bottom Half: Textbox and Blank Space
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Detection Notes") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Type here...") }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (uiState.detections.isNotEmpty()) {
                Text(
                    "Detected: ${uiState.detections.size} quails",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.align(Alignment.Start)
                )
                uiState.detections.forEach { detection ->
                    Text(
                        "• ${detection.sex}: ${(detection.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            }
        }
    }
}
