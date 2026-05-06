package com.nathan.quailspotter.domain

import kotlinx.serialization.Serializable

@Serializable
enum class QuailSex(val displayName: String, val symbol: String) {
    MALE("Male", "♂"),
    FEMALE("Female", "♀"),
    UNDETERMINED("Undetermined", "?")
}
