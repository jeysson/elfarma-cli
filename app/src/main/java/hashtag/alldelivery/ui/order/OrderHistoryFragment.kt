package hashtag.alldelivery.ui.order

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.OrderEvaluated
import hashtag.alldelivery.AllDeliveryApplication.Companion.Pedido
import hashtag.alldelivery.AllDeliveryApplication.Companion.PedidoHistory
import hashtag.alldelivery.AllDeliveryApplication.Companion.SENDORDER
import hashtag.alldelivery.AllDeliveryApplication.Companion.USER
import hashtag.alldelivery.MainActivity
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Order
import hashtag.alldelivery.core.models.OrderHistory
import hashtag.alldelivery.ui.paymentmethod.PaymentMethodFragment
import kotlinx.android.synthetic.main.order_history_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.concurrent.thread

class OrderHistoryFragment : Fragment(), View.OnClickListener {

    private val orderViewModel: OrderViewModel by sharedViewModel()
    private val TAM = 5
    private val page = 1

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
       val root = inflater.inflate(R.layout.order_history_fragment, container, false)

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        setObservers()

        (activity as MainActivity).showBottomNavigation()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        if(SENDORDER){
            SENDORDER = false
            changeView()
        }else{
            thread(true) {
                getItens()
            }
        }
    }

    fun setObservers(){
        orderViewModel.eventLoading.observe(viewLifecycleOwner){
            loading.visibility = if(it) VISIBLE else GONE
        }

        orderViewModel.eventOrder.observe(viewLifecycleOwner){
            Pedido = it
        }
    }

    fun initAdapter(){
        orderViewModel.adapter = OrderHistoryAdapter(
            this)
        //
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = orderViewModel.adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getItens(){
        orderViewModel.getOrderHistory(USER?.id!!, page, TAM)
    }

    public fun changeView() {
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
            replace(R.id.nav_host_fragment, OrderFragment::class.java, null)
        }
    }

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.details_button -> {
                var positions = v?.tag.toString().toInt()
                PedidoHistory = orderViewModel?.adapter?.itens?.get(positions) as OrderHistory?

                val manager: FragmentManager = activity!!.supportFragmentManager
                manager.beginTransaction()
                manager.commit(true) {
                    setCustomAnimations(
                        R.anim.enter_from_left,
                        R.anim.exit_to_right,
                        R.anim.enter_from_right,
                        R.anim.exit_to_left
                    )

                    addToBackStack(null)
                    if(PedidoHistory?.status?.id!! < 7){
                        orderViewModel.getOrder(PedidoHistory?.id!!)
                        replace(R.id.nav_host_fragment, OrderFragment::class.java, null)
                    }
                    else
                        replace(R.id.nav_host_fragment, OrderOneHistoryFragment::class.java, null)
                }
            }

            R.id.evaluated_button->{
                OrderEvaluated = Order()
                OrderEvaluated?.id = (orderViewModel?.adapter?.itens?.get(v.tag as Int) as OrderHistory)?.id
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

    }
}