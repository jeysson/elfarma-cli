package hashtag.elfarma.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hashtag.elfarma.R
import kotlinx.android.synthetic.main.publi_item.view.*

class PubliSliderAdapter(list: IntArray) : RecyclerView.Adapter<PubliSliderAdapter.PubliSliderHolder>() {

    var list: IntArray = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PubliSliderHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.publi_item, parent, false)
        return PubliSliderHolder(view)
    }

    override fun onBindViewHolder(holder: PubliSliderHolder, position: Int) {
        holder.view.setImageResource(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class PubliSliderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view = itemView.imgView
    }
}