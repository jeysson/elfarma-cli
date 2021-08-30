package hashtag.elfarma.ui.order

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import hashtag.elfarma.R
import hashtag.elfarma.core.models.Order
import kotlinx.android.synthetic.main.order_evaluate_item_row.view.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class OrderEvaluateAdapter(frag: OrderFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val fragment = frag
    var itens: ArrayList<Order>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_evaluate_item_row, parent, false)
        return OrderEvaluateHolder(view)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = itens?.get(position)
        holder as OrderEvaluateHolder
        val obj = item as Order

            /*
            * Carrega o logo do estabelecimento
            */
            if(obj?.store?.logo != null){
                val imageBytes = android.util.Base64.decode(obj?.store?.logo, 0)
                val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val drawableImage = BitmapDrawable(fragment.resources, image)
                holder.logo.setImageDrawable(drawableImage)
                holder.logo.visibility = VISIBLE
                holder.logo_container.visibility = VISIBLE
                holder.header_image.visibility = VISIBLE
            }
            //
            holder.store_name.text = obj?.store?.nomeFantasia
        val formatter = DateTimeFormatter.ofPattern(
            "MMM", Locale(
                fragment.getString(R.string.language),
                fragment.getString(R.string.country)
            )
        )
            holder.day.text = obj?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()?.dayOfMonth.toString()

            holder.month.text = obj?.date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()?.format(formatter)
        
        holder.card.tag = position

        holder.card.setOnClickListener(fragment)
    }

    override fun getItemCount(): Int {
        return if(itens != null) itens?.size!! else 0
    }

    class OrderEvaluateHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logo = view.store_image
        val logo_container = view.logo_container
        val header_image = view.header_image
        val store_name = view.store_name
        val day = view.day
        val month = view.month
        val card = view.card_view
    }

    fun addItens(it: ArrayList<Order>){
        if(itens == null)
            itens = ArrayList<Order>()
        //
        itens?.addAll(it)
    }
}