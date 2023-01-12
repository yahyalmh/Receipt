package com.example.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface DatastoreRepository {
    fun <T> getValue(key: Preferences.Key<T>): Flow<T?>
    suspend fun <T> setValue(key: Preferences.Key<T>, value: T): Preferences
}

class DatastoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DatastoreRepository {

    override fun <T> getValue(key: Preferences.Key<T>): Flow<T?> = dataStore.data
        .map { preferences -> preferences[key] }

    override suspend fun <T> setValue(key: Preferences.Key<T>, value: T) =
        dataStore.edit { preferences -> preferences[key] = value }
}