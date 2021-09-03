package hashtag.elfarma.ui.order

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
import androidx.recyclerview.widget.RecyclerView
import hashtag.elfarma.AllDeliveryApplication.Companion.OrderEvaluated
import hashtag.elfarma.AllDeliveryApplication.Companion.Pedido
import hashtag.elfarma.AllDeliveryApplication.Companion.PedidoHistory
import hashtag.elfarma.AllDeliveryApplication.Companion.SENDORDER
import hashtag.elfarma.AllDeliveryApplication.Companion.USER
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.core.models.Order
import hashtag.elfarma.core.models.OrderHistory
import kotlinx.android.synthetic.main.order_history_fragment.*
import kotlinx.android.synthetic.main.order_history_fragment.list
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.concurrent.thread

class OrderHistoryFragment : Fragment(), View.OnClickListener {

    private val orderViewModel: OrderViewModel by sharedViewModel()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page = 1
    var itemsPerPage = 6

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

        list.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val recyclerLayout = (list.layoutManager as LinearLayoutManager)

                if (!isLoading && !isLastPage) {
                    if (recyclerLayout.findLastCompletelyVisibleItemPosition() == (itemsPerPage * page) - 1) {
                        isLoading = true
                        loading.visibility = View.VISIBLE
                        page += 1

                        getMoreItems()
                    }
                }
            }
        })


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
        orderViewModel.getOrderHistory(USER?.id!!, page, itemsPerPage)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMoreItems(filter: String? = null) {

        orderViewModel.getOrderHistory(USER?.id!!, page, itemsPerPage)

    }

}