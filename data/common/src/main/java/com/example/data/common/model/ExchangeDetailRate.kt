package com.example.data.common.model

import java.math.BigDecimal

data class ExchangeDetailRate(
    val id: String,
    val symbol: String,
    val currencySymbol: String?,
    val type: String,
    val rateUsd: BigDecimal,
    val timestamp: Long
)

fun ExchangeDetailRate.toExchangeRate() = ExchangeRate(
    id = id,
    symbol = symbol,
    currencySymbol = currencySymbol,
    type = type,
    rateUsd = rateUsd
)