package com.app.cryptotracker.core.navigation

import android.widget.Toast
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.cryptotracker.core.presentation.util.ObserveAsEvents
import com.app.cryptotracker.core.presentation.util.toString
import com.app.cryptotracker.crypto.presentation.coin_detail.CoinDetailScreen
import com.app.cryptotracker.crypto.presentation.coin_list.CoinListActions
import com.app.cryptotracker.crypto.presentation.coin_list.CoinListEvent
import com.app.cryptotracker.crypto.presentation.coin_list.CoinListScreen
import com.app.cryptotracker.crypto.presentation.coin_list.CoinListViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveCoinListDetailPane(
    modifier: Modifier = Modifier,
    viewModel: CoinListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val coins by viewModel.coins.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    // handling errors
    val context = LocalContext.current

    ObserveAsEvents(
        events = viewModel.events
    ) { event ->
        when (event) {
            is CoinListEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.toString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                CoinListScreen(
                    state = state,
                    searchText = searchText,
                    onAction = { action ->
                        viewModel.onAction(action)
                        when (action) {
                            is CoinListActions.OnCoinClicked -> {
                                navigator.navigateTo(
                                    pane = ListDetailPaneScaffoldRole.Detail
                                )
                            }
                        }
                    },
                    onSearchTextChange = viewModel::onSearchTextChange,
                    isSearching = isSearching,
                    coins = coins,
                    onRefresh = viewModel::refresh,
                    isRefreshing = isRefreshing
                )
            }
        },
        detailPane = {
            AnimatedPane {
                CoinDetailScreen(state = state)
            }
        },
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.closeConnections()
        }
    }
}