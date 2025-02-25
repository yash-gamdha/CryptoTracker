package com.app.cryptotracker.crypto.data.networking.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CoinDto(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd : Double,
    val priceUsd: Double,
    val changePercent24Hr: Double
)