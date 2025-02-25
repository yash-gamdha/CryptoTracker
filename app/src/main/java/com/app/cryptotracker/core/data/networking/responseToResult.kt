package com.app.cryptotracker.core.data.networking

import android.util.Log
import com.app.cryptotracker.core.domain.util.NetworkError
import com.app.cryptotracker.core.domain.util.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json

@Suppress("JSON_FORMAT_REDUNDANT")
suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, NetworkError> {
    return when(response.status.value) {
        in 200..299 -> { // success
            try {
                val responseBody = response.body<ByteArray>()
                val jsonResponse = String(responseBody)
                val responseToReturn = Json {
                    ignoreUnknownKeys = true
                }.decodeFromString<T>(jsonResponse)
                Result.Success(responseToReturn)
            } catch (e: NoTransformationFoundException) { // failed to convert in JSON
                Log.d("Serialization", "${e.message.toString()}\n${T::class.simpleName}")
                Result.Error(NetworkError.SERIALIZATION)
            }
        }
        408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
        429 -> Result.Error(NetworkError.TOO_MANY_REQUESTS)
        in 500..599 -> { // server error
            Result.Error(NetworkError.SERVER_ERROR)
        }
        else -> Result.Error(NetworkError.UNKNOWN)
    }
}