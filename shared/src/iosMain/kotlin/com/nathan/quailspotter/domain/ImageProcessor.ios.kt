package com.nathan.quailspotter.domain

import platform.UIKit.*
import platform.CoreGraphics.*
import platform.Foundation.*
import kotlinx.cinterop.*

actual class ImageProcessor {
    @OptIn(ExperimentalForeignApi::class)
    actual fun resizeForUint8(imageData: ByteArray, width: Int, height: Int): Any {
        val nsData = imageData.toNSData()
        val uiImage = UIImage.imageWithData(nsData) ?: return ByteArray(width * height * 3)
        
        val colorSpace = CGColorSpaceCreateDeviceRGB()
        val bytesPerPixel = 4
        val bytesPerRow = width * bytesPerPixel
        
        val context = CGBitmapContextCreate(
            data = null,
            width = width.toULong(),
            height = height.toULong(),
            bitsPerComponent = 8u,
            bytesPerRow = bytesPerRow.toULong(),
            space = colorSpace,
            bitmapInfo = CGImageAlphaInfo.kCGImageAlphaNoneSkipLast.value
        ) ?: return ByteArray(width * height * 3)

        UIGraphicsPushContext(context)
        uiImage.drawInRect(CGRectMake(0.0, 0.0, width.toDouble(), height.toDouble()))
        UIGraphicsPopContext()

        val rawData = CGBitmapContextGetData(context) ?: run {
            CGContextRelease(context)
            CGColorSpaceRelease(colorSpace)
            return ByteArray(width * height * 3)
        }
        
        val bytePtr = rawData.reinterpret<ByteVar>()
        val rgbByteArray = ByteArray(width * height * 3)

        for (i in 0 until width * height) {
            rgbByteArray[i * 3 + 0] = bytePtr[i * 4 + 0] // R
            rgbByteArray[i * 3 + 1] = bytePtr[i * 4 + 1] // G
            rgbByteArray[i * 3 + 2] = bytePtr[i * 4 + 2] // B
        }

        CGContextRelease(context)
        CGColorSpaceRelease(colorSpace)

        return rgbByteArray
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun ByteArray.toNSData(): NSData = usePinned {
        NSData.dataWithBytes(it.addressOf(0), this.size.toULong())
    }
}
