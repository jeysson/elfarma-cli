package hashtag.alldelivery.ui.products

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Color.alpha
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat.animate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Product
import kotlinx.android.synthetic.main.product_card_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.util.*
import java.util.logging.Handler

class ProductsListItemAdapter(
    val frag: Fragment,
    val lLayoutManager: LinearLayoutManager?,
    val gLayoutManager: GridLayoutManager?,
    itens: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    lateinit var itemClickListener: AdapterView.OnItemClickListener
    val fragment = frag
    private val model: ProductViewModel by frag.sharedViewModel()
    val itens: ArrayList<Product>? = itens
    private lateinit var myView: View


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


        holder as ProductItemCardViewHolder




        //        Increment Button

        var isOpen = false
        var count = 1
        holder.textItemCount.text = count.toString()
        holder.plusButton.setOnClickListener {
            isOpen = animationListener(holder.plusButton, holder.cardViewIncrementItem, isOpen)
        }
        holder.incrementButton.setOnClickListener {
            count += 1
            holder.textItemCount.text = count.toString()
        }
        holder.decrementButton.setOnClickListener {
            if (count <= 1) {
                isOpen = animationListener(holder.plusButton, holder.cardViewIncrementItem, isOpen)
            } else {
                count -= 1
                holder.textItemCount.text = count.toString()
            }

        }


        var product = itens?.get(position)



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


        val incrementButton = view.findViewById<Button>(R.id.increment_selling_item_button)
        val decrementButton = view.findViewById<Button>(R.id.decrement_selling_item_button)
        val textItemCount = view.findViewById<TextView>(R.id.txt_item_result)
        val cardViewIncrementItem =
            view.findViewById<MaterialCardView>(R.id.card_view_increment_decrement_item)
        val plusButton = view.findViewById<Button>(R.id.image_button_plus_item)
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

    fun animateAppear(plusButton: Button, cardViewIncrementItem: MaterialCardView) {
        plusButton.animate().apply {
            alpha(0f)
            duration = 200
        }.withEndAction {
            plusButton.animate().translationY(200f)
            val plusAnimate = TranslateAnimation(0f, 0f, 0f , 0f)
            plusAnimate.duration = 200
            plusAnimate.fillAfter = true

            cardViewIncrementItem.animate().alpha(1f).duration = 200
            val cardAnimate = TranslateAnimation(0f, 0f, 0f , 0f)
            cardAnimate.duration = 200
            cardAnimate.fillAfter = true

            cardViewIncrementItem.startAnimation(cardAnimate)
            plusButton.startAnimation(plusAnimate)
        }

    }

    fun animateDisappear(plusButton: Button, cardViewIncrementItem: MaterialCardView) {
        cardViewIncrementItem.animate().apply {
            alpha(0f)
            duration = 200
        }.withEndAction {
            plusButton.animate().translationY(0f).withEndAction {
                plusButton.animate().alpha(1f).duration = 200
            }
            val plusAnimate = TranslateAnimation(0f, 0f, 0f , 0f)
            plusAnimate.duration = 200
            plusAnimate.fillAfter = true

            val cardAnimate = TranslateAnimation(0f, 0f, 0f , 0f)
            cardAnimate.duration = 200
            cardAnimate.fillAfter = true

            cardViewIncrementItem.startAnimation(cardAnimate)
            plusButton.startAnimation(plusAnimate)
        }

    }

    fun animationListener(plusButton: Button, cardViewIncrementItem: MaterialCardView, isOpen: Boolean): Boolean {
        var opened = false
        if (isOpen){
            animateDisappear(plusButton, cardViewIncrementItem)
            opened = false
        }else if (!isOpen){
            animateAppear(plusButton, cardViewIncrementItem)
            opened = true
        }

        return opened
    }
}