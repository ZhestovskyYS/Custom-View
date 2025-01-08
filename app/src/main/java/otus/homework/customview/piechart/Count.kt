package otus.homework.customview.piechart

import android.content.Context
import android.graphics.Color
import otus.homework.customview.R

private const val MAX_ANGLE = 360.0f
private val COLOR_SPEC_RANGE = 0..255
private val OTHERS_PLACEHOLDER = R.string.others_placeholder

class Count(
    val startAngle: Float = -90.0f,
    val thresholdPercent: Float = 0.05f,
    val thresholdPlaceholder: String? = null,
) {
    init {
        require(thresholdPercent < 1.0f && startAngle in -360.0f..360.0f)
    }

    operator fun invoke(context: Context, pieces: List<Piece>): List<PiePiece> {
        val (min, max, sum) = readPiecesStats(pieces)
        if (sum == 0.0) return emptyList()

        val threshold = max * thresholdPercent
        if (min <= threshold) {
            val othersThreshold = pieces
                .filter { it.weight < threshold }

            val meaningPieces = pieces - othersThreshold.toSet()
            val sortedMeaningPieces = meaningPieces.sortedByDescending { it.weight }
            if (meaningPieces.isEmpty())
                return mapAllPieces(sortedMeaningPieces, sum)

            val meaningPiecesColors = generateColors(meaningPieces.size)
            val meaningPiePieces = sortedMeaningPieces.toPiePieces(meaningPiecesColors, sum)
            val othersPiece = PiePiece(
                name = thresholdPlaceholder ?: context.getString(OTHERS_PLACEHOLDER),
                color = Color.GRAY,
                startAngle = meaningPiePieces.last().endAngle,
                sweepAngle = (MAX_ANGLE + startAngle - meaningPiePieces.last().endAngle).toFloat(),
            )

            return meaningPiePieces + othersPiece
        } else {
            return mapAllPieces(pieces, sum)
        }
    }


    private fun mapAllPieces(pieces: List<Piece>, sum: Double): List<PiePiece> {
        pieces.ifEmpty { return emptyList() }

        val colors = generateColors(pieces.size)
        val piecesWithoutLastOne = pieces - pieces.last()
        return piecesWithoutLastOne.toPiePieces(colors, sum)
            .let {
                it + PiePiece(
                    name = pieces.last().name,
                    color = colors[pieces.lastIndex],
                    startAngle = it.last().endAngle,
                    sweepAngle = (MAX_ANGLE - it.last().endAngle).toFloat(),
                )
            }
    }

    private fun List<Piece>.toPiePieces(colors: List<Int>, sum: Double): List<PiePiece> {
        var previousItemAngle = startAngle
        return mapIndexed { index, piece ->
            PiePiece(
                name = piece.name,
                color = colors[index],
                startAngle = previousItemAngle,
                sweepAngle = countPieceAngle(piece.weight, sum),
            ).also { previousItemAngle = it.endAngle }
        }
    }

    private fun readPiecesStats(pieces: List<Piece>): PiecesStats {
        var minValue = Float.MAX_VALUE
        var maxValue = Float.MIN_VALUE
        var sum = 0.0

        for (item in pieces) {
            sum += item.weight
            minValue = if (minValue > item.weight) item.weight else minValue
            maxValue = if (maxValue < item.weight) item.weight else maxValue
        }
        return PiecesStats(minValue, maxValue, sum)
    }

    private fun countPieceAngle(pieceWeight: Float, sum: Double) =
        ((pieceWeight / sum) * MAX_ANGLE).toFloat()


    private fun generateColors(number: Int): List<Int> {
        val colors = mutableListOf<Int>()
        while (colors.size < number) {
            colors.add(
                Color.rgb(
                    COLOR_SPEC_RANGE.random(),
                    COLOR_SPEC_RANGE.random(),
                    COLOR_SPEC_RANGE.random(),
                )
            )
        }
        return colors
    }

    private data class PiecesStats(
        val minValue: Float,
        val maxValue: Float,
        val sum: Double,
    )
}