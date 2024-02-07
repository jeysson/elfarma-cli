package hashtag.elfarma.ui.products

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.AllDeliveryApplication.Companion.Pedido
import hashtag.elfarma.R
import hashtag.elfarma.core.models.Product
import hashtag.elfarma.core.utils.OnChangedValueListener
import hashtag.elfarma.core.utils.ProductDiffCallback
import hashtag.elfarma.ui.store.StoreFragment
import kotlinx.android.synthetic.main.product_card_item.view.*
import kotlinx.android.synthetic.main.product_card_item.view.btPlusMinus
import kotlinx.android.synthetic.main.product_card_item.view.card_product
import kotlinx.android.synthetic.main.product_card_item.view.cross_selling_item_description
import kotlinx.android.synthetic.main.product_card_item.view.cross_selling_item_details
import kotlinx.android.synthetic.main.product_card_item.view.cross_selling_item_image
import kotlinx.android.synthetic.main.product_card_item.view.cross_selling_item_original_value
import kotlinx.android.synthetic.main.product_card_item.view.cross_selling_item_percentage
import kotlinx.android.synthetic.main.product_card_item.view.cross_selling_item_total_value
import kotlinx.android.synthetic.main.product_card_item.view.cross_selling_item_total_value2
import kotlinx.android.synthetic.main.product_card_item_search.view.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.textColor
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ProductAdapter(
    val frag: Fragment,
    val search:Int = 0,
    val it: ArrayList<Product>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener, OnChangedValueListener {

    private val TYPE_SEARCH_PRODUCT = 1
    private val TYPE_STORE_SEARCH_PRODUCT = 2

    val fragment = frag
    var itens: ArrayList<Product>? = it
    //private lateinit var myView: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == TYPE_SEARCH_PRODUCT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_card_item, parent, false)
            //
            val holder = ProductItemCardViewHolder(view)
            holder.setIsRecyclable(false)
            return holder
        }else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_card_item_search, parent, false)
            //
            val holder = StoreProductItemCardViewHolder(view)
            holder.setIsRecyclable(false)
            return holder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var product = itens?.get(position)
        var p = Pedido?.itens?.firstOrNull { p -> p.produto?.id == product?.id }

        if (holder is ProductItemCardViewHolder){
            if(product?.store != null && !product?.store?.logo.isNullOrEmpty()) {
                holder as ProductItemCardViewHolder
                //
                val imageBytes = android.util.Base64.decode(product?.store?.logo, 0)
                val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val drawable = BitmapDrawable(fragment.resources, image)
                holder.img_store.setImageDrawable(drawable)
            }

            holder.bt.produto = product

            if (p != null)
                holder.bt.total = p?.quantity!!
            else
                holder.bt.total = 0
            holder.card_store.tag = position
            holder.card_store.setOnClickListener(this)
            holder.card.tag = position
            holder.card.setOnClickListener(this)
            holder.name.text = product!!.nome
            holder.detalhe.text = product.descricao
            //
            if(product.precoPromocional != null && product.precoPromocional!! > 0 ){
               // holder.preco.textColor = R.color.deprecated_forest
                holder.preco.text = NumberFormat.getCurrencyInstance(
                    Locale(
                        fragment.getString(R.string.language),
                        fragment.getString(R.string.country)
                    )
                ).format(product.precoPromocional)

                holder.preco2.visibility = View.VISIBLE
                holder.preco2.text = NumberFormat.getCurrencyInstance(
                    Locale(
                        fragment.getString(R.string.language),
                        fragment.getString(R.string.country)
                    )
                ).format(product.precoPromocional)

                holder.precooriginal.visibility = View.VISIBLE
                holder.precooriginal.text = NumberFormat.getCurrencyInstance(
                    Locale(
                        fragment.getString(R.string.language),
                        fragment.getString(R.string.country)
                    )
                ).format(product.preco)
                holder.percentual.visibility = View.VISIBLE
                holder.percentual.text = NumberFormat.getPercentInstance().format (1-(product.precoPromocional!!/product.preco!!))

            }else{
                //holder.preco.textColor = R.color.heavy_grey
                holder.preco.text = NumberFormat.getCurrencyInstance(
                    Locale(
                        fragment.getString(R.string.language),
                        fragment.getString(R.string.country)
                    )
                ).format(product.preco)
            }
            //
            holder.image.alpha = 0f
            holder.image.animate().apply {
                duration = 400
                alpha(1f)
            }

            if (product.productImages!!.size > 0) {
                val imageBytes =
                    android.util.Base64.decode(product.productImages!![0].fotoBase64, 0)
                val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val drawable = BitmapDrawable(fragment.resources, image)
                holder.image.setImageDrawable(drawable)
            }

            if (frag as? StoreFragment != null)
                holder.bt.addOnChangeValueListener(frag)
        }else {
            holder as StoreProductItemCardViewHolder

            holder.bt.produto = product

            if (p != null)
                holder.bt.total = p?.quantity!!
            else
                holder.bt.total = 0

            holder.card.tag = position
            holder.card.setOnClickListener(this)
            holder.name.text = product!!.nome
            holder.detalhe.text = product.descricao

            if(product.precoPromocional != null && product.precoPromocional!! > 0 ){
                //holder.preco.textColor = R.color.deprecated_forest
                holder.preco.text = NumberFormat.getCurrencyInstance(
                    Locale(
                        fragment.getString(R.string.language),
                        fragment.getString(R.string.country)
                    )
                ).format(product.precoPromocional)

                holder.preco2.visibility = View.VISIBLE
                holder.preco2.text = NumberFormat.getCurrencyInstance(
                    Locale(
                        fragment.getString(R.string.language),
                        fragment.getString(R.string.country)
                    )
                ).format(product.precoPromocional)

                holder.precooriginal.visibility = View.VISIBLE
                holder.precooriginal.text = NumberFormat.getCurrencyInstance(
                    Locale(
                        fragment.getString(R.string.language),
                        fragment.getString(R.string.country)
                    )
                ).format(product.preco)
                holder.percentual.visibility = View.VISIBLE
                holder.percentual.text = NumberFormat.getPercentInstance().format (1-(product.precoPromocional!!/product.preco!!))

            }else{
               // holder.preco.textColor = R.color.heavy_grey
                holder.preco.text = NumberFormat.getCurrencyInstance(
                    Locale(
                        fragment.getString(R.string.language),
                        fragment.getString(R.string.country)
                    )
                ).format(product.preco)
            }
            //
            holder.image.alpha = 0f
            holder.image.animate().apply {
                duration = 400
                alpha(1f)
            }

            if (product.productImages != null && product.productImages!!.size > 0) {
                val imageBytes =
                    android.util.Base64.decode(product.productImages!![0].fotoBase64, 0)
                val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val drawable = BitmapDrawable(fragment.resources, image)
                holder.image.setImageDrawable(drawable)
            }

            if (frag as? ProductSearch != null)
                holder.bt.addOnChangeValueListener(frag)
            else if(frag as? StoreFragment != null)
                holder.bt.addOnChangeValueListener(frag)
        }
    }

    override fun getItemCount() = itens!!.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        if (search == 0 || search == 2) {
            return  TYPE_SEARCH_PRODUCT
        }else {
            return TYPE_STORE_SEARCH_PRODUCT
        }
    }

    class ProductItemCardViewHolder(view: View) : StoreProductItemCardViewHolder(view) {
        /*val preco = view.cross_selling_item_total_value
        val name = view.cross_selling_item_description
        val detalhe = view.cross_selling_item_details
        val image = view.cross_selling_item_image
        val card = view.card_product
        val bt = view.btPlusMinus*/
        val card_store = view.card_store
       // val img_store = view.img_store
    }

    open class StoreProductItemCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val preco = view.cross_selling_item_total_value
        val preco2 = view.cross_selling_item_total_value2
        val name = view.cross_selling_item_description
        val detalhe = view.cross_selling_item_details
        val image = view.cross_selling_item_image
        val card = view.card_product
        val bt = view.btPlusMinus
        val img_store = view.img_store
        val percentual = view.cross_selling_item_percentage
        val precooriginal = view.cross_selling_item_original_value
    }

    override fun onClick(view: View?) {
        when(view?.id){
           R.id.card_store->{
               var position = view!!.tag as Int
               AllDeliveryApplication.STORE = itens?.get(position)?.store
               val manager: FragmentManager = frag.activity!!.supportFragmentManager
               manager.beginTransaction()
               manager.commit(true) {
                   setCustomAnimations(
                       R.anim.enter_from_left,
                       R.anim.exit_to_right,
                       R.anim.enter_from_right,
                       R.anim.exit_to_left
                   )

                   addToBackStack(null)
                   replace(R.id.nav_host_fragment, StoreFragment::class.java, null)
               }
           }
           R.id.card_product -> {
               var position = view!!.tag as Int
               AllDeliveryApplication.STORE = itens?.get(position)?.store
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
        if (view is CardView) {

        }
    }

    fun addItems(products: List<Product>) {
        if(itens.isNullOrEmpty())
            itens = ArrayList<Product>()
        //
        var old = ArrayList<Product>(itens!!)
        itens?.addAll(products)
        var new = ArrayList<Product>(itens!!)
        //
        var diffResult = DiffUtil.calculateDiff(ProductDiffCallback(old, new))
        //
        fragment.runOnUiThread {
            diffResult.dispatchUpdatesTo(this)
        }
    }
}