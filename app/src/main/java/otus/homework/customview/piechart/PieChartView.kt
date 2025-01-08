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
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import otus.homework.customview.DemoValuesReader
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.properties.Delegates

private const val RESTORE_KEY = "PieChartView#RestoreKey"
private const val RESTORE_STATE = "PieChartView#RestoreKey#State"
private const val RESTORE_SELECTED_ID = "PieChartView#RestoreKey#SelectedID"

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val count = Count()
    private val drawer = Drawer()
    private val chartRect = RectF()

    var pieces by Delegates.observable(emptySet<Piece>()) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            val newPiePieces = count(context, newValue.toList())
            if (newPiePieces != piePieces) {
                piePieces = newPiePieces
                invalidate()
            }
        }
    }
    var piePieces = emptyList<PiePiece>()
        private set
    var selectedID: Int? = null
        private set
    var onSectorClickListener: ((PiePiece) -> Unit)? = null

    init {
        if (isInEditMode)
            pieces = DemoValuesReader.readPieChartValues(context).toSet()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 300
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        updateRectSize(width, height)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (piePieces.isEmpty()) return

        drawer.invoke(canvas, chartRect, piePieces)
    }


    //region onTouchEvent
    override fun onTouchEvent(event: MotionEvent) =
        when (event.action) {
            MotionEvent.ACTION_UP   -> performClick()
            MotionEvent.ACTION_DOWN -> handleClickEvent(event.x, event.y)
            else                    -> super.onTouchEvent(event)
        }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun handleClickEvent(clickX: Float, clickY: Float): Boolean {
        if (onSectorClickListener == null) return false

        val cx = width / 2f
        val cy = height / 2f
        val dx = clickX - cx
        val dy = clickY - cy
        val distanceFromCenter = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        val radius = (width / 3f).coerceAtLeast(height / 3f)
        val strokeWidth = drawer.paint.strokeWidth

        if (distanceFromCenter !in (radius - strokeWidth / 2)..(radius + strokeWidth / 2))
            return false

        val touchAngle = (Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())) + 360) % 360
        selectedID = piePieces
            .indexOfFirst { it.anglesRange.contains(touchAngle) }
            .takeIf { it != -1 }
            ?.also {
                onSectorClickListener?.invoke(piePieces[it])
            }

        return selectedID != null
    }

    //endregion


    //region State Save and Restoration
    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            RESTORE_KEY to super.onSaveInstanceState(),
            RESTORE_STATE to ArrayList(piePieces),
            RESTORE_SELECTED_ID to selectedID,
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val savedData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                state.getParcelableArrayList(RESTORE_STATE, PiePiece::class.java) else
                state.getParcelableArrayList(RESTORE_STATE)

            if (savedData != null) {
                piePieces = savedData
                selectedID = state.getInt(RESTORE_SELECTED_ID)
                selectedID?.let {
                    val category = savedData[it]
                    onSectorClickListener?.invoke(category)
//                    animateSelection(it)
                }
            }
            super.onRestoreInstanceState(state.getParcelable(RESTORE_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }
    //endregion

    private fun updateRectSize(width: Int, height: Int) {
        val radius = (width / 3f).coerceAtMost(height / 3f)
        val cx = width / 2f
        val cy = height / 2f
        chartRect.set(cx - radius, cy - radius, cx + radius, cy + radius)
    }
}