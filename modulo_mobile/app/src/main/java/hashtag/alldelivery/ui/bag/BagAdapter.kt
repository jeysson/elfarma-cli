package hashtag.alldelivery.ui.bag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Group
import hashtag.alldelivery.core.models.Item
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.utils.LoadViewItemAdpter
import hashtag.alldelivery.ui.products.ProductDetail
import hashtag.alldelivery.ui.store.StoreFragment
import kotlinx.android.synthetic.main.bag_content_list_item.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import kotlinx.android.synthetic.main.store_list_header.view.store_title
import kotlinx.coroutines.*
import org.jetbrains.anko.doAsync
import java.text.NumberFormat
import java.util.*

class BagAdapter(frag: BagFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener{
    private var itens: ArrayList<Item>? = null
    var fragment = frag
    var recyledViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bag_content_list_item, parent, false)

        var holder = BagItemViewHolder(fragment, view)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder as BagItemViewHolder
        var item = itens?.get(position)
        holder.name.text = item?.produto?.nome
        holder.quantity.text = item?.quantidade.toString()
        holder.price.text = NumberFormat.getCurrencyInstance(
            Locale(fragment.getString(R.string.language),
                fragment.getString(R.string.country))).format(item?.valor)
        holder.view.tag = position
        holder.view.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return itens!!.size
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

    class BagItemViewHolder(fragment: BagFragment, view: View): RecyclerView.ViewHolder(view) {

        val quantity = view.quantity
        val name = view.name
        val price = view.price
        val view = view

    }

    fun addItems(its: ArrayList<Item>) {

        if(itens.isNullOrEmpty())
            itens = ArrayList<Item>()

        itens?.addAll(its)
    }

    override fun onClick(view: View?) {
        var position = view!!.tag as Int
        AllDeliveryApplication.PRODUCT = itens?.get(position)?.produto

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