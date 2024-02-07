package hashtag.elfarma.ui.order

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import com.jaeger.library.StatusBarUtil
import hashtag.elfarma.AllDeliveryApplication.Companion.OrderEvaluated
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.core.models.Evaluated
import hashtag.elfarma.core.models.Order
import hashtag.elfarma.core.utils.OnBackPressedListener
import kotlinx.android.synthetic.main.order_evaluate_fragment.*
import kotlinx.android.synthetic.main.order_simple_toolbar.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class OrderEvaluateFragment : Fragment(), OnBackPressedListener {

    private val orderViewModel: OrderViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_evaluate_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtil.setLightMode(activity)
        help.visibility = View.INVISIBLE

        title.text = "Avaliação do Pedido"
        order_number.text = String.format(getString(R.string.order_list_history_number),
            OrderEvaluated?.id!!.toString().padStart(6, '0'))
        tx_comment.setSelection(0)
        tx_comment.doOnTextChanged { text, start, count, after ->
            lb_total_comment.text = text.toString().length.toString()+"/300"
        }
        back_button.setOnClickListener {
            back()
        }

        action_button.setOnClickListener {
            var oe = Order()
            oe.id = OrderEvaluated?.id

            oe?.evaluateds = ArrayList<Evaluated>()
            val evaluatedPackage  = Evaluated()
            evaluatedPackage.tipo = 1
            evaluatedPackage.notaLoja = criteria_package_rating.rating
            evaluatedPackage.pedido = Order()
            evaluatedPackage.pedido?.id = oe.id
            oe?.evaluateds!!.add(evaluatedPackage)
            //
            val evaluatedProduct  = Evaluated()
            evaluatedProduct.tipo = 2
            evaluatedProduct.notaLoja = criteria_product_rating.rating
            evaluatedProduct.pedido = Order()
            evaluatedProduct.pedido?.id = oe.id
            oe?.evaluateds!!.add(evaluatedProduct)
            //
            val evaluatedDelivery  = Evaluated()
            evaluatedDelivery.tipo = 3
            evaluatedDelivery.notaLoja = criteria_delivery_rating.rating
            evaluatedDelivery.pedido = Order()
            evaluatedDelivery.pedido?.id = oe.id
            oe?.evaluateds!!.add(evaluatedDelivery)
            //
            val evaluatedCost  = Evaluated()
            evaluatedCost.tipo = 4
            evaluatedCost.notaLoja = criteria_cost_rating.rating
            evaluatedCost.pedido = Order()
            evaluatedCost.pedido?.id = oe.id
            oe?.evaluateds!!.add(evaluatedCost)

            oe.comment = tx_comment.text.toString()
            orderViewModel.saveEvaluate(oe!!)
        }

        setupObservers()

        (activity as MainActivity).hideBag()
        (activity as MainActivity).hideBottomNavigation()
    }

    fun setupObservers() {
        orderViewModel.eventOrder.observe(viewLifecycleOwner) {
            back()
        }
    }

    private fun back(){
        OrderEvaluated = null
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