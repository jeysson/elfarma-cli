package hashtag.alldelivery.ui.order

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.ADDRESS
import hashtag.alldelivery.AllDeliveryApplication.Companion.BAG_ORDER_ADDRESS_CHANGE
import hashtag.alldelivery.AllDeliveryApplication.Companion.Pedido
import hashtag.alldelivery.AllDeliveryApplication.Companion.PedidoHistory
import hashtag.alldelivery.AllDeliveryApplication.Companion.STORE
import hashtag.alldelivery.AllDeliveryApplication.Companion.changeFragment
import hashtag.alldelivery.MainActivity
import hashtag.alldelivery.R
import hashtag.alldelivery.core.utils.OnBackPressedListener
import hashtag.alldelivery.ui.address.DeliveryAddress
import hashtag.alldelivery.ui.bag.BagAdapter
import hashtag.alldelivery.ui.paymentmethod.PaymentMethodFragment
import hashtag.alldelivery.ui.store.StoreFragment
import kotlinx.android.synthetic.main.bag_content_delivery.*
import kotlinx.android.synthetic.main.bag_content_details.*
import kotlinx.android.synthetic.main.bag_fragment.*
import kotlinx.android.synthetic.main.bag_fragment.add_more_items
import kotlinx.android.synthetic.main.bag_fragment.content_list
import kotlinx.android.synthetic.main.bag_fragment.delivery_time
import kotlinx.android.synthetic.main.bag_fragment_toolbar.*
import kotlinx.android.synthetic.main.bag_payment_method_option.*
import kotlinx.android.synthetic.main.checkout_button.*
import kotlinx.android.synthetic.main.order_one_history_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread

class OrderOneHistoryFragment : Fragment(), OnBackPressedListener {

    private val orderViewModel: OrderViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_one_history_fragment, container, false)
        content_list.layoutManager
        content_list.setHasFixedSize(true)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtil.setLightMode(activity)
        title.text = "Pedido"
        //
        add_more_items.visibility = View.GONE
        //
        btChangePayment.visibility = View.GONE
        //
        back_button.setOnClickListener {
            back()
        }

        clear_bag.visibility = View.INVISIBLE

        action_button.visibility = View.GONE

        action.visibility = View.INVISIBLE

        minimum_price_alert.visibility = View.GONE

        setObservers()

        thread(true) {
            orderViewModel.getOrder(PedidoHistory?.id!!)
        }

        (activity as MainActivity).hideBag()
        (activity as MainActivity).hideBottomNavigation()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setObservers(){
       orderViewModel.eventOrder.observe(viewLifecycleOwner){

           val data = it?.date?.toInstant()
               ?.atZone(ZoneId.systemDefault())
               ?.toLocalDateTime()

           val formatter = DateTimeFormatter.ofPattern("E, dd 'de' MMMM 'às' H:m", Locale(getString(R.string.language),
               getString(R.string.country))
           )

           var locale = Locale(getString(R.string.language),
               getString(R.string.country))
           //
           store_name.text = it.store?.nomeFantasia
           order_number.text = String.format(getString(R.string.order_list_history_number),
               it?.id!!.toString().padStart(6, '0'))
           delivery_time.text = data?.format(formatter)
           //
           val adapter = OrderOneHistoryAdapter(this)
           adapter.addItems(it?.itens!!)
           content_list.adapter = adapter
           //
           subtotal.text = NumberFormat.getCurrencyInstance(locale).format(it?.itens?.sumByDouble { p-> p.price!! * p.quantity!! })
           delivery_fee_price.text = NumberFormat.getCurrencyInstance(locale).format(it.store?.taxaEntrega)
           total_price.text = NumberFormat.getCurrencyInstance(locale).format(it?.itens?.sumByDouble {
                   p-> p.price!! * p.quantity!!
           }!! + it.store?.taxaEntrega!!)
           //
           if(it?.paymentMethod != null){
               action_button.text = getString(R.string.bag_confirm_order_go)
               action.text = getString(R.string.bag_payment_change)
               description.text = when(it?.paymentMethod?.tipo){
                   0 -> it?.paymentMethod?.nome
                   1 -> "Débito - ${it?.paymentMethod?.nome} (máquina)"
                   2 -> "Crédito - ${it?.paymentMethod?.nome} (máquina)"
                   else -> ""
               }
               description.visibility = VISIBLE
           }
           //
           //
           if(STORE?.pedidoMinimo != null && STORE?.pedidoMinimo!! > 0) {
               minimum_price_alert.text = String.format(locale,
                   resources.getString(R.string.payment_minimum_price),
                   STORE?.pedidoMinimo
               )
           }else
               minimum_price_alert.visibility = View.INVISIBLE

           //
           address_title.text =  "${it?.address?.address}, ${it?.address?.number}"
           address_description.text = if(it?.address?.complement == "")
               it?.address?.neighborhood
           else
               "${it?.address?.complement}, ${it?.address?.neighborhood}"
       }
    }

    private fun back(){
        StatusBarUtil.setLightMode(activity)
        activity!!.supportFragmentManager.popBackStack()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()

       // (activity as MainActivity).showBottomNavigation()
       // (activity as MainActivity).showBag()
    }

    override fun onBackPressed() {
        back()
    }
}