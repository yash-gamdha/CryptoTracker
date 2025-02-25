package com.app.cryptotracker.crypto.presentation.coin_detail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.cryptotracker.crypto.domain.CoinPrice
import com.app.cryptotracker.ui.theme.CryptoTrackerTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun LineChart(
    dataPoints: List<DataPoint>,
    style: ChartStyle,
    visibleDataPointsIndices: IntRange,
    unit: String,
    modifier: Modifier = Modifier,
    selectedDataPoint: DataPoint? = null,
    onSelectedDataPoint: (DataPoint) -> Unit = {},
    onXLabelWidthChange: (Float) -> Unit = {},
    showGridLines: Boolean = true
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = style.labelFontSize
    )

    val visibleDataPoints = remember(dataPoints, visibleDataPointsIndices) {
        dataPoints.slice(visibleDataPointsIndices)
    }

    val maxYValue = remember(visibleDataPoints) {
        dataPoints.maxOfOrNull { it.y } ?: 0f
    }
    val minYValue = remember(visibleDataPoints) {
        dataPoints.minOfOrNull { it.y } ?: 0f
    }

    val measurer = rememberTextMeasurer()

    var xLabelWidth by remember {
        mutableFloatStateOf(0F)
    }

    LaunchedEffect(key1 = xLabelWidth) {
        onXLabelWidthChange(xLabelWidth)
    }

    val selectedDataPointIndex = remember(selectedDataPoint) {
        dataPoints.indexOf(selectedDataPoint)
    }

    var drawPoints by remember {
        mutableStateOf(listOf<DataPoint>())
    }

    var isShowingDataPoints by remember {
        mutableStateOf(selectedDataPoint != null)
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(drawPoints, xLabelWidth) {
                detectHorizontalDragGestures { change, _ ->
                    val newSelectedDataPointIndex = getSelectedDataPointIndex(
                        touchOffsetX = change.position.x,
                        triggerWidth = xLabelWidth,
                        drawPoints = drawPoints
                    )

                    isShowingDataPoints = (newSelectedDataPointIndex + visibleDataPointsIndices.first) in
                            visibleDataPointsIndices

                    if (isShowingDataPoints) {
                        onSelectedDataPoint(dataPoints[newSelectedDataPointIndex])
                    }
                }
            }
    ) {
        val yAxisLabelSpacingPx = style.yAxisLabelSpacing.toPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()
        val horizontalPaddingPx = style.horizontalPadding.toPx()

        val xLabelTextLayoutResult = visibleDataPoints.map {
            measurer.measure(
                text = it.xLabel,
                style = textStyle.copy(textAlign = TextAlign.Center)
            )
        }

        val maxXLabelHeight = xLabelTextLayoutResult.maxOfOrNull { it.size.height } ?: 0
        val maxXLabelWidth = xLabelTextLayoutResult.maxOfOrNull { it.size.width } ?: 0
        val maxXLabelLineCount = xLabelTextLayoutResult.maxOfOrNull { it.lineCount } ?: 0
        val xLabelLineHeight = if (maxXLabelLineCount > 0) {
            maxXLabelHeight / maxXLabelLineCount
        } else 0

        val viewPortHeightPx = size.height -
                (maxXLabelHeight + (2 * verticalPaddingPx) + xLabelLineHeight + xAxisLabelSpacingPx)

        // Y Label calculations
        val yLabelViewPortHeightPx = viewPortHeightPx + xLabelLineHeight
        val yLabelCountExcludingLastLabel = ((yLabelViewPortHeightPx / (xLabelLineHeight + yAxisLabelSpacingPx))).toInt()
        val valueIncrement = (maxYValue - minYValue) / yLabelCountExcludingLastLabel // incrementing the price value on y axis

        val yLabels = (0..yLabelCountExcludingLastLabel).map {
            ValueLabel(
                value = maxYValue - (valueIncrement * it),
                unit = unit
            )
        }

        val yLabelTextLayoutResults = yLabels.map {
            measurer.measure(
                text = it.formatted(),
                style = textStyle
            )
        }
        val maxYLabelWidth = yLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0


        val viewPortTopY = verticalPaddingPx + xLabelLineHeight + 10f
        val viewPortBottomY = viewPortHeightPx + viewPortTopY
        val viewPortRightX = size.width
        val viewPortLeftX = 2f * horizontalPaddingPx + maxYLabelWidth

        xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx

        xLabelTextLayoutResult.forEachIndexed { index, result ->
            val x = viewPortLeftX + (xLabelWidth * index) + (xAxisLabelSpacingPx / 2f)
            val y = viewPortBottomY + xAxisLabelSpacingPx
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x,
                    y = y
                ),
                color = if (selectedDataPointIndex == index) {
                    style.selectedColor
                } else style.unselectedColor,
            )

            if (showGridLines) {
                drawLine(
                    color = if (selectedDataPointIndex == index) {
                        style.selectedColor
                    } else style.unselectedColor,
                    start = Offset(
                        x = x + result.size.width / 2f,
                        y = viewPortBottomY
                    ),
                    end = Offset(
                        x = x + result.size.width / 2f,
                        y = viewPortTopY
                    ),
                    strokeWidth = if (selectedDataPointIndex == index) {
                        style.gridLineThicknessPx * 1.5f
                    } else style.gridLineThicknessPx
                )
            }

            if (selectedDataPointIndex == index) {
                val valueLabel = ValueLabel(
                    value = visibleDataPoints[index].y,
                    unit = unit
                )

                val valueResult = measurer.measure(
                    text = valueLabel.formatted(),
                    style = textStyle.copy(
                        color = style.selectedColor
                    ),
                    maxLines = 1
                )

                val textPositionX = if (selectedDataPointIndex == visibleDataPoints.lastIndex) {
                    x - valueResult.size.width
                } else {
                    x - valueResult.size.width / 2f
                } + result.size.width / 2f

                val isTextVisible =
                    (size.width - textPositionX).roundToInt() in 0..size.width.roundToInt()

                if (isTextVisible) {
                    drawText(
                        textLayoutResult = valueResult,
                        topLeft = Offset(
                            x = textPositionX,
                            y = viewPortTopY - valueResult.size.height - 10f
                        )
                    )
                }
            }
        }

        val heightRequiresForYLabels = xLabelLineHeight * (yLabelCountExcludingLastLabel + 1) // xLabelLineHeight = yLabelLineHeight
        val remainingHeightForYLabels = yLabelViewPortHeightPx - heightRequiresForYLabels
        val spaceBetweenYLabels = remainingHeightForYLabels / yLabelCountExcludingLastLabel

        yLabelTextLayoutResults.forEachIndexed { index, result ->
            val x = horizontalPaddingPx + maxYLabelWidth - result.size.width.toFloat() // right aligning the y label values
            val y = viewPortTopY +
                    index * (spaceBetweenYLabels + xLabelLineHeight) -
                    xLabelLineHeight / 2f

            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x,
                    y = y
                ),
                color = style.unselectedColor,
            )

            if (showGridLines) {
                drawLine(
                    color = style.unselectedColor,
                    start = Offset(
                        x = viewPortLeftX,
                        y = y + result.size.height / 2f
                    ),
                    end = Offset(
                        x = viewPortRightX,
                        y = y + result.size.height / 2f
                    ),
                    strokeWidth = style.gridLineThicknessPx
                )
            }
        }

        drawPoints = visibleDataPointsIndices.map {
            val x = viewPortLeftX + (it - visibleDataPointsIndices.first) *
                    xLabelWidth + xLabelWidth / 2f

            // [minYValue .. maxYValue] -> [0.0 .. 1.0]
            val ratio = (dataPoints[it].y - minYValue) / (maxYValue - minYValue)
            val y = viewPortBottomY - (ratio * viewPortHeightPx)
            DataPoint(
                x = x,
                y = y,
                xLabel = dataPoints[it].xLabel
            )
        }

        val connectionPoints1 = mutableListOf<DataPoint>()
        val connectionPoints2 = mutableListOf<DataPoint>()

        for (i in 1 until drawPoints.size) {
            val p0 = drawPoints[i - 1]
            val p1 = drawPoints[i]

            val x = (p0.x + p1.x) / 2f
            val y1 = p0.y
            val y2 = p1.y

            connectionPoints1.add(DataPoint(x, y1))
            connectionPoints2.add(DataPoint(x, y2))
        }

        val linePath = Path().apply {
            if (drawPoints.isNotEmpty()) {
                moveTo(drawPoints.first().x, drawPoints.first().y)

                for (i in 1 until drawPoints.size) {
                    cubicTo(
                        x1 = connectionPoints1[i - 1].x,
                        y1 = connectionPoints1[i - 1].y,
                        x2 = connectionPoints2[i - 1].x,
                        y2 = connectionPoints2[i - 1].y,
                        x3 = drawPoints[i].x,
                        y3 = drawPoints[i].y
                    )
                }
            }
        }

        drawPath(
            path = linePath,
            color = style.chartLineColor,
            style = Stroke(
                width = 3f,
                cap = StrokeCap.Round
            )
        )

        drawPoints.forEachIndexed { index, point ->
            if (isShowingDataPoints) {
                val circleOffset = Offset(
                    x = point.x,
                    y = point.y
                )
                drawCircle(
                    color = style.unselectedColor,
                    center = circleOffset,
                    radius = 10f
                )

                if (selectedDataPointIndex == index) {
                    drawCircle(
                        color = Color.White,
                        center = circleOffset,
                        radius = 15f
                    )
                    drawCircle(
                        color = style.selectedColor,
                        center = circleOffset,
                        radius = 15f,
                        style = Stroke(
                            width = 3f
                        )
                    )
                }
            }
        }
    }
}

private fun getSelectedDataPointIndex(
    touchOffsetX: Float,
    triggerWidth: Float,
    drawPoints: List<DataPoint>
): Int {
    val triggerRangeLeft = touchOffsetX - triggerWidth / 2f
    val triggerRangeRight = touchOffsetX + triggerWidth / 2f

    return drawPoints.indexOfFirst {
        it.x in triggerRangeLeft..triggerRangeRight
    }
}

@Preview(widthDp = 1000)
@Composable
private fun LineChartPreview() {
    CryptoTrackerTheme {
        val testCoinHistory = remember {
            (1..20).map {
                CoinPrice(
                    priceUsd = Random.nextDouble() * 1000,
                    time = ZonedDateTime.now().plusHours(it.toLong())
                )
            }
        }
        val style = ChartStyle(
            chartLineColor = Color.Black,
            unselectedColor = Color(0xFF777777),
            selectedColor = Color.Blue,
            gridLineThicknessPx = 2.5f,
            axisLineThicknessPx = 5f,
            xAxisLabelSpacing = 8.dp,
            yAxisLabelSpacing = 25.dp,
            verticalPadding = 8.dp,
            horizontalPadding = 8.dp,
            labelFontSize = 14.sp
        )

        val dataPoints = testCoinHistory.map {
            DataPoint(
                x = it.time.hour.toFloat(),
                y = it.priceUsd.toFloat(),
                xLabel = DateTimeFormatter
                    .ofPattern("ha\nM/d") // h - hour, a -> am/pm, \n -> new line, M -> Month, / -> /, d -> date
                    .format(it.time)
            )
        }

        LineChart(
            dataPoints = dataPoints,
            style = style,
            visibleDataPointsIndices = 0..4,
            unit = "$",
            modifier = Modifier
                .height(300.dp)
                .width(700.dp)
                .background(Color.White),
            selectedDataPoint = dataPoints[18],
        )
    }
}