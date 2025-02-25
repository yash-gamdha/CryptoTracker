package com.app.cryptotracker.crypto.presentation.coin_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.cryptotracker.core.domain.util.onError
import com.app.cryptotracker.core.domain.util.onSuccess
import com.app.cryptotracker.crypto.domain.CoinDataSource
import com.app.cryptotracker.crypto.domain.WebSocketConnection
import com.app.cryptotracker.crypto.presentation.coin_detail.components.DataPoint
import com.app.cryptotracker.crypto.presentation.models.CoinUI
import com.app.cryptotracker.crypto.presentation.models.toCoinUI
import com.app.cryptotracker.crypto.presentation.models.toDisplayableNumber
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(FlowPreview::class)
class CoinListViewModel(
    private val coinDataSource: CoinDataSource,
    private val webSocketConnection: WebSocketConnection
) : ViewModel() {
    private val _state = MutableStateFlow(CoinListState())

    val state = _state
        .onStart {
            loadCoins()
//            webSocketObserver()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            CoinListState()
        )

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    private var selectedCoinWebSocketJob: Job? = null
    private var historyJob: Job? = null

    private val _coins = MutableStateFlow(_state.value.coins)
    val coins = searchText
        .debounce(700L)
        .onEach { _isSearching.update { true } }
        .combine(_coins) { searchText, coins ->
            delay(1000L)
            if (searchText.isBlank()) {
                coins
            } else {
                coins?.filter {
                    it.matchSearchQuery(searchText)
                }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _coins.value
        )

    fun onAction(actions: CoinListActions) {
        when (actions) {
            is CoinListActions.OnCoinClicked -> {
                historyJob?.cancel()
                getHistoryForSelectedCoin(actions.coinUI)
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun getHistoryForSelectedCoin(coinUi: CoinUI) {
        _state.update { it.copy(selectedCoin = coinUi, selectedCoinPrice = coinUi.priceUsd) }
        selectedCoinWebSocketJob?.cancel()
        selectedCoinWebSocketCall(coinUi)

        historyJob = viewModelScope.launch {
            coinDataSource
                .getCoinHistory(
                    coinId = coinUi.id,
                    start = ZonedDateTime.now().minusDays(5),
                    end = ZonedDateTime.now()
                )
                .onSuccess { history ->
                    val dataPoints = history
                        .sortedBy { it.time }
                        .map {
                            DataPoint(
                                x = it.time.hour.toFloat(),
                                y = it.priceUsd.toFloat(),
                                xLabel = DateTimeFormatter
                                    .ofPattern("ha\nM/d")
                                    .format(it.time)
                            )
                        }

                    _state.update {
                        it.copy(
                            selectedCoin = it.selectedCoin?.copy(
                                coinPriceHistory = dataPoints
                            )
                        )
                    }
                }
                .onError { error ->
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            coins = coins.map { coin -> coin.toCoinUI() }
                        )
                    }
                    _coins.value = _state.value.coins
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    fun refresh() {
        _isRefreshing.update { true }
        loadCoins()
        _isRefreshing.update { false }
    }

    fun closeConnections() {
        viewModelScope.launch {
            coinDataSource.closeConnection()
            webSocketConnection.closeConnection()
            Log.d("connection", "closed")
        }
    }

    private fun selectedCoinWebSocketCall(coinUI: CoinUI) {
        selectedCoinWebSocketJob = viewModelScope.launch {
            webSocketConnection.connectToWebSocket(
                asset = coinUI.id
            ) { currentPrice ->
                _state.update {
                    it.copy(
                        selectedCoinPrice = currentPrice.split(":")
                            .last().drop(1).dropLast(2)
                            .toDouble().toDisplayableNumber()
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        selectedCoinWebSocketJob?.cancel()
        historyJob?.cancel()

        closeConnections()
    }
}