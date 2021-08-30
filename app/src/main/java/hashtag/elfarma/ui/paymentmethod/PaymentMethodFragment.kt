package hashtag.elfarma.ui.paymentmethod

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.jaeger.library.StatusBarUtil
import hashtag.elfarma.AllDeliveryApplication.Companion.Pedido
import hashtag.elfarma.AllDeliveryApplication.Companion.STORE
import hashtag.elfarma.R
import hashtag.elfarma.core.models.PaymentMethod
import hashtag.elfarma.core.utils.OnBackPressedListener
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.payment_method_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.concurrent.thread

class PaymentMethodFragment : Fragment(), OnBackPressedListener, View.OnClickListener {
    private lateinit var adapt: PaymentMethodAdapter

    // TODO: Rename and change types of parameters
    val viewModelPaymentMethod: PaymentMethodViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.payment_method_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.setDarkMode(activity)
        topbar_title.text = getString(R.string.payment_method_title)

        back_button.setOnClickListener {
            back()
        }

        setupObservers()

        thread(true){
            loading.visibility = VISIBLE
            viewModelPaymentMethod.getPaymentMethods(STORE?.id!!)
        }
    }

    fun setupObservers() {
        viewModelPaymentMethod.loadPayment.observe(viewLifecycleOwner) {
            val arr = ArrayList<PaymentMethod>()
            for (grp in it.sortedBy { p -> p.tipo }.groupBy { p -> p.tipo }) {
                if (grp.key == 0)
                    arr.addAll(grp.value)
                else if (grp.key == 1) {
                    val o = PaymentMethod()
                    o.header = true
                    o.nome = "Débito"
                    arr.add(o)
                    arr.addAll(grp.value.sortedBy { p -> p.nome })
                } else if (grp.key == 2) {
                    val o = PaymentMethod()
                    o.header = true
                    o.nome = "Crédito"
                    arr.add(o)
                    arr.addAll(grp.value.sortedBy { p -> p.nome })
                }
            }

            adapt = PaymentMethodAdapter(this)
            payment_list.adapter = adapt
            adapt.addItens(arr)
            adapt.notifyDataSetChanged()
            loading.visibility = GONE
        }
    }

    override fun onBackPressed() {
        back()
    }

    private fun back(){
        StatusBarUtil.setLightMode(activity)
        activity!!.supportFragmentManager.popBackStack()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()
    }

    override fun onClick(v: View?) {

        adapt.notifyDataSetChanged()

        var position = v!!.tag as Int

        v?.setBackgroundResource(R.drawable.bg_payment_selected)

        if(adapt.itens?.get(position)?.troco!!)
        {
            Pedido?.paymentMethod = adapt.itens?.get(position)
            var modal = PaymentMethodChangeDialog()
            modal.show(activity?.supportFragmentManager!!, "")
        }
        else{
            Pedido?.paymentMethod = adapt.itens?.get(position)
            Pedido?.vlrtroco = null
            back()
        }
    }

}