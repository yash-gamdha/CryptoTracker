package com.app.cryptotracker.crypto.data.networking.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CoinResponseDto(
    val data: List<CoinDto>
)