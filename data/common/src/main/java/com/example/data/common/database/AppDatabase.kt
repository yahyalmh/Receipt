package com.example.data.common.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [ExchangeRateEntity::class])
abstract class AppDatabase: RoomDatabase() {

    abstract fun exchangeRateDao(): ExchangeRateDao
}