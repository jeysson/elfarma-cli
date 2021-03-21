package hashtag.alldelivery.ui.products

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.Pedido
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.utils.OnChangedValueListener
import hashtag.alldelivery.ui.store.StoreFragment
import kotlinx.android.synthetic.main.product_card_item.view.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductAdapter(
    val frag: Fragment,
    val search:Boolean = false,
    val it: ArrayList<Product>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener, OnChangedValueListener {

    val fragment = frag
    var itens: ArrayList<Product>? = it
    private lateinit var myView: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = if (!search){
            LayoutInflater.from(parent.context)
                .inflate(R.layout.product_card_item_search, parent, false)
        }else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.product_card_item, parent, false)
        }
        myView = view

        var holder = ProductItemCardViewHolder(view)

        holder.setIsRecyclable(false)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var product = itens?.get(position)
        var p = Pedido?.itens?.firstOrNull { p -> p.produto?.id == product?.id }

        holder as ProductItemCardViewHolder

        holder.bt.produto = product

        if (p != null)
            holder.bt.total = p?.quantity!!
        else
            holder.bt.total = 0

        holder.card.tag = position
        holder.card.setOnClickListener(this)
        holder.name.text = product!!.nome
        holder.detalhe.text = product.descricao
        holder.preco.text = NumberFormat.getCurrencyInstance(
            Locale(
                fragment.getString(R.string.language),
                fragment.getString(R.string.country)
            )
        ).format(product.preco)
        //
        holder.image.alpha = 0f
        holder.image.animate().apply {
            duration = 400
            alpha(1f)
        }

        if (product.productImages!!.size > 0) {
            val imageBytes = android.util.Base64.decode(product.productImages!![0].fotoBase64, 0)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val drawable = BitmapDrawable(fragment.resources, image)
            holder.image.setImageDrawable(drawable)
        }

        if (frag as? StoreFragment != null)
            holder.bt.addOnChangeValueListener(frag)
    }

    override fun getItemCount() = itens!!.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ProductItemCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val preco = view.cross_selling_item_total_value
        val name = view.cross_selling_item_description
        val detalhe = view.cross_selling_item_details
        val image = view.cross_selling_item_image
        val card = view.card_product
        val bt = view.btPlusMinus
    }

    override fun onClick(view: View?) {
        if (view is CardView) {
            var position = view!!.tag as Int
            AllDeliveryApplication.PRODUCT = itens?.get(position)

            val manager: FragmentManager = fragment.activity!!.supportFragmentManager
            manager.beginTransaction()
            manager.commit {
                setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )

                addToBackStack(null)
                replace(R.id.nav_host_fragment, ProductDetail::class.java, null)

            }
        }
    }

    fun addItems(produtos: List<Product>) {
        var tamanhoAtual = itens?.size
        itens?.addAll(produtos)
        var tamanhoNovo = itens?.size
        notifyItemRangeChanged(tamanhoAtual!!, tamanhoNovo!!)
    }
}