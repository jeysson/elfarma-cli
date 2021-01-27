package hashtag.alldelivery.ui.lojas

import android.app.ActivityOptions
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.ui.address.AddressEditBottomDialogFragment
import hashtag.alldelivery.ui.home.HomeFragment
import kotlinx.android.synthetic.main.store_item_adapter.view.*
import kotlinx.android.synthetic.main.store_list_header.view.*

class StoresListItemAdapter(val act: AppCompatActivity, val itens: List<Store>): RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    private val TYPE_HEADER = 1
    private val TYPE_BODY = 2

    lateinit var listHeader: View
    lateinit var itemClickListener: AdapterView.OnItemClickListener
    var activity = act

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemViewHolder {
        if(viewType == TYPE_HEADER){
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_list_header, parent, false)
        return StoreItemViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.store_item_adapter, parent, false)
            return StoreItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position > 0){
            holder as StoreItemViewHolder
            val item = itens[position]

            val name = item.nomeFantasia

            holder.name.text = name
            holder.rating.text = "4,5"
            holder.categoryDistance.text = "1,5"
            holder.deliveryTime.text = "45-60 min"
            holder.deliveryFree.text = "R$ 10,00"

            holder.card.setTag(position)
            holder.card.setOnClickListener(this)
        }
    }

    override fun getItemCount() = itens.size

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEADER;

        } else {
            return TYPE_BODY;
        }
    }

    class StoreHeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name = view.store_title
    }

    class StoreItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val card = view.card
        val name = view.name
        val rating = view.rating
        val categoryDistance = view.category_and_distance
        val deliveryTime = view.delivery_time
        val deliveryFree = view.delivery_free
    }

    override fun onClick(view: View?) {
        if(view is CardView){
            var position = view!!.tag as Int
            AllDeliveryApplication.store = itens[position]
            val manager: FragmentManager = activity.supportFragmentManager
            manager.beginTransaction()
            manager.commit {
                setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                replace(R.id.nav_host_fragment, StoreFragment::class.java, null )
                addToBackStack(null)
            }
        }
    }
}