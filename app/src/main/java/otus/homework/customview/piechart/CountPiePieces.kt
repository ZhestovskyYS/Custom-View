package otus.homework.customview.piechart

import android.content.Context
import android.graphics.Color
import otus.homework.customview.R

private const val MAX_ANGLE = 360.0
private const val MIN_PIECE_THRESHOLD = 0.1 // 10 percents from max
private val COLOR_SPEC_RANGE = 0..255
private val OTHERS_PLACEHOLDER = R.string.others_placeholder

object CountPiePieces {
    operator fun invoke(context: Context, pieces: List<Piece>): List<PiePiece> {
        val (min, max, sum) = readPiecesStats(pieces)
        if (sum == 0.0) return emptyList()

        val threshold = max * MIN_PIECE_THRESHOLD
        if (min <= threshold) {
            val othersThreshold = pieces
                .filter { it.weight < threshold }

            val meaningPieces = pieces - othersThreshold.toSet()
            if (meaningPieces.isEmpty())
                return mapAllPieces(pieces, sum)

            val othersWeight = othersThreshold.sumOf { it.weight.toDouble() }.toFloat()
            val othersPiece = PiePiece(
                name = context.getString(OTHERS_PLACEHOLDER),
                color = Color.GRAY,
                angle = countPieceAngle(othersWeight, sum),
            )

            val meaningPiecesColors = generateColors(meaningPieces.size)
            return meaningPieces.toPiePieces(meaningPiecesColors, sum) + othersPiece
        } else {
            return mapAllPieces(pieces, sum)
        }
    }


    private fun mapAllPieces(pieces: List<Piece>, sum: Double): List<PiePiece> {
        val colors = generateColors(pieces.size)
        return pieces.toPiePieces(colors, sum)
    }

    private fun List<Piece>.toPiePieces(colors: List<Int>, sum: Double) =
        mapIndexed { index, piece ->
            PiePiece(
                name = piece.name,
                color = colors[index],
                angle = countPieceAngle(piece.weight, sum),
            )
        }

    private fun readPiecesStats(pieces: List<Piece>): PiecesStats {
        val _pieces = pieces.toList()

        var minValue = Float.MAX_VALUE
        var maxValue = Float.MIN_VALUE
        var sum = 0.0

        var previousItem: Piece? = null
        for (item in _pieces) {
            sum += item.weight
            previousItem?.let {
                minValue = if (minValue > it.weight) it.weight else minValue
                maxValue = if (maxValue < it.weight) it.weight else maxValue
            }
            previousItem = item
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