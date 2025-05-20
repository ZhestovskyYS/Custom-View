package otus.homework.customview.linechart

import android.graphics.Path

data class LineChartState(
    val width: Float = LineChartView.MIN_WIDTH,
    val height: Float = LineChartView.MIN_HEIGHT,

    val colors: List<Int> = emptyList(),
    val linesPaths: List<Path> = emptyList(),

    val maxPrice: Double = 0.0,
    val priceLines: Int = 0,
    val firstDay: Int = 1,
    val lastDay: Int = 100,
    val monthName: String = "",
) {
    val daysAmount: Int
        get() = (lastDay - firstDay) + 1
}
