package com.nathan.quailspotter

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.*
import com.nathan.quailspotter.domain.BoundingBox
import com.nathan.quailspotter.domain.QuailAnalysisResult
import com.nathan.quailspotter.domain.QuailBird
import com.nathan.quailspotter.domain.QuailDetection
import com.nathan.quailspotter.domain.QuailSex
import com.nathan.quailspotter.domain.Rect
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class QuailUiTest {

    @Test
    fun testAiButtonDisabledWhenNoImage() = runComposeUiTest {
        setContent {
            MaterialTheme {
                MainContent(
                    uiState = QuailUiState(capturedImage = null),
                    onLaunchCamera = {},
                    onLaunchGallery = {},
                    onAiDeepScan = {}
                )
            }
        }

        onNodeWithTag("ai_deep_scan_button").assertIsNotEnabled()
    }

    @Test
    fun testAiButtonEnabledEvenWhenNoDetectionsIfImagePresent() = runComposeUiTest {
        setContent {
            MaterialTheme {
                MainContent(
                    uiState = QuailUiState(
                        capturedImage = byteArrayOf(1, 2, 3),
                        detections = emptyList(),
                        isProcessing = false
                    ),
                    onLaunchCamera = {},
                    onLaunchGallery = {},
                    onAiDeepScan = {}
                )
            }
        }

        onNodeWithTag("ai_deep_scan_button").assertIsEnabled()
    }

    @Test
    fun testAiButtonEnabledWhenDetectionsPresent() = runComposeUiTest {
        setContent {
            MaterialTheme {
                MainContent(
                    uiState = QuailUiState(
                        capturedImage = byteArrayOf(1, 2, 3),
                        detections = listOf(
                            QuailDetection(Rect(0f, 0f, 10f, 10f), QuailSex.MALE, 0.9f)
                        ),
                        isProcessing = false
                    ),
                    onLaunchCamera = {},
                    onLaunchGallery = {},
                    onAiDeepScan = {}
                )
            }
        }

        onNodeWithTag("ai_deep_scan_button").assertIsEnabled()
    }

    @Test
    fun testAiButtonDisabledWhileProcessing() = runComposeUiTest {
        setContent {
            MaterialTheme {
                MainContent(
                    uiState = QuailUiState(
                        capturedImage = byteArrayOf(1, 2, 3),
                        detections = listOf(
                            QuailDetection(Rect(0f, 0f, 10f, 10f), QuailSex.MALE, 0.9f)
                        ),
                        isProcessing = true
                    ),
                    onLaunchCamera = {},
                    onLaunchGallery = {},
                    onAiDeepScan = {}
                )
            }
        }

        onNodeWithTag("ai_deep_scan_button").assertIsNotEnabled()
    }

    @Test
    fun testInformationAppearsAfterAiScan() = runComposeUiTest {
        var state by mutableStateOf(
            QuailUiState(
                capturedImage = byteArrayOf(1, 2, 3),
                detections = listOf(
                    QuailDetection(Rect(0f, 0f, 10f, 10f), QuailSex.MALE, 0.9f)
                )
            )
        )

        setContent {
            MaterialTheme {
                MainContent(
                    uiState = state,
                    onLaunchCamera = {},
                    onLaunchGallery = {},
                    onAiDeepScan = {
                        state = state.copy(isAiProcessing = true)
                    }
                )
            }
        }

        // Verify button is enabled and click it
        onNodeWithTag("ai_deep_scan_button").assertIsEnabled().performClick()

        // Verify it enters processing state
        onNodeWithTag("ai_deep_scan_button").assertIsNotEnabled()

        // Simulate AI result returning (within the 60s window)
        state = state.copy(
            isAiProcessing = false,
            aiAnalysisResult = QuailAnalysisResult(
                birds = listOf(
                    QuailBird(
                        id = 1,
                        sex = "male",
                        symbol = "♂",
                        genetics = "Pharaoh",
                        confidence = "95%",
                        reasoning = "Clear chest markings",
                        box = BoundingBox(0.1f, 0.1f, 0.2f, 0.2f)
                    )
                )
            )
        )

        // Verify information appears within 60 seconds
        waitUntil(timeoutMillis = 60000) {
            onAllNodesWithTag("ai_result_card").fetchSemanticsNodes().isNotEmpty()
        }
        
        onNodeWithTag("ai_result_card").assertExists()
    }

    @Test
    fun testAiErrorShowsErrorMessage() = runComposeUiTest {
        setContent {
            MaterialTheme {
                MainContent(
                    uiState = QuailUiState(
                        capturedImage = byteArrayOf(1, 2, 3),
                        detections = listOf(
                            QuailDetection(Rect(0f, 0f, 10f, 10f), QuailSex.MALE, 0.9f)
                        ),
                        aiError = "API Limit Reached"
                    ),
                    onLaunchCamera = {},
                    onLaunchGallery = {},
                    onAiDeepScan = {}
                )
            }
        }

        onNodeWithTag("ai_error_card").assertIsDisplayed()
        onNodeWithText("API Limit Reached").assertExists()
    }
}
