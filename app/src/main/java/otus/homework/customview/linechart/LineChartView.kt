package otus.homework.customview.linechart

import android.content.Context
import android.graphics.Canvas
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import otus.homework.customview.DemoValuesReader
import otus.homework.customview.piechart.ColorsGenerator
import kotlin.properties.Delegates

private const val RESTORE_KEY = "LineChartView#RestoreKey"
private const val RESTORE_PIECES = "LineChartView#RestoreKey#Record"
private const val RESTORE_COLORS = "LineChartView#RestoreKey#Colors"

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var colors = emptyList<Int>()

    var records by Delegates.vetoable(emptyList<Record>()) { _, oldValue, newValue ->
        val allowed = oldValue != newValue
        if (allowed) {
            if (colors.size < newValue.size)
                colors = ColorsGenerator(newValue.size)
        }

        return@vetoable allowed
    }

    init {
        if (isInEditMode)
            records = DemoValuesReader.readLineChartValues(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }
}