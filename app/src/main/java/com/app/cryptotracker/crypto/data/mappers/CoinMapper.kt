package com.app.cryptotracker.crypto.data.mappers

import com.app.cryptotracker.crypto.data.networking.dtos.CoinDto
import com.app.cryptotracker.crypto.data.networking.dtos.CoinPriceDto
import com.app.cryptotracker.crypto.domain.Coin
import com.app.cryptotracker.crypto.domain.CoinPrice
import java.time.Instant
import java.time.ZoneId

fun CoinDto.toCoin(): Coin {
    return Coin(
        id = id,
        rank = rank,
        name = name,
        symbol = symbol,
        marketCapUsd = marketCapUsd,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24Hr
    )
}

fun CoinPriceDto.toCoinPrice(): CoinPrice {
    return CoinPrice(
        priceUsd = priceUsd,
        time = Instant
            .ofEpochMilli(time)
            .atZone(ZoneId.systemDefault())
    )
}