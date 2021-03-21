package hashtag.alldelivery.ui.order

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.OrderHistory
import kotlinx.android.synthetic.main.order_history_item.view.*
import kotlinx.android.synthetic.main.order_history_item_header.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(frag: OrderHistoryFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 1
    private val TYPE_BODY = 2

    val fragment = frag
    var itens: ArrayList<Any>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.order_history_item_header, parent, false)
            return OrderHistoryHeaderHolder(view)
        }
        else{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_item, parent, false)
        return OrderHistoryHolder(view)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = itens?.get(position)

        if(item is OrderHistory){
            holder as OrderHistoryHolder
            val obj = item as OrderHistory

            /*
            * Carrega o logo do estabelecimento
            */
            if(obj?.logo != null){
                val imageBytes = android.util.Base64.decode(obj?.logo, 0)
                val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val drawableImage = BitmapDrawable(fragment.resources, image)
                holder.logo.setImageDrawable(drawableImage)

            }else {
                holder.logo.setImageResource(R.drawable.ic_medicine)
            }
            //
            holder.store_name.text = obj?.store
            holder.order_number.text = String.format(fragment.getString(R.string.order_history_number),
                obj.id.toString().padStart(6, '0'))

            holder.store_item_name.text = String.format("%sx %s", obj.item_quantity, obj.first_item_name)

            if(obj?.itensSize!! > 1){
                holder.store_item_more.visibility = VISIBLE
                holder.store_item_more.text = String.format(fragment.getString(R.string.order_history_more),
                    (obj?.itensSize!! - 1))
            }
        }else{
            holder as OrderHistoryHeaderHolder
            val formatter = DateTimeFormatter.ofPattern("E dd MMMM yyyy", Locale(fragment.getString(R.string.language),
                fragment.getString(R.string.country))
            )

            val date = (item as LocalDate).format(formatter)

            holder.date.text = date
        }
    }

    override fun getItemCount(): Int {
        return if(itens != null) itens?.size!! else 0
    }

    override fun getItemViewType(position: Int): Int {
        if (itens?.get(position) is OrderHistory) {
            return TYPE_BODY
        } else {
            return TYPE_HEADER
        }
    }

    class OrderHistoryHeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date = view.date
    }

    class OrderHistoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo = view.logo
        val store_name = view.store_name
        val order_number = view.order_number
        val store_item_name = view.store_item_name
        val store_item_more = view.store_item_more
    }

    fun addItens(it: ArrayList<Any>){
        if(itens == null)
            itens = ArrayList<Any>()
        //
        itens?.addAll(it)
    }
}