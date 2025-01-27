package otus.homework.customview.piechart

import android.content.Context
import android.graphics.Color
import otus.homework.customview.R

private const val START_ANGLE = 0.0f
private const val MAX_ANGLE = 360.0f
private val OTHERS_PLACEHOLDER = R.string.others_placeholder

class Count(
    val thresholdPercent: Float = 0.05f,
    val thresholdPlaceholder: String? = null,
) {
    init {
        require(thresholdPercent < 1.0f && START_ANGLE in -MAX_ANGLE..MAX_ANGLE)
    }

    operator fun invoke(context: Context, pieces: List<Piece>, colors: List<Int>): List<PiePiece> {
        val (min, max, sum) = readPiecesStats(pieces)
        if (sum == 0.0) return emptyList()

        val threshold = max * thresholdPercent
        if (min <= threshold) {
            val othersThreshold = pieces
                .filter { it.weight < threshold }

            val meaningPieces = pieces - othersThreshold.toSet()
            val sortedMeaningPieces = meaningPieces.sortedByDescending { it.weight }
            if (meaningPieces.isEmpty())
                return mapAllPieces(sortedMeaningPieces, colors, sum)

            val meaningPiePieces = sortedMeaningPieces.toPiePieces(colors, sum)
            val othersPiece = PiePiece(
                name = thresholdPlaceholder ?: context.getString(OTHERS_PLACEHOLDER),
                color = Color.GRAY,
                startAngle = meaningPiePieces.last().endAngle,
                sweepAngle = (MAX_ANGLE - meaningPiePieces.last().endAngle) % MAX_ANGLE,
            )

            return meaningPiePieces + othersPiece
        } else {
            return mapAllPieces(pieces, colors, sum)
        }
    }


    private fun mapAllPieces(pieces: List<Piece>, colors: List<Int>, sum: Double): List<PiePiece> {
        pieces.ifEmpty { return emptyList() }

        return pieces.foldIndexed(emptyList<PiePiece>()) { idx, acc, piece ->
            val sweep = ((piece.weight / sum) * MAX_ANGLE).toFloat()
            val start = acc.lastOrNull()?.endAngle ?: START_ANGLE

            acc + PiePiece(
                name = piece.name,
                color = colors[idx],
                startAngle = start,
                sweepAngle = sweep
            )
        }
    }

    private fun List<Piece>.toPiePieces(colors: List<Int>, sum: Double): List<PiePiece> {
        var previousItemAngle = START_ANGLE
        return mapIndexed { index, piece ->
            val pieceAngle = countPieceAngle(piece.weight, sum)
            PiePiece(
                name = piece.name,
                color = colors[index],
                startAngle = previousItemAngle,
                sweepAngle = pieceAngle,
            ).also {
                previousItemAngle = ((previousItemAngle + pieceAngle) % MAX_ANGLE + MAX_ANGLE) % MAX_ANGLE
            }
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

    private data class PiecesStats(
        val minValue: Float,
        val maxValue: Float,
        val sum: Double,
    )
}