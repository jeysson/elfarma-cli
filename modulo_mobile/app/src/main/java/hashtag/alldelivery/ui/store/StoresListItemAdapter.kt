package hashtag.alldelivery.ui.store

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.FIRST_VISIBLE
import hashtag.alldelivery.AllDeliveryApplication.Companion.LAST_VISIBLE
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.utils.LoadViewItemAdpter
import hashtag.alldelivery.ui.home.HomeFragment
import hashtag.alldelivery.ui.products.ProductViewModel
import kotlinx.android.synthetic.main.store_item_adapter.view.*
import kotlinx.android.synthetic.main.store_list_header.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class StoresListItemAdapter(
    frag: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    private val TYPE_HEADER = 1
    private val TYPE_BODY = 2
lateinit var evento:LoadViewItemAdpter
    lateinit var listHeader: View
    lateinit var itemClickListener: AdapterView.OnItemClickListener
    val fragment = frag
    private val model: ProductViewModel by frag.sharedViewModel()
    var itens: ArrayList<Store>? = null
    private lateinit var myView: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemViewHolder {
        if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.store_list_header, parent, false)
            return StoreItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.store_item_adapter, parent, false)
            return StoreItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position > 0) {
            holder as StoreItemViewHolder
            val item = itens?.get(position)
            val name = item?.nomeFantasia
//            Mostra "fechado" + overlay caso a loja esteja indisponível
            if(!item!!.disponivel){
                holder.closedItemOverlay.visibility = VISIBLE
                holder.closedText.visibility = VISIBLE
            }else {
                holder.closedItemOverlay.visibility = GONE
                holder.closedText.visibility = GONE
            }

//            Mudando a visibilidade dos itens
            holder.rating.visibility = GONE
            holder.starIcon.visibility = GONE
            holder.categoryType.visibility = GONE
            holder.dividerCategoryDistance.visibility = GONE
            holder.dividerRatingCategory.visibility = GONE

//            Animação de fadeIn

            if (position < FIRST_VISIBLE || position > LAST_VISIBLE ){
                holder.logo.alpha = 0f
                holder.logo.animate().apply {
                    interpolator = LinearInterpolator()
                    duration = 400
                    alpha(1f)
                 //   startDelay = 100
                    start()
                }
            }
            
//           Inserindo imagem
            if(item.logo != null){
                val imageBytes = android.util.Base64.decode(item.logo, 0)
                val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.logo.setImageBitmap(image)

            }else {
                holder.logo.setImageResource(R.drawable.ic_medicine)
                if(!item.logoCarregado)
                    (fragment as HomeFragment)._storeViewModel.getStoreLogo(item.id)
            }

//            transforma em valor em moeda e verifica se é 0 ou nulo
            var storeFee = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(item.taxaEntrega)
            if (item.taxaEntrega == 0f || item.taxaEntrega == null) {
                holder.deliveryFee.setTextColor(fragment.activity!!.getColor(R.color.green_free_item))
                storeFee = "Gratis"
            }else {
                holder.deliveryFee.setTextColor(fragment.activity!!.getColor(R.color.heavy_grey))
            }

            holder.name.text = name
            holder.rating.text = "4,5"
            holder.categoryDistance.text = "${item.distancia}"
            holder.deliveryTime.text = "${item.tempoMinimo} - ${item.tempoMaximo} min"
            holder.deliveryFee.text = "$storeFee"
            holder.categoryType.text = "Farmácia"

            holder.card.setTag(position)
            holder.card.setOnClickListener(this)
        }
    }

    override fun getItemCount() = itens!!.size

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEADER;

        } else {
            return TYPE_BODY;
        }
    }

    class StoreHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.store_title
    }

    class StoreItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card = view.card
        val name = view.name
        val rating = view.rating_store_item
        val categoryDistance = view.distance_store_item
        val deliveryTime = view.delivery_time
        val deliveryFee = view.delivery_free
        val categoryType = view.category_store_item
        val starIcon = view.star_icon
        val dividerRatingCategory = view.divider_rating_category
        val dividerCategoryDistance = view.divider_category_distance
        val logo = view.logo
        val closedItemOverlay = view.image_view_store_closed_overlay
        val closedText = view.closed
    }

    override fun onClick(view: View?) {
        if (view is CardView) {
            var position = view!!.tag as Int
            AllDeliveryApplication.STORE = itens!!.get(position)
            val manager: FragmentManager = fragment.activity!!.supportFragmentManager
            manager.beginTransaction()
            manager.commit {
                setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                replace(R.id.nav_host_fragment, StoreFragment::class.java, null)
                addToBackStack(null)
            }
        }
    }

    fun addItems(lojas: List<Store>) {
        if(itens.isNullOrEmpty())
            itens = ArrayList<Store>()
        var tamanhoAtual = itens?.size
        itens?.addAll(lojas)
        var tamanhoNovo = itens?.size
        notifyItemRangeChanged(tamanhoAtual!!, tamanhoNovo!!)
    }
}