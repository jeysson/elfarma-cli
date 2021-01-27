package hashtag.alldelivery.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Filter
import kotlinx.android.synthetic.main.filter_bar_button.view.*
import java.util.ArrayList

class FilterListAdapter internal constructor(list: List<Filter>): RecyclerView.Adapter<FilterListAdapter.FilterItemViewHolder>(),
    View.OnClickListener  {

    private var filters: List<Filter> = list

    inner class FilterItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.filter_bar_button, parent, false)
        return FilterItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterItemViewHolder, position: Int) {
        val item = filters?.get(position)

        holder.title.text = item!!.name
    }

    override fun getItemCount(): Int = filters!!.size

    override fun onClick(v: View?) {

    }

    fun setFilter(list: List<Filter>) {
        filters = list
        this.notifyDataSetChanged()
    }
}