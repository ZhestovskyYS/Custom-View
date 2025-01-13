package otus.homework.customview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import otus.homework.customview.databinding.PieChartLegendItemBinding
import otus.homework.customview.piechart.Piece

class PieChartLegendAdapter(
    private val legendInfo: List<Piece>
) : RecyclerView.Adapter<PieChartLegendAdapter.ViewHolder>() {


    class ViewHolder(private val binding: PieChartLegendItemBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}