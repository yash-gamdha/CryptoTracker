package com.app.cryptotracker.core.presentation.util

import android.content.Context
import com.app.cryptotracker.R
import com.app.cryptotracker.core.domain.util.NetworkError

fun NetworkError.toString(context: Context): String {
    val resId = when(this) {
        NetworkError.REQUEST_TIMEOUT -> R.string.error_request_timeout
        NetworkError.SERIALIZATION -> R.string.error_serialization
        NetworkError.TOO_MANY_REQUESTS -> R.string.error_too_many_requests
        NetworkError.NO_INTERNET -> R.string.error_no_internet
        NetworkError.SERVER_ERROR -> R.string.error_unknown
        NetworkError.UNKNOWN -> R.string.error_unknown
    }

    return context.getString(resId)
}