package com.app.cryptotracker.crypto.domain

interface WebSocketConnection {
    suspend fun connectToWebSocket(asset: String, onMessage: (String) -> Unit)
    suspend fun closeConnection()
}