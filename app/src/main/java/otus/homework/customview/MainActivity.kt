package otus.homework.customview

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import otus.homework.customview.databinding.ActivityMainBinding
import otus.homework.customview.piechart.Piece

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var pieChartInfo: List<Piece> = emptyList()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pieChartInfo = DemoValuesReader.readPieChartValues(this)

        binding.pieChart.pieces = pieChartInfo
        binding.pieChart.onSectorClickListener = { piece ->
            binding.pieChartLegend.selectedData.isVisible = true
            binding.pieChartLegend.selectedDataBox.isVisible = true

            binding.pieChartLegend.selectedData.text = "${piece.name} (${piece.value})"
            binding.pieChartLegend.selectedDataBox.setBackgroundColor(piece.color)
        }
        binding.pieChartLegend.legendData.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = PieChartLegendAdapter(binding.pieChart.piePieces)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}