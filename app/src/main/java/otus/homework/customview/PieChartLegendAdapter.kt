package otus.homework.customview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import otus.homework.customview.databinding.PieChartLegendItemBinding
import otus.homework.customview.piechart.PiePiece
import otus.homework.customview.piechart.Piece
import otus.homework.customview.piechart.PieceInfo

class PieChartLegendAdapter(
    private val legendInfo: List<PiePiece>
) : RecyclerView.Adapter<PieChartLegendAdapter.ViewHolder>() {

    override fun getItemCount() = legendInfo.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            PieChartLegendItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(legendInfo[position])

    class ViewHolder(
        private val binding: PieChartLegendItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(piece: PiePiece) {
            binding.title.text = piece.name
            binding.colorBox.setBackgroundColor(piece.color)
        }
    }
}