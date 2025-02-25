package com.app.cryptotracker.di

import com.app.cryptotracker.core.data.networking.HttpClientFactory
import com.app.cryptotracker.crypto.data.networking.RemoteCoinDataSource
import com.app.cryptotracker.crypto.data.networking.RemoteWebSocketConnection
import com.app.cryptotracker.crypto.domain.CoinDataSource
import com.app.cryptotracker.crypto.domain.WebSocketConnection
import com.app.cryptotracker.crypto.presentation.coin_list.CoinListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single(qualifier = named("normalEngine")) { HttpClientFactory.create(CIO.create()) }
    single(qualifier = named("webSocketEngine")) { HttpClientFactory.createForWebSocketCalls(CIO.create()) }

    factory<CoinDataSource> {
        RemoteCoinDataSource(get(qualifier = named("normalEngine")))
    }

    factory<WebSocketConnection> {
        RemoteWebSocketConnection(get(qualifier = named("webSocketEngine")))
    }

    viewModelOf(::CoinListViewModel)
}