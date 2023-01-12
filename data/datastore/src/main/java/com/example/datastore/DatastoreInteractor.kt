package com.example.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DatastoreInteractor {
    fun getThemeType(): Flow<String?>
    suspend fun setThemeType(themeType: String): Preferences
}

class DatastoreInteractorImpl @Inject constructor(
    private val repository: DatastoreRepository
) :
    DatastoreInteractor {

    override fun getThemeType() = repository.getValue(PreferencesKeys.THEME_TYPE_KEY)

    override suspend fun setThemeType(themeType: String) =
        repository.setValue(PreferencesKeys.THEME_TYPE_KEY, themeType)

}