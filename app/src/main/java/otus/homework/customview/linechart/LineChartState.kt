package otus.homework.customview.linechart

data class LineChartState(
    val minWidth: Float,
    val minHeight: Float,
    val width: Float,
    val height: Float,
    val maxPrice: Int,
    val priceDelimiter: Float,
    val priceLines: Int,
    val firstDay: Int,
    val lastDay: Int,
    val daysAmount: Int,
    val monthName: String,
)
