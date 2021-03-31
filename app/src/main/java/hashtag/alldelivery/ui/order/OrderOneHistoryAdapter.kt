package hashtag.alldelivery.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.OrderItem
import hashtag.alldelivery.ui.products.ProductDetail
import kotlinx.android.synthetic.main.bag_content_list_item.view.*
import java.text.NumberFormat
import java.util.*

class OrderOneHistoryAdapter(frag: OrderOneHistoryFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var itens: ArrayList<OrderItem>? = null
    var fragment = frag

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bag_content_list_item, parent, false)

        var holder = OrderOneHistoryItemViewHolder(view)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder as OrderOneHistoryItemViewHolder
        var item = itens?.get(position)
        holder.name.text = item?.produto?.nome
        holder.quantity.text = item?.quantity.toString()
        holder.price.text = NumberFormat.getCurrencyInstance(
            Locale(fragment.getString(R.string.language),
                fragment.getString(R.string.country))).format(item?.price)
        holder.view.tag = position
    }

    override fun getItemCount(): Int {
        return itens!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    class OrderOneHistoryItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val quantity = view.quantity
        val name = view.name
        val price = view.price
        val view = view

    }

    fun addItems(its: ArrayList<OrderItem>) {

        if(itens.isNullOrEmpty())
            itens = ArrayList<OrderItem>()

        itens?.addAll(its)
    }
}