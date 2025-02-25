package com.app.cryptotracker.crypto.data.networking

import android.util.Log
import com.app.cryptotracker.core.data.networking.constructURL
import com.app.cryptotracker.core.data.networking.constructWebSocketURL
import com.app.cryptotracker.core.data.networking.makeRequest
import com.app.cryptotracker.core.domain.util.NetworkError
import com.app.cryptotracker.core.domain.util.Result
import com.app.cryptotracker.core.domain.util.map
import com.app.cryptotracker.crypto.data.mappers.toCoin
import com.app.cryptotracker.crypto.data.mappers.toCoinPrice
import com.app.cryptotracker.crypto.data.networking.dtos.CoinHistoryDto
import com.app.cryptotracker.crypto.data.networking.dtos.CoinResponseDto
import com.app.cryptotracker.crypto.domain.Coin
import com.app.cryptotracker.crypto.domain.CoinDataSource
import com.app.cryptotracker.crypto.domain.CoinPrice
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.websocket.close
import java.time.ZoneId
import java.time.ZonedDateTime

class RemoteCoinDataSource(
    private val httpClient: HttpClient
): CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return makeRequest<CoinResponseDto> {
            httpClient.get(
                urlString = constructURL("/assets")
            ) {
                header("Accept","application/json")
            }
        }.map { response ->
            response.data.map { it.toCoin() }
        }
    }

    override suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> {
        val startMillis = start
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        val endMillis = end
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        return makeRequest<CoinHistoryDto> {
            httpClient.get(
                urlString = constructURL("/assets/$coinId/history")
            ) {
                parameter("interval","h6")
                parameter("start", startMillis)
                parameter("end", endMillis)
            }
        }.map { response ->
            response.data.map { it.toCoinPrice() }
        }
    }

    override suspend fun closeConnection() {
        httpClient.close()
    }
}