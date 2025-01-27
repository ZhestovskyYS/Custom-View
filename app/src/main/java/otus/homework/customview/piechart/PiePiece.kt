package otus.homework.customview.piechart

import androidx.annotation.ColorInt

data class PiePiece(
    val name: String,
    @ColorInt val color: Int,
    val startAngle: Float,
    val sweepAngle: Float,
) {
    val endAngle get() = startAngle + sweepAngle
    val anglesRange get() = startAngle..endAngle
}