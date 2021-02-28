package hashtag.alldelivery.ui.products

import android.app.ActivityOptions
import android.app.Application
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.Pedido
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Item
import hashtag.alldelivery.core.models.Order
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.utils.OnChangedValueListener
import hashtag.alldelivery.ui.store.StoreFragment
import kotlinx.android.synthetic.main.product_card_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.util.*

class ProductsListItemAdapter(
    val frag: StoreFragment,
    val lLayoutManager: LinearLayoutManager?,
    val gLayoutManager: GridLayoutManager?,
    itens: ArrayList<Product>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener, OnChangedValueListener {

    lateinit var itemClickListener: AdapterView.OnItemClickListener
    val fragment = frag
    private val model: ProductViewModel by frag.sharedViewModel()
    val itens: ArrayList<Product>? = itens
    private lateinit var myView: View
    var evento: OnChangedValueListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = if (lLayoutManager == null){
            LayoutInflater.from(parent.context)
                .inflate(R.layout.product_card_item, parent, false)
        }else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.product_card_item_search, parent, false)
        }
        myView = view

        var holder = ProductItemCardViewHolder(view)


        holder.setIsRecyclable(false)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var product = itens?.get(position)
        var p = Pedido?.itens?.firstOrNull { p-> p.produto?.id == product?.id }

        holder as ProductItemCardViewHolder

        holder.bt.produto = product

        if(p != null)
            holder.bt.total = p?.quantidade!!
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

        runBlocking(Dispatchers.IO) {
            if (!product.carregouImagens && ((gLayoutManager != null
                        && (gLayoutManager.findLastVisibleItemPosition() + 2) >= position
                        && gLayoutManager.findFirstVisibleItemPosition() <= position)
                        ||
                        (lLayoutManager != null
                                && (lLayoutManager.findLastVisibleItemPosition() + 1) >= position
                                && lLayoutManager.findFirstVisibleItemPosition() <= position))
            ) {
                product.carregouImagens = true
                product.productImages = model?.getImages(product.id)
            }
        }

        if (product.productImages!!.size > 0) {
            val imageBytes = android.util.Base64.decode(product.productImages!![0].fotoBase64, 0)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            holder.image.setImageBitmap(image)
        }

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
            val intent = Intent(fragment.context, ProductDetail::class.java)
            fragment.context?.startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(fragment.activity).toBundle()
            )
        }
    }

    fun addItems(produtos: List<Product>) {
        var tamanhoAtual = itens?.size
        itens?.addAll(produtos)
        var tamanhoNovo = itens?.size
        notifyItemRangeChanged(tamanhoAtual!!, tamanhoNovo!!)
    }

//    override fun OnChangedValue(id:Int, value: Int){
//        if(Pedido == null)
//            Pedido = Order()
//        //
//        var ix = Pedido?.itens?.firstOrNull { p: Item -> p.produto == id   }
//
//        if(ix == null)
//            Pedido?.itens?.add(Item(id, value))
//        else
//            ix.quantidade = value
//
//        evento?.OnChangedValue(id, value)
//    }

//    fun addOnChangeValueListener(ev:OnChangedValueListener){
//        evento = ev
//    }
}