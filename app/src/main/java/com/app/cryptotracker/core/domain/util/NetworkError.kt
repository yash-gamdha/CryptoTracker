package com.app.cryptotracker.core.domain.util

enum class NetworkError: Error {
    REQUEST_TIMEOUT,
    SERIALIZATION,
    TOO_MANY_REQUESTS,
    NO_INTERNET,
    SERVER_ERROR,
    UNKNOWN
}