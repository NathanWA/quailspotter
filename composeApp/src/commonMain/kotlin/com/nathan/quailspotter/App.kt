package com.nathan.quailspotter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nathan.quailspotter.domain.QuailAnalysisResult
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
                        onLaunchGallery = { galleryLauncher() },
                        onAiDeepScan = { viewModel.onAnalyzeWithAi() }
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
    onLaunchGallery: () -> Unit,
    onAiDeepScan: () -> Unit
) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

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
            val currentImage = imageBitmap
            if (currentImage != null) {
                val aiResult = uiState.aiAnalysisResult
                if (aiResult != null) {
                    LabelledQuailImage(
                        bitmap = currentImage,
                        result = aiResult,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    QuailImageBase(
                        bitmap = currentImage,
                        modifier = Modifier.fillMaxSize()
                    ) { dstWidth, dstHeight, offsetX, offsetY ->
                        uiState.detections.forEach { detection ->
                            val rect = detection.boundingBox
                            val left = offsetX + (rect.left * dstWidth / 224f)
                            val top = offsetY + (rect.top * dstHeight / 224f)
                            val right = offsetX + (rect.right * dstWidth / 224f)
                            val bottom = offsetY + (rect.bottom * dstHeight / 224f)

                            drawRect(
                                color = if (detection.sex == com.nathan.quailspotter.domain.QuailSex.MALE) Color.Blue else Color.Magenta,
                                topLeft = Offset(left, top),
                                size = Size(right - left, bottom - top),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Capture or select a quail image", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (uiState.isProcessing || uiState.isAiProcessing) {
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

        // Bottom Half: AI Analysis Trigger and Results
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onAiDeepScan,
                enabled = uiState.capturedImage != null && !uiState.isProcessing && !uiState.isAiProcessing,
                modifier = Modifier.fillMaxWidth().testTag("ai_deep_scan_button"),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isAiProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyzing Quail...")
                } else {
                    Text("Perform AI Deep Scan")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState.aiError != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("ai_error_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = uiState.aiError,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                val aiResult = uiState.aiAnalysisResult
                if (aiResult != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("ai_result_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "AI Deep Scan Result:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            aiResult.birds.forEach { bird ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        "Quail #${bird.id}: ${bird.symbol} ${bird.sex}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Genetics: ${bird.genetics}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Confidence: ${bird.confidence}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "Reasoning: ${bird.reasoning}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                                if (bird != aiResult.birds.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (uiState.detections.isNotEmpty()) {
                    Text(
                        "TFLite Detection Summary:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    uiState.detections.forEach { detection ->
                        Text(
                            "• ${detection.sex}: ${(detection.confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LabelledQuailImage(
    bitmap: ImageBitmap,
    result: QuailAnalysisResult,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    QuailImageBase(bitmap = bitmap, modifier = modifier) { dstWidth, dstHeight, offsetX, offsetY ->
        result.birds.forEach { bird ->
            val color = when (bird.sex.lowercase()) {
                "male" -> Color.Blue
                "female" -> Color.Magenta
                else -> Color.Gray
            }

            // OpenAI boxes are normalized 0.0–1.0
            val left = offsetX + bird.box.x * dstWidth
            val top = offsetY + bird.box.y * dstHeight
            val width = bird.box.width * dstWidth
            val height = bird.box.height * dstHeight

            drawRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(width, height),
                style = Stroke(width = 2.dp.toPx())
            )

            val textLayoutResult = textMeasurer.measure(
                text = "${bird.id} ${bird.symbol} ${bird.genetics}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            val textTop = (top - textLayoutResult.size.height).coerceAtLeast(offsetY)

            drawRect(
                color = color.copy(alpha = 0.8f),
                topLeft = Offset(left, textTop),
                size = Size(
                    textLayoutResult.size.width.toFloat() + 4.dp.toPx(),
                    textLayoutResult.size.height.toFloat()
                )
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(left + 2.dp.toPx(), textTop)
            )
        }
    }
}

@Composable
fun QuailImageBase(
    bitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    overlay: DrawScope.(dstWidth: Float, dstHeight: Float, offsetX: Float, offsetY: Float) -> Unit
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
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

        overlay(dstWidth, dstHeight, offsetX, offsetY)
    }
}
