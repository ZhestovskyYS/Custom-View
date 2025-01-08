package otus.homework.customview

import android.content.Context
import org.json.JSONArray
import otus.homework.customview.piechart.Piece
import java.io.BufferedReader

class DemoValuesReader {

    fun readPieChartValues(context: Context): List<Piece> {
        val payloads = context.readPayloads()
        return payloads.map {
            Piece(
                name = it.name,
                weight = it.amount.toFloat(),
            )
        }
    }


    private fun Context.readPayloads(): List<Payload> {
        val json = resources
            .openRawResource(R.raw.payload)
            .bufferedReader()
            .use(BufferedReader::readText)

        return buildList {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val payload = array.getJSONObject(i)
                add(
                    Payload(
                        payload.getInt("id"),
                        payload.getString("name"),
                        payload.getInt("amount"),
                        payload.getString("category"),
                        payload.getLong("time"),
                    )
                )
            }
        }
    }

    private data class Payload(
        val id: Int,
        val name: String,
        val amount: Int,
        val category: String,
        val time: Long,
    )
}