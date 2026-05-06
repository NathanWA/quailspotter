package com.nathan.quailspotter.domain

import kotlinx.serialization.Serializable

@Serializable
data class Rect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

@Serializable
data class QuailDetection(
    val boundingBox: Rect,
    val sex: QuailSex,
    val confidence: Float
)

@Serializable
data class QuailAnalysisResult(
    val birds: List<QuailBird>
)

@Serializable
data class QuailBird(
    val id: Int,
    val sex: String,          // "male", "female", "uncertain"
    val symbol: String,       // "♂", "♀", "?"
    val genetics: String,
    val confidence: String,
    val reasoning: String,
    val box: BoundingBox      // normalized 0.0–1.0
)

@Serializable
data class BoundingBox(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)