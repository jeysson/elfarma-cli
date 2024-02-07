package hashtag.elfarma.ui.bag

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hashtag.elfarma.AllDeliveryApplication.Companion.Pedido
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.component.ButtonMinusPlus
import hashtag.elfarma.core.models.Product
import kotlinx.android.synthetic.main.bag_confirm_order.*

class BagChangeOrderDialog: BottomSheetDialogFragment() {

    lateinit var product: Product
    var quantity: Int = 0
    lateinit var obj: ButtonMinusPlus

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bag_change_order, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dismiss_button.setOnClickListener {
            obj.total = 0
            dismiss()
        }
        //
        action_button.setOnClickListener {
            Pedido = null
            (activity as MainActivity).changeValueBag(null, product, quantity)
            dismiss()
        }
    }
}