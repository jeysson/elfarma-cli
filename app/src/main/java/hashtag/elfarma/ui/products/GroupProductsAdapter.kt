package hashtag.elfarma.ui.products

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hashtag.elfarma.AllDeliveryApplication.Companion.SEARCH_STORE
import hashtag.elfarma.R
import hashtag.elfarma.core.models.Group
import hashtag.elfarma.core.utils.GroupDiffCallback
import hashtag.elfarma.ui.store.StoreFragment
import kotlinx.android.synthetic.main.product_item.view.*
import kotlinx.android.synthetic.main.store_list_header.view.store_title
import java.util.ArrayList
import kotlin.concurrent.thread

class GroupProductsAdapter(frag: StoreFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val sharedPool = RecyclerView.RecycledViewPool()

    lateinit var groups: ArrayList<Group>
    var fragment = frag
    var adapters: HashMap<Int, ProductAdapter> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)

        var holder = ProductItemViewHolder(view)
        holder.setIsRecyclable(false)
        //
        val layoutM = LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false)
        layoutM.recycleChildrenOnDetach = true
        //
        holder.list.apply {
            layoutManager = layoutM
            setRecycledViewPool(sharedPool)
        }
        //
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder as ProductItemViewHolder
        var group = groups?.get(position)
        holder.name.text = group?.nome

        holder.list.removeAllViews()
        //
       /* var layoutManager = LinearLayoutManager(
            holder.list.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        layoutManager.recycleChildrenOnDetach = true

        holder.list.layoutManager = layoutManager

        holder.list.recycledViewPool = sharedPool*/

        if(adapters.containsKey(group?.id)){
            holder.list.adapter = adapters.get(group?.id)
        }
        else{

            adapters.put(group?.id!!, ProductAdapter( fragment
                                                             , SEARCH_STORE
                                                             , group?.products!!))

            holder.list.adapter = adapters.get(group?.id)
        }

        holder.BindItem()
        //
        if(!group.carregouImagens) {
            //fragment.viewModelStore.eventLoadImage.postValue(position)
            Log.d("LOADIMG", "Chamando met Posição: "+position)
            fragment.ObterImagens(position)
        }
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

    class ProductItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val name = view.store_title
        val list = view.list_horizon

        fun BindItem(){
            list.alpha = 0f
            list.animate().apply {
                interpolator = LinearInterpolator()
                alpha(1f)
                duration = 400
            }
        }
    }

    fun addItems(grp: ArrayList<Group>) {

        if(groups.isNullOrEmpty())
            groups = ArrayList<Group>()
        //
        var old = ArrayList<Group>(groups!!)
        groups?.addAll(grp)
        var new = ArrayList<Group>(groups!!)
        //
        var diffResult = DiffUtil.calculateDiff(GroupDiffCallback(old, new))
        thread(true){
            diffResult.dispatchUpdatesTo(this)
        }
    }
}