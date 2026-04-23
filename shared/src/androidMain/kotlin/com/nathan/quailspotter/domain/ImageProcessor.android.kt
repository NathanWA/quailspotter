package com.nathan.quailspotter.domain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class ImageProcessor {
    actual fun resizeForUint8(imageData: ByteArray, width: Int, height: Int): Any {
        println("ImageProcessor: Decoding byte array of size ${imageData.size}")
        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        if (bitmap == null) {
            println("ImageProcessor: Failed to decode bitmap")
            return ByteArray(0)
        }
        println("ImageProcessor: Bitmap decoded, size ${bitmap.width}x${bitmap.height}. Resizing to ${width}x${height}")
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        
        val byteBuffer = ByteBuffer.allocateDirect(width * height * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(width * height)
        resizedBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        println("ImageProcessor: Converting pixels to RGB bytes in direct ByteBuffer")
        for (pixel in pixels) {
            byteBuffer.put(((pixel shr 16) and 0xFF).toByte())
            byteBuffer.put(((pixel shr 8) and 0xFF).toByte())
            byteBuffer.put((pixel and 0xFF).toByte())
        }
        println("ImageProcessor: Finished processing")
        
        byteBuffer.rewind()
        return byteBuffer
    }
}
