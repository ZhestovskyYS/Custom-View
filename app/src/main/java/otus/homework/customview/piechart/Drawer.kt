package otus.homework.customview.piechart

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF


class Drawer(
    val paint: Paint = Paint()
) {

    fun invoke(
        canvas: Canvas,
        chartRect: RectF,
        piePieces: List<PiePiece>
    ): Canvas {
        piePieces.forEach { piece ->
            paint.setColor(piece.color)
            canvas.drawArc(chartRect, piece.startAngle, piece.sweepAngle, true, paint)
        }
        return canvas
    }
}