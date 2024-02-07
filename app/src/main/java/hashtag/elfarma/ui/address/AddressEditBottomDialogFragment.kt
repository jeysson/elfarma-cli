package hashtag.elfarma.ui.address

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.R
import hashtag.elfarma.core.models.Address
import kotlinx.android.synthetic.main.address_edit_bottom_dialog.*

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ItemListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class AddressEditBottomDialogFragment : BottomSheetDialogFragment() {

    var address: Address? = null
    var addressViewModel: AddressViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.address_edit_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)

        Binding()

        delete_address_button.setOnClickListener {
            address?.let { add -> addressViewModel!!.delete(add) }
            this.dismiss()
        }

        edit_address_button.setOnClickListener {
            AllDeliveryApplication.EDIT    = true;
            AllDeliveryApplication.LAT_LONG = LatLng(this.address!!.lat!!, this.address!!.longi!!)
            AllDeliveryApplication.ADDRESS = this.address
            val intent = Intent(this.context, DetailAddress::class.java)
            // start your next activity
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this.activity).toBundle())
            this.dismiss()
        }

        cancel_button.setOnClickListener {
            this.dismiss()
        }
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int): AddressEditBottomDialogFragment =
            AddressEditBottomDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }

    }

    private fun Binding(){
        title_dialog.text = address!!.address+", "+address!!.number
        complement_address.text = address!!.neighborhood+", "+address!!.city + " - " +address!!.state
    }
}