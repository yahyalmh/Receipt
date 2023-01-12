package com.example.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {

    const val PREFERENCES_FILE_NAME = "app_preferences_file"
    val THEME_TYPE_KEY = stringPreferencesKey("theme_type")

}