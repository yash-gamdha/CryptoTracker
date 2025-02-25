@file:OptIn(ExperimentalLayoutApi::class)

package com.app.cryptotracker.crypto.presentation.coin_detail

import androidx.compose.animation.AnimatedVisibility
import com.app.cryptotracker.R
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.cryptotracker.crypto.presentation.coin_detail.components.ChartStyle
import com.app.cryptotracker.crypto.presentation.coin_detail.components.DataPoint
import com.app.cryptotracker.crypto.presentation.coin_detail.components.InfoCard
import com.app.cryptotracker.crypto.presentation.coin_detail.components.LineChart
import com.app.cryptotracker.crypto.presentation.coin_list.CoinListState
import com.app.cryptotracker.crypto.presentation.coin_list.components.previewCoin
import com.app.cryptotracker.crypto.presentation.models.toDisplayableNumber
import com.app.cryptotracker.ui.theme.greenBackground

@Composable
fun CoinDetailScreen(
    state: CoinListState,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    }

    if (state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else if (state.selectedCoin != null) {
        val coin = state.selectedCoin

        Scaffold { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = coin.iconRes),
                    contentDescription = coin.name,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = coin.name,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = contentColor
                )
                Text(
                    text = coin.symbol,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    color = contentColor
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    InfoCard(
                        title = stringResource(id = R.string.market_cap),
                        formattedNumber = "$ ${coin.marketCapUsd.formatted}",
                        icon = ImageVector.vectorResource(id = R.drawable.stock)
                    )
                    InfoCard(
                        title = stringResource(id = R.string.price),
                        formattedNumber = "$ ${state.selectedCoinPrice.formatted}",
                        icon = ImageVector.vectorResource(id = R.drawable.dollar)
                    )
                    val absoluteChangeFormatted =
                        (state.selectedCoinPrice.value * (coin.changePercent24Hr.value / 100))
                            .toDisplayableNumber()
                    val isPositive = coin.changePercent24Hr.value > 0.0
                    val contentColor = if (isPositive) {
                        if (isSystemInDarkTheme()) Color.Green else greenBackground
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                    InfoCard(
                        title = stringResource(id = R.string.change_last_24hr),
                        formattedNumber = absoluteChangeFormatted.formatted,
                        icon = if (isPositive) {
                            ImageVector.vectorResource(id = R.drawable.trending)
                        } else {
                            ImageVector.vectorResource(id = R.drawable.trending_down)
                        },
                        contentColor = contentColor
                    )
                }

                AnimatedVisibility(
                    visible = coin.coinPriceHistory.isNotEmpty()
                ) {
                    var selectedDataPoint by remember {
                        mutableStateOf<DataPoint?>(null)
                    }

                    var labelWidth by remember {
                        mutableFloatStateOf(0f)
                    }

                    var totalChartWidth by remember {
                        mutableFloatStateOf(0f)
                    }

                    val amountOfVisibleDataPoints = if (labelWidth > 0) {
                        ((totalChartWidth - 2.5 * labelWidth) / labelWidth).toInt()
                    } else {
                        0
                    }

                    val startIndex = (coin.coinPriceHistory.lastIndex - amountOfVisibleDataPoints)
                        .coerceAtLeast(0)

                    LineChart(
                        dataPoints = coin.coinPriceHistory,
                        style = ChartStyle(
                            chartLineColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.secondary
                                .copy(alpha = 0.4f),
                            selectedColor = MaterialTheme.colorScheme.primary,
                            gridLineThicknessPx = 5f,
                            axisLineThicknessPx = 5f,
                            xAxisLabelSpacing = 8.dp,
                            yAxisLabelSpacing = 25.dp,
                            verticalPadding = 8.dp,
                            horizontalPadding = 8.dp,
                            labelFontSize = 14.sp
                        ),
                        visibleDataPointsIndices = startIndex..coin.coinPriceHistory.lastIndex,
                        unit = "$",
                        selectedDataPoint = selectedDataPoint,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f)
                            .onSizeChanged { totalChartWidth = it.width.toFloat() },
                        onSelectedDataPoint = { selectedDataPoint = it },
                        onXLabelWidthChange = { labelWidth = it }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CoinDetailScreenPreview() {
    CoinDetailScreen(
        state = CoinListState(selectedCoin = previewCoin),
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}