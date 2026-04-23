package com.nathan.quailspotter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nathan.quailspotter.domain.QuailDetection
import com.nathan.quailspotter.domain.QuailDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import quailspotter.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class QuailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuailUiState())
    val uiState: StateFlow<QuailUiState> = _uiState.asStateFlow()

    private var detector: QuailDetector? = null

    init {
        viewModelScope.launch {
            try {
                val modelBytes = Res.readBytes("files/openmv_quailproject_v4.tflite")
                val newDetector = QuailDetector(modelBytes)
                newDetector.initialize()
                detector = newDetector
                println("ViewModel: Detector initialized")
            } catch (e: Exception) {
                println("ViewModel: Error initializing detector: ${e.message}")
            }
        }
    }

    fun onImageCaptured(imageBytes: ByteArray) {
        _uiState.value = _uiState.value.copy(
            capturedImage = imageBytes,
            appState = AppState.RESULT,
            detections = emptyList()
        )
        processImage(imageBytes)
    }

    private fun processImage(imageBytes: ByteArray) {
        val currentDetector = detector ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)
            try {
                val results = currentDetector.identify(imageBytes)
                _uiState.value = _uiState.value.copy(
                    detections = results,
                    isProcessing = false
                )
            } catch (e: Exception) {
                println("ViewModel: Error processing image: ${e.message}")
                _uiState.value = _uiState.value.copy(isProcessing = false)
            }
        }
    }

    fun onBack() {
        _uiState.value = _uiState.value.copy(appState = AppState.HOME)
    }

    fun onNavigateToCamera() {
        _uiState.value = _uiState.value.copy(appState = AppState.CAMERA)
    }

    override fun onCleared() {
        super.onCleared()
        detector?.close()
    }
}

enum class AppState {
    HOME, CAMERA, RESULT
}

data class QuailUiState(
    val appState: AppState = AppState.HOME,
    val capturedImage: ByteArray? = null,
    val detections: List<QuailDetection> = emptyList(),
    val isProcessing: Boolean = false
)
