package com.app.cryptotracker.crypto.presentation.coin_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.cryptotracker.crypto.presentation.app_info.AppInfoBottomSheet
import com.app.cryptotracker.crypto.presentation.coin_list.components.CoinListItem
import com.app.cryptotracker.crypto.presentation.coin_list.components.previewCoin
import com.app.cryptotracker.crypto.presentation.models.CoinUI
import com.app.cryptotracker.ui.theme.CryptoTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListScreen(
    state: CoinListState,
    searchText: String,
    coins: List<CoinUI>?,
    isSearching: Boolean,
    isRefreshing: Boolean,
    onAction: (CoinListActions) -> Unit,
    onRefresh: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    val scrollState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    val bottomSheetState = rememberModalBottomSheetState()
    var isSheetVisible by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "CryptoTracker",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { isSheetVisible = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "App Info"
                            )
                        }
                    },
                    scrollBehavior = scrollBehaviour
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    value = searchText,
                    onValueChange = { onSearchTextChange(it) },
                    placeholder = { Text("Search by name or symbol") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon"
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    onSearchTextChange("")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search field",
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(75),
                    singleLine = true,
                    colors = TextFieldDefaults.colors()
                )
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (isSearching) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (coins == null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (coins.isEmpty()) {
                        Column(
                            modifier = modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Not found",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "(┬┬﹏┬┬)",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        PullToRefreshBox(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                            isRefreshing = isRefreshing,
                            onRefresh = { onRefresh() },
                            state = pullToRefreshState,
                        ) {
                            LazyColumn(
                                modifier = modifier
                                    .fillMaxSize(),
                                state = scrollState
                            ) {
                                items(coins) { coinUI ->
                                    CoinListItem(
                                        coinUI = coinUI,
                                        onClick = {
                                            onAction(CoinListActions.OnCoinClicked(coinUI))
                                        }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isSheetVisible) {
            AppInfoBottomSheet(
                sheetState = bottomSheetState,
                onDismiss = {
                    isSheetVisible = false
                },
                context = LocalContext.current,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CoinListScreenPreview() {
    CryptoTrackerTheme {
        CoinListScreen(
            state = CoinListState(
                coins = (1..100).map {
                    previewCoin.copy(id = "$it")
                }
            ),
            onAction = {},
            onSearchTextChange = {},
            searchText = "",
            isSearching = false,
            coins = emptyList(),
            onRefresh = {},
            isRefreshing = false
        )
    }
}