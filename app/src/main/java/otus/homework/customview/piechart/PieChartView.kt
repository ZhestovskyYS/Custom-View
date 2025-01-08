package otus.homework.customview.piechart

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

/*private const val RESTORE_KEY = "PieChartView#RestoreKey"
private const val RESTORE_STATE = "PieChartView#RestoreKey#State"
private const val RESTORE_SELECTED_ID = "PieChartView#RestoreKey#SelectedID"*/

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var pieces by Delegates.observable(emptySet<Piece>()) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            val newPiePieces = CountPiePieces(context, newValue.toList())
            if (newPiePieces != piePieces) {
                piePieces = newPiePieces
                invalidate()
            }
        }
    }
    var piePieces = emptyList<PiePiece>()
    private set
    var selectedID: String? = null
        private set
    var onSectorClickListener: ((PiePiece) -> Unit)? = null


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 300
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (piePieces.isEmpty()) return

        DrawPieChart(context, canvas, piePieces)
    }

   /* override fun onSaveInstanceState(): Parcelable {
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
                selectedID = state.getString(RESTORE_SELECTED_ID)
                updateAngles(data, data.sumOf { it.amount.toDouble() }.toFloat())
                selectedCategoryIndex?.let {
                    val category = savedData[it]
                    onSectorClickListener?.invoke(category)
                    animateSelection(it)
                }
            }
            super.onRestoreInstanceState(state.getParcelable(KEY_SUPER_STATE))
        } else {
            super.onRestoreInstanceState(state)
        }
    }*/


}