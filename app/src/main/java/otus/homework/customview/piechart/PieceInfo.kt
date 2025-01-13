package otus.homework.customview.piechart

data class PieceInfo(
    val name: String,
    val value: Float,
    val percentage: Float,
    val color: Int,
    val startAngle: Float,
    val sweepAngle: Float,
    val endAngle: Float,
) {
    companion object {
        fun from(piece: Piece, pieData: PiePiece) =
            PieceInfo(
                name = piece.name,
                value = piece.weight,
                percentage = (pieData.sweepAngle / 360) * 100,
                color = pieData.color,
                startAngle = pieData.startAngle,
                sweepAngle = pieData.sweepAngle,
                endAngle = pieData.endAngle,
            )
    }
}
