package hashtag.elfarma.ui.bag

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hashtag.elfarma.AllDeliveryApplication.Companion.ADDRESS
import hashtag.elfarma.AllDeliveryApplication.Companion.Pedido
import hashtag.elfarma.AllDeliveryApplication.Companion.SENDORDER
import hashtag.elfarma.AllDeliveryApplication.Companion.STORE
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.ui.order.OrderViewModel
import kotlinx.android.synthetic.main.bag_confirm_order.*
import kotlinx.android.synthetic.main.bag_confirm_order_address.*
import kotlinx.android.synthetic.main.bag_confirm_order_payment.*
import kotlinx.android.synthetic.main.bag_confirm_order_time.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread

class BagConfirmOrderDialog: BottomSheetDialogFragment() {

    private val orderViewModel: OrderViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bag_confirm_order, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduling_time.text = "Hoje, ${Pedido?.store?.tempoMinimo} - ${Pedido?.store?.tempoMaximo} min"
        //
        val formatter = DateTimeFormatter.ofPattern(
            "E", Locale(
                getString(R.string.language),
                getString(R.string.country)
            )
        )

        day_of_week.text = LocalDate.now().format(formatter)
        day_of_month.text = LocalDate.now().dayOfMonth.toString()
        //
        address_title.text = "${Pedido?.address?.address}, ${Pedido?.address?.number}"
        address_description.text = if (Pedido?.address?.complement == "")
            Pedido?.address?.neighborhood
        else
            "${Pedido?.address?.complement}, ${Pedido?.address?.neighborhood}"
        //
        val imageBytes = android.util.Base64.decode(Pedido?.paymentMethod?.imagem, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val drawableImage = BitmapDrawable(resources, image)
        payment_type_icon.setImageDrawable(drawableImage)
        payment_description.text = when (Pedido?.paymentMethod?.tipo) {
            0 -> Pedido?.paymentMethod?.nome
            1 -> "Débito - ${Pedido?.paymentMethod?.nome}"
            2 -> "Crédito - ${Pedido?.paymentMethod?.nome}"
            else -> ""
        }
        //
        dismiss_button.setOnClickListener {
            dismiss()
        }
        //
        action_button.setOnClickListener {
            SENDORDER = true
            thread(true) {
                orderViewModel.checkoutOrder(Pedido!!)
                Pedido = null
            }

            (activity as MainActivity).select(R.id.navigation_orders)

            dismiss()
        }
    }

    fun setObservers(){
        orderViewModel.eventOrder.observe(viewLifecycleOwner){
           // (activity as MainActivity).select(R.id.navigation_orders)

          //  dismiss()
        }
    }
}