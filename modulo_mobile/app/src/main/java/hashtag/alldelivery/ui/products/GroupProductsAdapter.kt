package hashtag.alldelivery.ui.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Group
import hashtag.alldelivery.core.utils.LoadViewItemAdpter
import hashtag.alldelivery.ui.store.StoreFragment
import kotlinx.android.synthetic.main.product_item.view.*
import kotlinx.android.synthetic.main.store_list_header.view.store_title
import kotlinx.coroutines.*
import org.jetbrains.anko.doAsync

class GroupProductsAdapter(val frag: StoreFragment, var groups: List<Group>?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    lateinit var evento: LoadViewItemAdpter
    var fragment = frag
    var recyledViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)

        var holder = ProductItemViewHolder(fragment, view)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder as ProductItemViewHolder
        var group =groups?.get(position)
        holder.name.text = group?.nome
        holder.list.removeAllViews()
        //
        holder.BindItem(AllDeliveryApplication.STORE?.id, group)

       /* if(evento != null){
            evento.OnLoadViewItemAdpter(group?.id, position, holder)
        }*/
    }

    override fun getItemCount(): Int {
        return groups!!.size
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

    class ProductItemViewHolder(fragment: StoreFragment, view: View): RecyclerView.ViewHolder(view) {

        val name = view.store_title
        val list = view.list_horizon
        val frag = fragment

        fun BindItem(store: Int?, group: Group?){
            var layoutManager = LinearLayoutManager(
                list.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            list.layoutManager = layoutManager
            var adapt = ProductsListItemAdapter(frag, layoutManager, null, group?.products)
            list.adapter = adapt
            list.alpha = 0f
            list.animate().apply {
                interpolator = LinearInterpolator()
                alpha(1f)
                duration = 400
            }
        }
    }

    fun setOnLoadViewItemAdpter(objeto: LoadViewItemAdpter){
        evento = objeto
    }
}