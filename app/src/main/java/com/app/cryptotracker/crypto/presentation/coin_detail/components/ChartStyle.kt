package com.app.cryptotracker.crypto.presentation.coin_detail.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

data class ChartStyle(
    val chartLineColor: Color,
    val unselectedColor: Color,
    val selectedColor: Color,
    val gridLineThicknessPx: Float,
    val axisLineThicknessPx: Float,
    val xAxisLabelSpacing: Dp,
    val yAxisLabelSpacing: Dp,
    val verticalPadding: Dp,
    val horizontalPadding: Dp,
    val labelFontSize: TextUnit
)