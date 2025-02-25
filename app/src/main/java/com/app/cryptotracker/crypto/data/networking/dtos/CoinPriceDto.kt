package com.app.cryptotracker.crypto.data.networking.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceDto (
    val priceUsd: Double,
    val time: Long
)