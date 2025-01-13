package otus.homework.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import otus.homework.customview.databinding.ActivityMainBinding
import otus.homework.customview.piechart.PiePiece
import otus.homework.customview.piechart.Piece

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var pieChartInfo: List<Piece> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pieChartInfo = DemoValuesReader.readPieChartValues(this)

        binding.pieChart.pieces = pieChartInfo
        binding.pieChart.onSectorClickListener = { piece ->

        }
        binding.pieChartLegend.legendData
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}