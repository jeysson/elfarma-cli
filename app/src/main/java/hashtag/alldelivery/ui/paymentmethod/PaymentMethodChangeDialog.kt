package hashtag.alldelivery.ui.paymentmethod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.Pedido
import hashtag.alldelivery.AllDeliveryApplication.Companion.STORE
import hashtag.alldelivery.R
import kotlinx.android.synthetic.main.fragment_item_list_dialog_list_dialog_item.*
import kotlinx.android.synthetic.main.payment_method_change_dialog.*
import java.text.NumberFormat
import java.util.*

class PaymentMethodChangeDialog: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.payment_method_change_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val locale = Locale(getString(R.string.language),
            getString(R.string.country))
        editvlrtroco.doOnTextChanged { text, start, count, after ->
            if(text.toString().length > 0 &&
                editvlrtroco.text.toString().replace(",", ".").toDouble() > 0)
            bttroco.isEnabled = true
            else
                bttroco.isEnabled = false
        }

        description.text = String.format(locale,
            resources.getString(R.string.money_change_dialog_step_two_description),
            NumberFormat.getCurrencyInstance(locale).format(Pedido?.itens?.sumByDouble {
                    p-> p.price!! * p.quantity!!
            }!! + STORE?.taxaEntrega!!)
        )

        bttroco.setOnClickListener {
            AllDeliveryApplication.Pedido?.vlrtroco = editvlrtroco.text.toString().replace(",", ".").toDouble()
            dismiss()
            back()
        }

        no_change_button.setOnClickListener {
            dismiss()
            back()
        }
    }

    private fun back(){
        activity!!.supportFragmentManager.popBackStack()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()
    }
}