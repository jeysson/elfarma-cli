package hashtag.alldelivery.ui.address

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.ADDRESS
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Address
import kotlinx.android.synthetic.main.address_list_item.view.*


class AddressListAdapter internal constructor(activity: AppCompatActivity): RecyclerView.Adapter<AddressListAdapter.AddressItemViewHolder>(),
    View.OnClickListener {

    private val inflater: LayoutInflater = LayoutInflater.from(activity.baseContext)
    private var address = emptyList<Address>() // Cached copy of words
    private var activity = activity
    private lateinit var _view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressItemViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.address_list_item, parent, false)
        _view = view
        return AddressItemViewHolder(view)

    }

    override fun onBindViewHolder(holder: AddressItemViewHolder, position: Int) {


        val item = address?.get(position)

//        Muda a cor do cardView se for o endereço padrão
        if (item.id == ADDRESS?.id) {
            holder.cardView.strokeColor = Color.parseColor("#057DCD")
            holder.cardView.strokeWidth = holder.itemView.resources.getDimension(R.dimen.address_card_selected_bord_width).toInt()
        } else {
            holder.cardView.strokeColor = Color.parseColor("#A6A6A6")
            holder.cardView.strokeWidth = holder.itemView.resources.getDimension(R.dimen.address_card_default).toInt()
        }

        holder.title.text = item!!.address+", "+item.number
        holder.description.text = item.neighborhood+", "+item.city+" - "+item.state
        holder.description2.text = item!!.complement
        holder.btSelected.setTag(position)
        holder.btSelected.setOnClickListener(this)

        holder.itemView.setOnClickListener {
            AllDeliveryApplication.ADDRESS = address[position]
            AllDeliveryApplication.LAT_LONG = LatLng(address[position].lat!!,
                address[position].longi!!
            )
            activity.finish()
        }

    }

    override fun onClick(view: View?){
        if(view is ImageView){
            var position = view!!.tag as Int
            val manager: FragmentManager = activity.supportFragmentManager
            var d = AddressEditBottomDialogFragment()
            d.address = this.address[position]
            d.show(manager, d.tag)
        }
    }


    override fun getItemCount(): Int = address!!.size

    internal fun setAddress(address: List<Address>) {
        this.address = address
        notifyDataSetChanged()
    }

    inner class AddressItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title = view.title_address
        val description = view.description_address
        val description2 = view.second_description
        var btSelected = view.kebab
        val cardView = view.card_view_addess_item
    }

}