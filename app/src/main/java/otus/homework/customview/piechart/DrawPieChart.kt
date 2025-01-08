package otus.homework.customview.piechart

import android.content.Context
import android.graphics.Canvas

private const val START_ANGLE = -90.0f

object DrawPieChart {
    operator fun invoke(
        context: Context,
        canvas: Canvas,
        piePieces: List<PiePiece>
    ): Canvas = canvas.apply {

    }
}