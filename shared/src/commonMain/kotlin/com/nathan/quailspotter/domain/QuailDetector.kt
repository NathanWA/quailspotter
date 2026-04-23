package com.nathan.quailspotter.domain

import org.kmp.playground.kflite.Kflite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuailDetector(private val modelBytes: ByteArray) : QuailClassifier {

    private val imageProcessor = ImageProcessor()

    override suspend fun initialize() {
        println("QuailDetector: Initializing with model size: ${modelBytes.size}")
        try {
            Kflite.init(modelBytes)
            println("QuailDetector: Kflite.init successful")
        } catch (t: Throwable) {
            println("QuailDetector: Kflite.init failed or already initialized: ${t.message}")
        }
    }

    override suspend fun identify(imageData: ByteArray): List<QuailDetection> = withContext(Dispatchers.Default) {
        println("QuailDetector: Starting identification, image size: ${imageData.size}")
        
        val input: Any
        try {
            // Input: 1 x 224 x 224 x 3 (uint8)
            input = imageProcessor.resizeForUint8(imageData, 224, 224)
            println("QuailDetector: Image resized")
        } catch (t: Throwable) {
            println("QuailDetector: Resize failed: ${t.message}")
            t.printStackTrace()
            return@withContext emptyList()
        }
        
        // Output: 1 x 7 x 1029 (float32)
        val output = Array(1) { Array(7) { FloatArray(1029) } }
        
        println("QuailDetector: Running Kflite.run")
        try {
            Kflite.run(
                inputs = listOf(input),
                outputs = mapOf(0 to output)
            )
            println("QuailDetector: Kflite run completed successfully")
        } catch (t: Throwable) {
            println("QuailDetector: Kflite run encountered a throwable: ${t.message}")
            t.printStackTrace()
            return@withContext emptyList()
        }

        println("QuailDetector: Parsing output")
        val results = parseOutput(output)
        println("QuailDetector: Found ${results.size} detections")
        results
    }

    private fun parseOutput(output: Array<Array<FloatArray>>): List<QuailDetection> {
        val detections = mutableListOf<QuailDetection>()
        val numBoxes = 1029
        val data = output[0] // shape [7][1029]

        for (i in 0 until numBoxes) {
            // The model output is [1, 7, 1029]
            // Indices: 0:cx, 1:cy, 2:w, 3:h, 4:male, 5:female, 6:other
            // LOGS SHOW NORMALIZED COORDINATES (0.0 to 1.0)
            val cx = data[0][i] * 224f
            val cy = data[1][i] * 224f
            val w = data[2][i] * 224f
            val h = data[3][i] * 224f
            
            val maleScore = data[4][i]
            val femaleScore = data[5][i]
            val otherScore = data[6][i]

            val maxScore = maxOf(maleScore, femaleScore, otherScore)
            
            if (maxScore > 0.3f) {
                // Log high-confidence detections for debugging
                if (maxScore > 0.5f) {
                    println("QuailDetector: Candidate: score=$maxScore, box=[cx=${cx.toInt()}, cy=${cy.toInt()}, w=${w.toInt()}, h=${h.toInt()}]")
                }

                val sex = when {
                    maleScore == maxScore -> QuailSex.MALE
                    femaleScore == maxScore -> QuailSex.FEMALE
                    else -> QuailSex.UNDETERMINED
                }

                detections.add(
                    QuailDetection(
                        boundingBox = Rect(
                            left = (cx - w / 2),
                            top = (cy - h / 2),
                            right = (cx + w / 2),
                            bottom = (cy + h / 2)
                        ),
                        sex = sex,
                        confidence = maxScore
                    )
                )
            }
        }

        return nms(detections)
    }

    private fun nms(detections: List<QuailDetection>): List<QuailDetection> {
        if (detections.isEmpty()) return emptyList()
        
        val sorted = detections.sortedByDescending { it.confidence }.toMutableList()
        val selected = mutableListOf<QuailDetection>()
        
        while (sorted.isNotEmpty()) {
            val first = sorted.removeAt(0)
            selected.add(first)
            
            sorted.removeAll { other ->
                calculateIoU(first.boundingBox, other.boundingBox) > 0.5f
            }
        }
        
        return selected.take(10)
    }

    private fun calculateIoU(a: Rect, b: Rect): Float {
        val intersectionLeft = maxOf(a.left, b.left)
        val intersectionTop = maxOf(a.top, b.top)
        val intersectionRight = minOf(a.right, b.right)
        val intersectionBottom = minOf(a.bottom, b.bottom)
        
        val intersectionArea = maxOf(0f, intersectionRight - intersectionLeft) * 
                               maxOf(0f, intersectionBottom - intersectionTop)
        
        val areaA = (a.right - a.left) * (a.bottom - a.top)
        val areaB = (b.right - b.left) * (b.bottom - b.top)
        
        return intersectionArea / (areaA + areaB - intersectionArea)
    }

    override fun close() {
        Kflite.close()
    }
}
