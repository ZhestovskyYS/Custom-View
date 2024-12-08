package otus.homework.customview

import android.content.Context
import android.graphics.Color
import android.health.connect.datatypes.units.Percentage
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var piePieces by Delegates.observable(emptySet<PiePiece>()) { _, _, newValue ->

    }
    private var piecesPercented = emptyList<PiePiecePercented>()


}

class PiePiecePercented(
    val piece: PiePiece,
    val percentage: Float,
)

data class PiePiece(
    val color: Color,
    val weight: Float,
    val name: String,
)