package com.app.cryptotracker.crypto.presentation.coin_list

import com.app.cryptotracker.core.domain.util.NetworkError

sealed interface CoinListEvent {
    data class Error(val error: NetworkError): CoinListEvent
}