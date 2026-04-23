package com.nathan.quailspotter.domain

data class Rect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

data class QuailDetection(
    val boundingBox: Rect,
    val sex: QuailSex,
    val confidence: Float
)
