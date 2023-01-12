package com.example.data.common.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.common.database.DbConfig.FAVORITE_TABLE_NAME

@Entity(tableName = FAVORITE_TABLE_NAME)
data class ExchangeRateEntity(
    @PrimaryKey val id: String,
    val symbol: String,
)