package hashtag.alldelivery.ui.order

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.Pedido
import hashtag.alldelivery.AllDeliveryApplication.Companion.STORE
import hashtag.alldelivery.MainActivity
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Order
import hashtag.alldelivery.core.utils.OnBackPressedListener
import kotlinx.android.synthetic.main.order_delivery_address.*
import kotlinx.android.synthetic.main.order_detail.*
import kotlinx.android.synthetic.main.order_estimated_delivery_time.*
import kotlinx.android.synthetic.main.order_fragment.*
import kotlinx.android.synthetic.main.order_progress_bar.*
import kotlinx.android.synthetic.main.order_simple_toolbar.*
import kotlinx.android.synthetic.main.payment_method_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread

class OrderFragment: Fragment(), OnBackPressedListener {

    private val orderViewModel: OrderViewModel by sharedViewModel()

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
        //
        loading_view.visibility = VISIBLE

        //confirmed.progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN)
        confirmed.setProgress(100)
        confirmed.setAnimate(true)

        cooking.setProgress(0)
        cooking.setAnimate(true)
        cooking.visibility = GONE

        traveling.setProgress(0)
        traveling.setAnimate(true)
        traveling.visibility = GONE

        completed.setProgress(0)
        completed.setAnimate(true)
        completed.visibility = GONE

        setupObservers()
        //
        thread(true){
            orderViewModel.checkoutOrder(Pedido!!)
        }
        //
        back_button.setOnClickListener {
            back()
        }

        (activity as MainActivity).select(R.id.navigation_requests)
        (activity as MainActivity).showBottomNavigation()
/*
        thread(true){
            loading.visibility = View.VISIBLE
            viewModelPaymentMethod.getPaymentMethods(AllDeliveryApplication.STORE?.id!!)
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupObservers(){
        orderViewModel.eventOrder.observe(viewLifecycleOwner){
            loadData(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadData(order: Order){

        loading_view.visibility = GONE

        val formatter = DateTimeFormatter.ofPattern("H:m", Locale(getString(R.string.language),
            getString(R.string.country))
        )
        val tempoMinimo = order?.date?.toInstant()
                                     ?.atZone(ZoneId.systemDefault())
                                     ?.toLocalDateTime()
                                     ?.plusMinutes(order.store?.tempoMinimo!!.toLong())
        val tempoMaximo = order.date?.toInstant()
                                    ?.atZone(ZoneId.systemDefault())
                                    ?.toLocalDateTime()
                                    ?.plusMinutes(STORE?.tempoMaximo!!.toLong())

        estimated_time_countdown.text = "${tempoMinimo?.format(formatter)} - ${tempoMaximo?.format(formatter)}"

        // topbar_title.text = getString(R.string.payment_method_title)
        //
        address_title.text = if(order?.address?.complement != "")
            "${order?.address?.address}, ${order?.address?.number}, ${order?.address?.complement}"
        else
            "${order?.address?.address}, ${order?.address?.number}"
        address_description.text = "${order?.address?.neighborhood} - ${order?.address?.city}, ${order?.address?.state}"
        //
        if(order?.store?.logo != null) {
            val imageBytes = android.util.Base64.decode(Pedido?.store?.logo, 0)
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

        total.text = NumberFormat.getCurrencyInstance(Locale(getString(R.string.language),
            getString(R.string.country))).format(order?.itens?.sumByDouble { p-> p.price!! * p.quantity!! })
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
}