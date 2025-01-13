package otus.homework.customview.piechart

import android.graphics.Color


private val COLOR_SPEC_RANGE = 0..255

object ColorsGenerator {
    operator fun invoke(number: Int): List<Int> {
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
}