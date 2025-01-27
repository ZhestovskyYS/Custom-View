package otus.homework.customview.linechart

import java.util.Date

data class Record(
    val name: String,
    val data: List<RecordData>,
)

data class RecordData(
    val date: Date,
    val moneySpent: Double,
)
