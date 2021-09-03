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
import kotlinx.android.synthetic.main.bag_confirm_order.action_button
import kotlinx.android.synthetic.main.bag_confirm_order.warning
import kotlinx.android.synthetic.main.bag_minimum_order.*
import java.text.NumberFormat
import java.util.*

class BagMinimumOrderDialog: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bag_minimum_order, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var locale = Locale(getString(R.string.language),
            getString(R.string.country))

        warning.text = String.format(locale,
            resources.getString(R.string.payment_minimum_price),
            NumberFormat.getCurrencyInstance(locale).format(Pedido?.store?.pedidoMinimo)
        )

        action_button.setOnClickListener {

            dismiss()
        }
    }
}