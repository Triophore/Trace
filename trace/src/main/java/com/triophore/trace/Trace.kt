package com.triophore.trace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * A Composable function to display a real-time trace visualization of numeric data.
 *
 * The trace is drawn on a canvas, with customizable grid lines for visual reference.
 * The trace itself is animated to scroll horizontally, creating a sense of real-time data flow.
 *
 * @param modifier Modifiers to be applied to the layout.
 * @param verticalScale Scaling factor for the vertical amplitude of the trace.
 *  Larger values increase the height of the wave.
 * @param horizontalScale Time duration (in seconds) represented by the full width of the trace.
 * @param sampleRate Sampling rate of the data (in samples per second).
 * @param data The list of numeric data points to be visualized as the trace.
 * @param backgroundColor The background color of the trace area.
 * @param gridColor The color of the grid lines. Default is `Color.White`.
 * @param gridWidth The width of the grid lines. Default is `1.0f`.
 * @param horizontalGridLines The number of horizontal grid lines to display. Default is `4`.
 * @param verticalGridLines The number of vertical grid lines to display. Default is `9`.
 */
@Composable
fun Trace(
    modifier: Modifier = Modifier,
    verticalScale: Float,
    horizontalScale: Int = 1, // in seconds
    sampleRate: Double, // in sps
    data: List<Double>,
    backgroundColor: Color,
    gridColor: Color = Color.White,
    gridWidth: Float = 1.0f,
    horizontalGridLines: Int = 0,
    verticalGridLines: Int = 0
) {
    BoxWithConstraints(modifier = modifier.background(color = backgroundColor)) {
        val horizontalShift = 1 / ((horizontalScale * sampleRate))
        var key by remember { mutableStateOf(false) }
        key = data.size > 1
        val overlayWidth: Dp by animateDpAsState(
            if (!key) maxWidth else 0.dp,
            keyframes {
                durationMillis = horizontalScale * 1000
            }, label = ""
        )
        BoxWithConstraints(
            modifier = Modifier
                .height(maxHeight)
                .width(maxWidth),
            contentAlignment = Alignment.CenterEnd
        ) {
            Spacer(modifier = Modifier
                .height(maxHeight)
                .width(maxWidth)
                .drawWithCache {
                    val path = generatePath(
                        data = data,
                        size = size,
                        yScale = verticalScale,
                        horizontalShift = horizontalShift
                    )
                    onDrawBehind {
                        drawPath(path = path, color = Color.Green, style = Stroke(1.dp.toPx()))
                    }
                })
            Box(
                modifier = Modifier
                    .height(maxHeight)
                    .width(overlayWidth)
                    .background(color = backgroundColor)
            )
            Spacer(
                modifier = Modifier
                    .height(maxHeight)
                    .width(maxWidth)
                    .drawWithCache {
                        val verticalSize = size.width / (verticalGridLines + 1)
                        val horizontalSize = size.height / (horizontalGridLines + 1)
                        onDrawBehind {
                            repeat(verticalGridLines) { i ->
                                val startX = verticalSize * (i + 1)
                                drawLine(
                                    gridColor,
                                    start = Offset(startX, 0f),
                                    end = Offset(startX, size.height),
                                    strokeWidth = gridWidth
                                )

                            }
                            repeat(horizontalGridLines) { i ->
                                val startY = horizontalSize * (i + 1)
                                drawLine(
                                    gridColor,
                                    start = Offset(0f, startY),
                                    end = Offset(size.width, startY),
                                    strokeWidth = gridWidth
                                )

                            }
                        }
                    }
            )
        }
    }
}

/**
 * Generates a `Path` object representing the visual trace from numeric data points.
 *
 * The path is created by iterating through the data points, calculating their corresponding
 * coordinates on the canvas, and connecting them with line segments.
 *
 * The x-coordinate of each point is determined by its index in the list, scaled by the
 * `horizontalShift` factor. The y-coordinate is calculated to center the trace vertically
 * within the canvas, and scaled by the `yScale` factor to adjust its amplitude.
 *
 * @param data The list of numeric data points to be converted into a path.
 * @param size The `Size` of the canvas on which the path will be drawn.
 * @param yScale Scaling factor for the vertical amplitude of the path.
 * @param horizontalShift Factor determining the horizontal distance between each data point on the path.
 * @return A `Path` object representing the generated trace visualization.
 */
private fun generatePath(
    data: List<Double>,
    size: Size,
    yScale: Float,
    horizontalShift: Double
): Path {
    val path = Path()
    data.forEachIndexed { index, d ->
        val x = index * horizontalShift.toFloat() * size.width
        val y = ((size.height / 2 - (size.height * d.toFloat() / 2) * yScale).toFloat())
        if (index == 0)
            path.moveTo(x, y)
        else
            path.lineTo(x, y)
    }
    return path
}


