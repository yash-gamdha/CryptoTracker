package com.app.cryptotracker.core.data.networking

import com.app.cryptotracker.BuildConfig
import io.ktor.http.URLBuilder

fun constructURL(url: String): String {
    return when {
        url.contains(BuildConfig.BASE_URL) -> url
        url.startsWith("/") -> BuildConfig.BASE_URL + url.drop(1)
        else -> BuildConfig.BASE_URL + url
    }
}

fun constructWebSocketURL(asset: String): String {
    return URLBuilder(BuildConfig.WEBSOCKET_URL).apply {
        parameters.append("assets", asset)
    }.buildString()
}