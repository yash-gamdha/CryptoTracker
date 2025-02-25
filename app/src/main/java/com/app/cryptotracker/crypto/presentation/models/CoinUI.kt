package com.app.cryptotracker.crypto.presentation.models

import android.icu.text.NumberFormat
import androidx.annotation.DrawableRes
import com.app.cryptotracker.crypto.domain.Coin
import com.app.cryptotracker.core.presentation.util.getDrawableIdForCoin
import com.app.cryptotracker.crypto.presentation.coin_detail.components.DataPoint
import java.util.Locale

data class CoinUI(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd : DisplayableNumber,
    val priceUsd: DisplayableNumber,
    val changePercent24Hr: DisplayableNumber,
    @DrawableRes val iconRes: Int,
    val coinPriceHistory: List<DataPoint> = emptyList()
) {
    fun matchSearchQuery(query: String): Boolean {
        return name.contains(query, ignoreCase = true)
                || symbol.contains(query, ignoreCase = true)
    }
}

data class DisplayableNumber(
    val value: Double,
    val formatted: String
)

fun Coin.toCoinUI(): CoinUI {
    return CoinUI(
        rank = rank,
        id = id,
        name = name,
        symbol = symbol,
        marketCapUsd = marketCapUsd.toDisplayableNumber(),
        priceUsd = priceUsd.toDisplayableNumber(),
        changePercent24Hr = changePercent24Hr.toDisplayableNumber(),
        iconRes = getDrawableIdForCoin(symbol)
    )
}

fun Double.toDisplayableNumber(): DisplayableNumber {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    return DisplayableNumber(
        value = this,
        formatted = formatter.format(this)
    )
}