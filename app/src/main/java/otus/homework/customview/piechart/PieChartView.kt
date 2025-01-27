package otus.homework.customview.piechart

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.os.bundleOf
import otus.homework.customview.DemoValuesReader
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.properties.Delegates

private const val RESTORE_KEY = "PieChartView#RestoreKey"
private const val RESTORE_PIECES = "PieChartView#RestoreKey#Pieces"
private const val RESTORE_COLORS = "PieChartView#RestoreKey#Colors"
private const val RESTORE_SELECTED_ID = "PieChartView#RestoreKey#SelectedID"

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var colors = emptyList<Int>()
    private val count = Count()
    private val drawer = Drawer()
    private val chartRect = RectF()

    var pieces by Delegates.vetoable(emptyList<Piece>()) { _, oldValue, newValue ->
        val allowed = oldValue != newValue
        if (allowed) {
            if (colors.size < newValue.size)
                colors = ColorsGenerator(newValue.size)
            val newPiePieces = count(context, newValue.toList(), colors)
            if (newPiePieces != piePieces) {
                piePieces = newPiePieces
                invalidate()
            }
        }

        return@vetoable allowed
    }
    var piePieces = emptyList<PiePiece>()
        private set
    var selectedID: Int? = null
        private set
    var onSectorClickListener: ((PieceInfo) -> Unit)? = null

    init {
        if (isInEditMode)
            pieces = DemoValuesReader.readPieChartValues(context)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 300
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        updateRectSize(width, height)
        setMeasuredDimension(width, height)
    }

    private fun updateRectSize(width: Int, height: Int) {
        val radius = (width / 3f).coerceAtMost(height / 3f)
        val cx = width / 2f
        val cy = height / 2f
        chartRect.set(cx - radius, cy - radius, cx + radius, cy + radius)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (piePieces.isEmpty()) return

        drawer.invoke(canvas, chartRect, piePieces)
    }


    //region onTouchEvent
    override fun onTouchEvent(event: MotionEvent) =
        when (event.action) {
            MotionEvent.ACTION_UP -> performClick()
            MotionEvent.ACTION_DOWN -> handleClickEvent(event.x, event.y)
            else -> super.onTouchEvent(event)
        }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun handleClickEvent(clickX: Float, clickY: Float): Boolean {
        if (onSectorClickListener == null) return false

        val touchAngle = countTouchAngle(clickX, clickY, width, height, onClickIsOutOfChart = { return false })
        selectedID = findTouchedPieceID(touchAngle)
        selectedID?.also {
            val sortedPieces = pieces.sortedByDescending { it.weight }
            onSectorClickListener?.invoke(
                PieceInfo.from(sortedPieces[it], piePieces[it])
            )
        }
        return selectedID != null
    }

    private inline fun countTouchAngle(
        clickX: Float,
        clickY: Float,
        width: Int,
        height: Int,
        onClickIsOutOfChart: () -> Unit
    ): Double {
        val (distanceFromCenter, dx, dy, chartArea) = countDistances(clickX, clickY, width, height)
        val doesTouchPointBelongToChart = distanceFromCenter in chartArea

        if (!doesTouchPointBelongToChart)
            onClickIsOutOfChart()

        val rawAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble()))
        val shiftedAngle = rawAngle - drawer.startAngle
        val touchAngle = (shiftedAngle + 360) % 360
        return touchAngle
    }

    private fun countDistances(clickX: Float, clickY: Float, width: Int, height: Int): Distances {
        val cx = width / 2f
        val cy = height / 2f
        val dx = clickX - cx
        val dy = cy - clickY
        val distanceFromCenter = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        val radius = (width / 3f).coerceAtLeast(height / 3f)
        val strokeWidth = drawer.paint.strokeWidth
        val holeRadius = chartRect.width() / 5f
        val chartArea = (holeRadius - strokeWidth / 2)..(radius + strokeWidth / 2)
        return Distances(distanceFromCenter, dx, dy, chartArea)
    }

    private data class Distances(
        val distanceFromCenter: Float,
        val dx: Float,
        val dy: Float,
        val chartArea:  ClosedFloatingPointRange<Float>,
    )

    private fun findTouchedPieceID(touchAngle: Double): Int? = piePieces
        .indexOfFirst {
            val normalizedStart = it.startAngle % 360f
            val normalizedEnd = (it.startAngle + it.sweepAngle) % 360f

            when {
                normalizedEnd > normalizedStart ->
                    touchAngle in normalizedStart..normalizedEnd
                else ->
                    touchAngle >= normalizedStart || touchAngle < normalizedEnd
            }
        }
        .takeIf { it != -1 }

    //endregion


    //region State Save and Restoration
    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            RESTORE_KEY to super.onSaveInstanceState(),
            RESTORE_SELECTED_ID to selectedID,
            RESTORE_PIECES to ArrayList(piePieces),
            RESTORE_COLORS to colors.toIntArray(),
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val savedData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                state.getParcelableArrayList(RESTORE_PIECES, Piece::class.java) else
                state.getParcelableArrayList(RESTORE_PIECES)

            if (savedData != null) {
                colors = state.getIntegerArrayList(RESTORE_COLORS)?.toList() ?: emptyList()
                pieces = savedData
                selectedID = state.getInt(RESTORE_SELECTED_ID)
                selectedID?.let {
                    val info = PieceInfo.from(pieces[it], piePieces[it])
                    onSectorClickListener?.invoke(info)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                super.onRestoreInstanceState(state.getParcelable(RESTORE_KEY, Piece::class.java)) else
                super.onRestoreInstanceState(state.getParcelable(RESTORE_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }
    //endregion
}