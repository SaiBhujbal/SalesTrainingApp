package com.example.salestrainingapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProductPerformanceChart(
    data: List<ChartData>,
    primaryColor: Color,
    secondaryColor: Color,
    gridLineColor: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = width / (data.size * 3)

        // Draw grid lines
        for (i in 0..4) {
            val y = height - (height / 4) * i
            drawLine(
                color = gridLineColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw bars
        data.forEachIndexed { index, item ->
            val x = (width / data.size) * index + barWidth

            // Current score bar
            val currentHeight = (item.currentScore / 100f) * height
            drawRect(
                color = primaryColor,
                topLeft = Offset(x, height - currentHeight),
                size = Size(barWidth, currentHeight)
            )

            // Previous score bar
            val previousHeight = (item.previousScore / 100f) * height
            drawRect(
                color = secondaryColor,
                topLeft = Offset(x + barWidth * 1.2f, height - previousHeight),
                size = Size(barWidth, previousHeight)
            )
        }
    }
}

@Composable
fun SkillPerformanceChart(
    data: List<ChartData>,
    primaryColor: Color,
    secondaryColor: Color,
    gridLineColor: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = width / (data.size * 3)

        // Draw grid lines
        for (i in 0..4) {
            val y = height - (height / 4) * i
            drawLine(
                color = gridLineColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw bars
        data.forEachIndexed { index, item ->
            val x = (width / data.size) * index + barWidth

            // Score bar
            val scoreHeight = (item.currentScore / 100f) * height
            drawRect(
                color = primaryColor,
                topLeft = Offset(x, height - scoreHeight),
                size = Size(barWidth, scoreHeight)
            )

            // Benchmark bar
            val benchmarkHeight = (item.previousScore / 100f) * height
            drawRect(
                color = secondaryColor,
                topLeft = Offset(x + barWidth * 1.2f, height - benchmarkHeight),
                size = Size(barWidth, benchmarkHeight)
            )
        }
    }
}