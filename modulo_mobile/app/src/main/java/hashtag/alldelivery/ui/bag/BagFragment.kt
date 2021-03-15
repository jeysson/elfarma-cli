package hashtag.alldelivery.ui.bag

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.ADDRESS
import hashtag.alldelivery.AllDeliveryApplication.Companion.BAG_ORDER_ADDRESS_CHANGE
import hashtag.alldelivery.AllDeliveryApplication.Companion.Pedido
import hashtag.alldelivery.AllDeliveryApplication.Companion.STORE
import hashtag.alldelivery.MainActivity
import hashtag.alldelivery.R
import hashtag.alldelivery.core.utils.OnBackPressedListener
import hashtag.alldelivery.ui.address.DeliveryAddress
import hashtag.alldelivery.ui.paymentmethod.PaymentMethodFragment
import hashtag.alldelivery.ui.store.StoreFragment
import kotlinx.android.synthetic.main.bag_content_delivery.*
import kotlinx.android.synthetic.main.bag_content_details.*
import kotlinx.android.synthetic.main.bag_fragment.*
import kotlinx.android.synthetic.main.bag_fragment_toolbar.*
import kotlinx.android.synthetic.main.bag_payment_method_option.*
import kotlinx.android.synthetic.main.checkout_button.*
import java.text.NumberFormat
import java.util.*

class BagFragment : Fragment(), OnBackPressedListener, View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bag_fragment, container, false)
        content_list.layoutManager
        content_list.setHasFixedSize(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtil.setLightMode(activity)

        refreshAddress()
        //
        restaurant_name.text = STORE?.nomeFantasia
        delivery_time.text = "Hoje, ${STORE?.tempoMinimo} - ${STORE?.tempoMaximo} min"
        //
        val adapter = BagAdapter(this)
        adapter.addItems(Pedido?.itens!!)
        content_list.adapter = adapter
        //
        subtotal.text = NumberFormat.getCurrencyInstance(Locale(getString(R.string.language),
            getString(R.string.country))).format(Pedido?.itens?.sumByDouble { p-> p.price!! * p.quantity!! })
        delivery_fee_price.text = NumberFormat.getCurrencyInstance(Locale(getString(R.string.language),
            getString(R.string.country))).format(STORE?.taxaEntrega)
        total_price.text = NumberFormat.getCurrencyInstance(Locale(getString(R.string.language),
            getString(R.string.country))).format(Pedido?.itens?.sumByDouble {
                p-> p.price!! * p.quantity!!
            }!! + STORE?.taxaEntrega!!)
        //
        if(Pedido?.paymentMethod != null){
            action_button.text = getString(R.string.bag_confirm_order_go)
            action.text = getString(R.string.bag_payment_change)
            description.text = when(Pedido?.paymentMethod?.tipo){
                0 -> Pedido?.paymentMethod?.nome
                1 -> "Débito - ${Pedido?.paymentMethod?.nome} (máquina)"
                2 -> "Crédito - ${Pedido?.paymentMethod?.nome} (máquina)"
                else -> ""
            }
            description.visibility = VISIBLE
        }
        //
        btChangeAddress.setOnClickListener{
            val intent = Intent(context, DeliveryAddress::class.java)
            startActivityForResult(intent, BAG_ORDER_ADDRESS_CHANGE)
        }
        //
        add_more_items.setOnClickListener {
            val manager: FragmentManager = activity!!.supportFragmentManager
            manager.beginTransaction()
            manager.commit {
                setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )

                addToBackStack(null)
                replace(R.id.nav_host_fragment, StoreFragment::class.java, null)
            }
        }
        //
        btChangePayment.setOnClickListener{
            selectPayment()
        }
        //
        back_button.setOnClickListener {
            back()
        }

        action_button.setOnClickListener(this)

        (activity as MainActivity).hideBag()
        (activity as MainActivity).hideBottomNavigation()
    }

    private fun back(){
        StatusBarUtil.setLightMode(activity)
        activity!!.supportFragmentManager.popBackStack()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()

        (activity as MainActivity).showBottomNavigation()
        (activity as MainActivity).showBag()
    }

    override fun onBackPressed() {
        back()
    }

    override fun onClick(v: View?) {
        if(Pedido?.paymentMethod != null)
            BagConfirmOrderDialog().show(activity?.supportFragmentManager!!, "")
            else
        selectPayment()
    }

    fun selectPayment(){
        val manager: FragmentManager = activity!!.supportFragmentManager
        manager.beginTransaction()
        manager.commit {
            setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.enter_from_right,
                R.anim.exit_to_left
            )

            addToBackStack(null)
            replace(R.id.nav_host_fragment, PaymentMethodFragment::class.java, null)

        }
    }

    fun refreshAddress(){
        address_title.text =  "${Pedido?.address?.address}, ${Pedido?.address?.number}"
        address_description.text = if(Pedido?.address?.complement == "")
                                        Pedido?.address?.neighborhood
                                 else
                                     "${Pedido?.address?.complement}, ${Pedido?.address?.neighborhood}"
    }

    override fun onResume() {
        super.onResume()
        refreshAddress()
    }
}