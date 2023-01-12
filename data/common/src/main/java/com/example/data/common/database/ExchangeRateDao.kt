package com.example.data.common.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM favorite_rates order by symbol ASC")
    fun getAll(): Flow<List<ExchangeRateEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(rates: List<ExchangeRateEntity>)

    @Insert(onConflict = REPLACE)
    suspend fun insert(rate: ExchangeRateEntity)

    @Delete
    suspend fun delete(rate: ExchangeRateEntity)
}