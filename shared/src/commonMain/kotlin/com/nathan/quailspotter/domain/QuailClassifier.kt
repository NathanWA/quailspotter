package com.nathan.quailspotter.domain

import kotlinx.coroutines.flow.Flow

interface QuailClassifier {
    /**
     * Initializes the model. Should be called before identify.
     */
    suspend fun initialize()

    /**
     * Identifies quails in the given image data.
     * @param imageData The raw image bytes (e.g., from a JPEG or PNG).
     * @return A list of detected quails with their sex and bounding boxes.
     */
    suspend fun identify(imageData: ByteArray): List<QuailDetection>
    
    /**
     * Closes the model and releases resources.
     */
    fun close()
}
