package com.example.data.common.model

import com.example.data.common.database.ExchangeRateEntity
import java.math.BigDecimal

/**
 * @author yaya (@yahyalmh)
 * @since 02th November 2022
 */

data class ExchangeRate(
    val id: String,
    val symbol: String,
    val currencySymbol: String?,
    val type: String,
    val rateUsd: BigDecimal
)

fun ExchangeRate.toEntity() = ExchangeRateEntity(
    id = id,
    symbol = symbol
)
