package hashtag.alldelivery.ui.order

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.OrderEvaluated
import hashtag.alldelivery.AllDeliveryApplication.Companion.PedidoHistory
import hashtag.alldelivery.MainActivity
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Order
import hashtag.alldelivery.core.models.OrderHistory
import hashtag.alldelivery.core.utils.OnBackPressedListener
import kotlinx.android.synthetic.main.filter_activity.*
import kotlinx.android.synthetic.main.order_card_content.*
import kotlinx.android.synthetic.main.order_card_notification_view.*
import kotlinx.android.synthetic.main.order_delivery_address.*
import kotlinx.android.synthetic.main.order_detail.*
import kotlinx.android.synthetic.main.order_detail.store_image
import kotlinx.android.synthetic.main.order_detail.store_name
import kotlinx.android.synthetic.main.order_estimated_delivery_time.*
import kotlinx.android.synthetic.main.order_evaluate.*
import kotlinx.android.synthetic.main.order_evaluate_item_row.*
import kotlinx.android.synthetic.main.order_fragment.*
import kotlinx.android.synthetic.main.order_progress_bar.*
import kotlinx.android.synthetic.main.order_simple_toolbar.*
import kotlinx.android.synthetic.main.payment_method_fragment.*
import org.jetbrains.anko.doAsync
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread


class OrderFragment: Fragment(), OnBackPressedListener, OnClickListener {

    private lateinit var timerorder: Timer
    private val orderViewModel: OrderViewModel by sharedViewModel()
    private var order: Order? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.order_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.setLightMode(activity)

        loading_view.visibility = VISIBLE

        confirmed.setProgress(100)
        confirmed.setAnimate(true)

        cooking.setProgress(100)
        cooking.setAnimate(true)
        cooking.visibility = GONE

        traveling.setProgress(100)
        traveling.setAnimate(true)
        traveling.visibility = GONE

        completed.setProgress(100)
        completed.setAnimate(true)
        completed.visibility = GONE

        setupObservers()
        //
        back_button.setOnClickListener {
            back()
        }

        status_container.setOnClickListener {
            status_container.setOnClickListener {
                if (liststatus.visibility === VISIBLE) {

                    // The transition of the hiddenView is carried out
                    //  by the TransitionManager class.
                    // Here we use an object of the AutoTransition
                    // Class to create a default transition.
                    TransitionManager.beginDelayedTransition(
                        linearLayoutStatus,
                        AutoTransition()
                    )
                    liststatus.visibility = GONE
                    arrow_status.rotation = 180f
                } else {
                    TransitionManager.beginDelayedTransition(
                        linearLayoutStatus,
                        AutoTransition()
                    )
                    liststatus.visibility = VISIBLE
                    arrow_status.rotation = 0f
                }
            }
        }

        merchant_call_layout.setOnClickListener {
            call()
        }

        timerorder = fixedRateTimer("getorder", true, 10000, 15000){
            timerGetOrder()
        }

        orderViewModel.adapterEvaluate = OrderEvaluateAdapter(this)
        recycler_evaluate_order.adapter = orderViewModel.adapterEvaluate

        orderViewModel.getOrdersWaitingEvaluate(AllDeliveryApplication.USER?.id!!)

        waiting_order_info_click_area.setOnClickListener {
            val manager: FragmentManager = activity!!.supportFragmentManager
            manager.beginTransaction()
            manager.commit(true) {
                setCustomAnimations(
                    R.anim.enter_from_up,
                    R.anim.exit_to_down,
                    R.anim.enter_from_down,
                    R.anim.exit_to_up
                )

                addToBackStack(null)

                replace(R.id.nav_host_fragment, OrderOneHistoryFragment::class.java, null)
            }
        }


        (activity as MainActivity).hideBag()
        (activity as MainActivity).hideBottomNavigation()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupObservers(){

        orderViewModel.eventOrders.observe(viewLifecycleOwner) {
            if (it.size > 0)
                order_evaluate.visibility = VISIBLE
        }

        orderViewModel.eventOrder.observe(viewLifecycleOwner){
            order = it
            PedidoHistory = OrderHistory()
            PedidoHistory?.id = order?.id
            //
            if(it.itens?.size!! > 0) {
                loadData()
            }
            else {
                thread(true) {
                    orderViewModel.getOrder(it?.id!!)

                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadData(){

        loading_view.visibility = GONE

        val formatter = DateTimeFormatter.ofPattern(
            "HH:mm", Locale(
                getString(R.string.language),
                getString(R.string.country)
            )
        )
        val tempoMinimo = order?.date?.toInstant()
                                     ?.atZone(ZoneId.systemDefault())
                                     ?.toLocalDateTime()
                                     ?.plusMinutes(order?.store?.tempoMinimo!!.toLong())
        val tempoMaximo = order?.date?.toInstant()
                                    ?.atZone(ZoneId.systemDefault())
                                    ?.toLocalDateTime()
                                    ?.plusMinutes(order?.store?.tempoMaximo!!.toLong())

        estimated_time_countdown.text = "${tempoMinimo?.format(formatter)} - ${tempoMaximo?.format(
            formatter
        )}"

        for (i in 0..7){
            if(order?.listStatus?.size!!-1 < i)
                break;
            when(i){
                0-> last_status.text = order?.listStatus?.get(i)?.descricao
                1-> {
                    status1.visibility = VISIBLE
                    status1.text = order?.listStatus?.get(i)?.descricao
                }
                2-> {
                    status2.visibility = VISIBLE
                    status2.text = order?.listStatus?.get(i)?.descricao
                    confirmed.setAnimate(false)
                    confirmed.setProgress(100)
                    cooking.visibility = VISIBLE

                }
                3-> {
                    status3.visibility = VISIBLE
                    status3.text = order?.listStatus?.get(i)?.descricao

                    cooking.setAnimate(false)
                    cooking.setProgress(100)
                    traveling.visibility = VISIBLE
                }
                4-> {
                    status4.visibility = VISIBLE
                    status4.text = order?.listStatus?.get(i)?.descricao

                    traveling.setAnimate(false)
                    traveling.setProgress(100)
                    completed.visibility = VISIBLE
                }
                5-> {
                    status5.visibility = VISIBLE
                    status5.text = order?.listStatus?.get(i)?.descricao
                }
                6-> {
                    status6.visibility = VISIBLE
                    status6.text = order?.listStatus?.get(i)?.descricao
                }
            }
        }


        address_title.text = if(order?.address?.complement != "")
            "${order?.address?.address}, ${order?.address?.number}, ${order?.address?.complement}"
        else
            "${order?.address?.address}, ${order?.address?.number}"
        address_description.text = "${order?.address?.neighborhood} - ${order?.address?.city}, ${order?.address?.state}"
        //
        if(order?.store?.logo != null) {
            val imageBytes = android.util.Base64.decode(order?.store?.logo, 0)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val drawable = BitmapDrawable(resources, image)
            store_image.setImageDrawable(drawable)
        }
        store_name.text = order?.store?.nomeFantasia
        order_number.text = "#${order?.id.toString().padStart(6, '0')}"
        payment_method_title.text = "Pagamento na entrega"
        //
        if(order?.paymentMethod?.imagem != null) {
            val imageBytes2 = android.util.Base64.decode(order?.paymentMethod?.imagem, 0)
            val image2 = BitmapFactory.decodeByteArray(imageBytes2, 0, imageBytes2.size)
            val drawable2 = BitmapDrawable(resources, image2)
            payment_method_icon.setImageDrawable(drawable2)
        }
        payment_method_name.text = order?.paymentMethod?.nome

        total.text = NumberFormat.getCurrencyInstance(
            Locale(
                getString(R.string.language),
                getString(R.string.country)
            )
        ).format(order?.itens?.sumByDouble { p -> p.price!! * p.quantity!! }
            ?.plus(order?.store?.taxaEntrega!!))
    }

    fun timerGetOrder(){
        orderViewModel.getOrder(order?.id!!)
    }

    fun call() {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + order?.store?.telefoneCelular)
        startActivity(dialIntent)
    }

    private fun back(){
        timerorder.cancel()
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
        when(v?.id){
            R.id.card_view->{
                OrderEvaluated = orderViewModel.adapterEvaluate?.itens?.get(v.tag as Int)
                openEvaluate()
            }
        }
    }

    fun openEvaluate(){
        timerorder.cancel()

        val manager: FragmentManager = activity!!.supportFragmentManager
        manager.beginTransaction()
        manager.commit(true) {
            setCustomAnimations(
                R.anim.enter_from_up,
                R.anim.exit_to_down,
                R.anim.enter_from_down,
                R.anim.exit_to_up
            )

            addToBackStack(null)

            replace(R.id.nav_host_fragment, OrderEvaluateFragment::class.java, null)
        }
    }
}