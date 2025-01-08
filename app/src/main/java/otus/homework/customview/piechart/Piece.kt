package otus.homework.customview.piechart

data class Piece(
    val name: String,
    val weight: Float,
) {
    init {
        require(weight >= 0.0)
    }
}