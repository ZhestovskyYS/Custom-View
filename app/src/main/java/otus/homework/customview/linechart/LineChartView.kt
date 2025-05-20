package otus.homework.customview.linechart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.os.bundleOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import otus.homework.customview.DemoValuesReader
import otus.homework.customview.NormalizedViewStateUpdater
import otus.homework.customview.dp
import otus.homework.customview.piechart.ColorsGenerator
import otus.homework.customview.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val RESTORE_KEY = "LineChartView#RestoreKey"
private const val RESTORE_RECORDS = "LineChartView#RestoreKey#Record"
private const val RESTORE_COLORS = "LineChartView#RestoreKey#Colors"
private const val DATE_PLACEHOLDER = "01"

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        const val MIN_WIDTH = 250f
        const val MIN_HEIGHT = 150f
        const val DAYS_OFFSET = 2
        const val PRICE_DELIMITER = 800f
        const val GRAPH_LABEL_HORIZONTAL_OFFSET = 12f
    }

    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("MMM", Locale.getDefault())

    //region State
    private val _chartState = MutableStateFlow(LineChartState())
    val chartState = _chartState.asStateFlow()

    var records by NormalizedViewStateUpdater(
        initialValue = emptyList<Record>(),
        normalize = { value ->
            value
                .map {
                    it.copy(
                        data = it.data.sortedBy { it.date }
                    )
                }
                .sortedBy { it.data.last().date }
        },
        onUpdate = { newRecords ->
            _chartState.update { oldState ->
                val colors = if (oldState.colors.size < newRecords.size)
                    ColorsGenerator(newRecords.size) else
                    oldState.colors
                val maxPrice = newRecords
                    .map { it.data.sumOf(RecordData::moneySpent) }
                    .maxBy { it }
                val priceLines = (maxPrice / PRICE_DELIMITER + 1).toInt()
                val firstDay = normalizeDayNumber(
                    getDayNumber(
                        newRecords.first().data.first().date
                    )
                )
                val monthName = getMonthName(
                    newRecords.first().data.first().date
                )
                val lastDay = normalizeDayNumber(
                    getDayNumber(
                        newRecords.last().data.last().date
                    )
                )
                val linesPaths = newRecords.map { record ->
                    val path = Path()
                    path.moveTo(0f, 0f)
                    record.data.forEachIndexed { index, recordData ->
                        path.lineTo(
                            index.toFloat(),
                            recordData.moneySpent.toFloat()
                        )
                    }
                    path
                }

                oldState.copy(
                    colors = colors,
                    linesPaths = linesPaths,
                    maxPrice = maxPrice,
                    priceLines = priceLines,
                    firstDay = firstDay,
                    lastDay = lastDay,
                    monthName = monthName,
                )
            }

            invalidate()
        }
    )

    private fun normalizeDayNumber(dayNumber: Int): Int =
        if (dayNumber >= DAYS_OFFSET + 1)
            dayNumber - DAYS_OFFSET else
            1

    private fun getDayNumber(newDate: Date): Int {
        calendar.apply { timeInMillis = newDate.time }
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun getMonthName(newDate: Date): String {
        calendar.apply { timeInMillis = newDate.time }
        return dateFormatter.format(calendar.time)
    }
    //endregion

    private val axisPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.GRAY
        pathEffect = DashPathEffect(floatArrayOf(1f, 2f), 50f)
    }


    private val underGraphLabelPaint = Paint().apply {
        textSize = 10.sp
        color = Color.BLACK
    }

    private val rightSideGraphLabelPaint = Paint().apply {
        textSize = 12.sp
        color = Color.BLACK
        textAlign = Paint.Align.RIGHT
    }

    private val graphLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 30.dp
        pathEffect = CornerPathEffect(8f)
    }

    private val dateTextBounds = Rect()
    private val priceTextBounds = Rect()

    private val strokeOffset = axisPaint.strokeWidth / 2
    private var verticalsInterval = 0f
    private var horizontalInterval = 0f

    init {
        underGraphLabelPaint.getTextBounds(DATE_PLACEHOLDER, 0, DATE_PLACEHOLDER.length, dateTextBounds)
        val priceDelimiterString = PRICE_DELIMITER.toString()
        rightSideGraphLabelPaint.getTextBounds(priceDelimiterString, 0, priceDelimiterString.length, priceTextBounds)
        if (isInEditMode)
            records = DemoValuesReader.readLineChartValues(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = calculateMeasureSize(widthMode, widthSize, MIN_WIDTH.toInt())
        val height = calculateMeasureSize(heightMode, heightSize, MIN_HEIGHT.toInt())
        _chartState.update {
            it.copy(
                width = width.toFloat(),
                height = height.toFloat(),
            )
        }

        verticalsInterval = (width - strokeOffset * 2) / (chartState.value.daysAmount - 1)
        horizontalInterval = (height - GRAPH_LABEL_HORIZONTAL_OFFSET - strokeOffset * 2) / chartState.value.priceLines.toFloat()

        setMeasuredDimension(width, height)
    }

    private fun calculateMeasureSize(mode: Int, size: Int, minSize: Int): Int {
        return when (mode) {
            MeasureSpec.UNSPECIFIED -> minSize
            MeasureSpec.EXACTLY,
            MeasureSpec.AT_MOST -> minSize.coerceAtLeast(size)

            else -> throw IllegalStateException("Invalid measure mode")
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawAxis()
        canvas.drawLabels()
        canvas.drawChartLines()
    }

    private fun Canvas.drawAxis() {
        val chartState = chartState.value
        for (day in 0 until chartState.daysAmount) {
            val x = day * verticalsInterval + strokeOffset
            drawLine(x, 0f, x, chartState.height - GRAPH_LABEL_HORIZONTAL_OFFSET, axisPaint)
        }

        for (price in 0 until chartState.priceLines) {
            val y = price * horizontalInterval + strokeOffset
            drawLine(0f, y, chartState.width - strokeOffset, y, axisPaint)
        }
    }

    private fun Canvas.drawLabels() {
        fun Canvas.drawDateLabels(state: LineChartState) {
            fun calculateDateLabelOffset(state: LineChartState, dayNumber: Int): Int {
                val firstDay = state.firstDay
                val lastDay = state.lastDay
                return when (dayNumber) {
                    firstDay -> 0
                    lastDay -> dateTextBounds.width()
                    else -> dateTextBounds.width() / 2
                }
            }

            fun formatDayNumber(state: LineChartState, dayNumber: Int): String {
                fun Int.toStringWithLeadingZero(): String = if (this < 10)
                    "0$this" else
                    this.toString()

                val firstDay = state.firstDay
                val monthName = state.monthName
                return if (dayNumber == firstDay) {
                    "${dayNumber.toStringWithLeadingZero()} $monthName"
                } else {
                    dayNumber.toStringWithLeadingZero()
                }
            }

            var curGraphVerticalPlace = 0
            for (dayNumber in state.firstDay..state.lastDay) {
                val offset = calculateDateLabelOffset(state, dayNumber)
                val fullDayNumber = formatDayNumber(state, dayNumber)
                drawText(
                    fullDayNumber,
                    curGraphVerticalPlace * verticalsInterval - offset,
                    state.height,
                    underGraphLabelPaint
                )
                curGraphVerticalPlace++
            }
        }

        fun Canvas.drawPriceLabels(state: LineChartState) {
            val priceLines = state.priceLines
            for (price in 1..<priceLines) {
                drawText(
                    (price * PRICE_DELIMITER.toInt()).toString(),
                    state.width,
                    price * horizontalInterval - priceTextBounds.height() / 2,
                    rightSideGraphLabelPaint
                )
            }
        }

        val chartState = chartState.value
        drawDateLabels(chartState)
        drawPriceLabels(chartState)
    }

    private fun Canvas.drawChartLines() {
        for (record in records) {
            val path = Path()
            record.data.forEachIndexed { index, recordData ->
                if (index == 0) {
                    path.moveTo(
                        (recordData.moneySpent * verticalsInterval).toFloat(),
                        recordData.date.time * horizontalInterval
                    )
                } else {
                    path.lineTo(
                        (recordData.moneySpent * verticalsInterval).toFloat(),
                        recordData.date.time * horizontalInterval
                    )
                }
            }
            drawPath(path, graphLinePaint)
        }
    }


    //region State Save and Restoration
    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            RESTORE_KEY to super.onSaveInstanceState(),
            RESTORE_RECORDS to ArrayList(records),
            RESTORE_COLORS to ArrayList(chartState.value.colors)
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val savedRecords = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                state.getParcelableArrayList(RESTORE_RECORDS, Record::class.java) else
                state.getParcelableArrayList(RESTORE_RECORDS)

            if (savedRecords != null) {
                _chartState.update {
                    it.copy(
                        colors = state.getIntegerArrayList(RESTORE_COLORS)?.toList() ?: emptyList(),
                    )
                }
                records = savedRecords
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                super.onRestoreInstanceState(state.getParcelable(RESTORE_KEY, Record::class.java)) else
                super.onRestoreInstanceState(state.getParcelable(RESTORE_KEY))
        } else {
            super.onRestoreInstanceState(state)
        }
    }
    //endregion
}