package com.app.cryptotracker.crypto.presentation.coin_list

import com.app.cryptotracker.crypto.presentation.models.CoinUI

sealed interface CoinListActions {
    data class OnCoinClicked(val coinUI: CoinUI): CoinListActions
}