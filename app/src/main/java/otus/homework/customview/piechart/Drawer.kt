package otus.homework.customview.piechart

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF


class Drawer(
    val paint: Paint = Paint(),
    val startAngle: Float = -90f,
    private val holePath: Path = Path()
) {
    private var lastDrawnRectF: RectF? = null

    fun invoke(
        canvas: Canvas,
        chartRect: RectF,
        piePieces: List<PiePiece>
    ): Canvas {
        if (chartRect != lastDrawnRectF) {
            canvas.clipHole(chartRect)
            lastDrawnRectF = chartRect
        }
        piePieces.forEach { piece ->
            paint.setColor(piece.color)
            canvas.drawArc(chartRect, piece.startAngle + startAngle, piece.sweepAngle, true, paint)
        }
        return canvas
    }

    private fun Canvas.clipHole(chartRect: RectF) {
        holePath.reset()
        holePath.addCircle(
            chartRect.centerX(),
            chartRect.centerY(),
            chartRect.width() / 5f,
            Path.Direction.CW
        )

        clipOutPath(holePath)
    }
}