package com.nathan.quailspotter

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform