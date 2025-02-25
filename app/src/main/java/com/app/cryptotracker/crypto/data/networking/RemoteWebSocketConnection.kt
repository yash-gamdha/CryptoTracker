package com.app.cryptotracker.crypto.data.networking

import android.util.Log
import com.app.cryptotracker.crypto.domain.WebSocketConnection
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket

class RemoteWebSocketConnection(
    private val httpClient: HttpClient
): WebSocketConnection {
    override suspend fun connectToWebSocket(
        asset: String,
        onMessage: (String) -> Unit
    ) {
        httpClient.webSocket(
            urlString = "wss://ws.coincap.io/prices?assets=$asset"
        ) {
            try {
                for (message in incoming) {
                    onMessage(String(message.data))
                }
            } catch (e: Exception) {
                Log.e("websocket error remote", "$asset : ${e.message}")
            }
        }
    }

    override suspend fun closeConnection() {
        httpClient.close()
    }
}