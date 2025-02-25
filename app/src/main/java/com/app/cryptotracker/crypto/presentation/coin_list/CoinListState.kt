package com.app.cryptotracker.crypto.presentation.coin_list

import androidx.compose.runtime.Immutable
import com.app.cryptotracker.crypto.presentation.models.CoinUI
import com.app.cryptotracker.crypto.presentation.models.DisplayableNumber
import com.app.cryptotracker.crypto.presentation.models.toDisplayableNumber

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUI>? = null,
    val searchResult: List<CoinUI>? = null,
    val selectedCoin: CoinUI? = null,
    val selectedCoinPrice: DisplayableNumber = 0.0.toDisplayableNumber()
)