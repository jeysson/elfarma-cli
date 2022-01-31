package hashtag.elfarma.ui.order

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import hashtag.elfarma.AllDeliveryApplication.Companion.CONCLUIDO
import hashtag.elfarma.R
import hashtag.elfarma.core.models.OrderHistory
import kotlinx.android.synthetic.main.order_history_item_header.view.*
import kotlinx.android.synthetic.main.order_list_previous_item.view.*
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
            .inflate(R.layout.order_list_previous_item, parent, false)
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
            //holder.status.text = obj?.status?.descricao
            holder.order_number.text = String.format(fragment.getString(R.string.order_list_history_number),
                obj.id.toString().padStart(6, '0'))

            holder.first_order_item.text = String.format("%sx %s", obj.first_item_quantity, obj.first_item_name)
            holder.first_order_item_quantity.text = obj.first_item_quantity.toString()
            //
            if(obj?.itensSize!! > 1){
                holder.second_order_item.text = String.format("%sx %s", obj.second_item_quantity, obj.second_item_name)
                holder.second_order_item.visibility = VISIBLE
                holder.second_order_item_quantity.text = obj.second_item_quantity.toString()
                holder.second_order_item_quantity.visibility = VISIBLE
            }
            //
            holder.review_views_group.visibility = GONE
            //
            if(obj?.status?.id!! == CONCLUIDO){
                holder.review_views_group.visibility = VISIBLE
                holder.criteria_rating.visibility = VISIBLE
                //
                if(obj?.diasavaliacao!! < 16) {
                    if (obj?.avaliacao != null) {
                        holder.order_rating_label.text = fragment.getString(R.string.label_evaluate)
                        holder.criteria_rating.rating = obj?.avaliacao
                    }else{
                        holder.order_rating_label.text = fragment.getString(R.string.to_evaluate)
                        holder.evaluated_button.tag = position
                        holder.evaluated_button.setOnClickListener(fragment)
                    }
                }
                //
                if(obj?.diasavaliacao!! > 16) {
                    if (obj?.avaliacao != null) {
                        holder.order_rating_label.text = fragment.getString(R.string.label_evaluate)
                    } else {
                        holder.order_rating_label.text = fragment.getString(R.string.order_list_review_expired)
                        holder.criteria_rating.visibility = INVISIBLE
                    }
                }
            }
            //
            if(obj?.itensSize!!-2 > 0){
                holder.store_item_more.visibility = VISIBLE
                holder.store_item_more.text = String.format(fragment.getString(R.string.order_history_more),
                    (obj?.itensSize!! - 2))
            }

            holder.details_button.tag = position
            holder.details_button.setOnClickListener(fragment)

            if(obj?.status?.id!! < 7){
                holder.pulse.visibility = VISIBLE
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
      //  val status = view.order_status
        val order_number = view.order_number
        val first_order_item = view.first_order_item
        val first_order_item_quantity = view.first_order_item_quantity
        val second_order_item = view.second_order_item
        val second_order_item_quantity = view.second_order_item_quantity
        val store_item_more = view.load_more
        val order_rating_label = view.order_rating_label
        val criteria_rating = view.criteria_rating
     //   val see = view.order_see_more
        val card = view.order_list_previous_item
        val pulse = view.pulse_animation
        val details_button = view.details_button
        val review_views_group = view.review_views_group
        val evaluated_button = view.evaluated_button
    }

    fun addItens(it: ArrayList<Any>){
        if(itens == null)
            itens = ArrayList<Any>()
        //
        itens?.addAll(it)
    }
}