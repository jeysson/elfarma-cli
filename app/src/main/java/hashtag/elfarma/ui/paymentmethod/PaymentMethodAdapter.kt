package hashtag.elfarma.ui.paymentmethod

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hashtag.elfarma.AllDeliveryApplication.Companion.Pedido
import hashtag.elfarma.R
import hashtag.elfarma.core.models.PaymentMethod
import kotlinx.android.synthetic.main.payment_method_item.view.*
import kotlinx.android.synthetic.main.payment_method_item.view.title

class PaymentMethodAdapter(val fragment: PaymentMethodFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 1
    private val TYPE_BODY = 2

    var itens: ArrayList<PaymentMethod>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.payment_method_header_item, parent, false)
            return PaymentMethodHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.payment_method_item, parent, false)
            return PaymentMethodItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itens?.get(position)

        if(item?.header!!){
            holder as PaymentMethodHeaderViewHolder
            holder.title.text = item.nome
        }else {
            holder as PaymentMethodItemViewHolder

            if(Pedido?.paymentMethod != null && Pedido?.paymentMethod?.id == item.id){
                holder.bt.setBackgroundResource(R.drawable.bg_payment_selected)
            }

            holder.title.text = item?.nome
            //
            if (item?.imagem != null) {
                val imageBytes = android.util.Base64.decode(item?.imagem, 0)
                val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val drawableImage = BitmapDrawable(fragment.resources, image)
                holder.brand.setImageDrawable(drawableImage)
            }

            holder.bt.tag = position
            holder.bt.setOnClickListener(fragment)
        }
    }

    override fun getItemCount(): Int {
        return itens?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        if (itens?.get(position)?.header!!) {
            return TYPE_HEADER
        } else {
            return TYPE_BODY
        }
    }

    fun addItens(its: ArrayList<PaymentMethod>){
        if(itens == null)
            itens = ArrayList()

        itens?.addAll(its)
    }

    class PaymentMethodHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.title
    }

    class PaymentMethodItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val brand = view.brand
        val title = view.title
        val bt = view.bt
    }
}