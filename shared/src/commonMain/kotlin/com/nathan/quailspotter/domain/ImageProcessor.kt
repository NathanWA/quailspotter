package com.nathan.quailspotter.domain

expect class ImageProcessor() {
    fun resizeForUint8(imageData: ByteArray, width: Int, height: Int): Any
}
