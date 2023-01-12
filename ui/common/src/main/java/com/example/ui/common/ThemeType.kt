package com.example.ui.common

enum class ThemeType {
    LIGHT, DARK, SYSTEM
}

fun String.toThemeType(): ThemeType {
    val themeType = ThemeType.values().firstOrNull { this == it.name }
    return themeType ?: throw UnknownThemeType("$this is unknown theme type")
}

class UnknownThemeType(override val message: String): Exception()