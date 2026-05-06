package com.nathan.quailspotter

import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()
actual fun encodeBase64(bytes: ByteArray): String {
    NSData.create(bytes = bytes, length = bytes.size.toULong())
        .base64EncodedStringWithOptions(0)
}