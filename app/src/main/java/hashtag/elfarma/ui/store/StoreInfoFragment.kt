package hashtag.elfarma.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jaeger.library.StatusBarUtil
import hashtag.elfarma.AllDeliveryApplication.Companion.STORE
import hashtag.elfarma.R
import hashtag.elfarma.core.utils.OnBackPressedListener
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.store_card_toolbar.back_button
import java.text.NumberFormat
import java.util.*

class StoreInfoFragment : Fragment(), OnBackPressedListener {

    //private lateinit var _viewModel: StoreViewModel
    private lateinit var _viewModel: StoresViewModel
    private lateinit var _view: View
    private val storeName by lazy { _view.findViewById<TextView>(R.id.txt_store_name_info_fragment) }
    private val storeDescription by lazy { _view.findViewById<TextView>(R.id.txt_store_description_info_fragment) }
    private val deliveryTime by lazy { _view.findViewById<TextView>(R.id.txt_delivery_time_info_fragment) }
    private val deliveryDefaultTimer by lazy { _view.findViewById<TextView>(R.id.txt_timer_info_fragment) }
    private val deliveryFee by lazy { _view.findViewById<TextView>(R.id.txt_delivery_fee_info_fragment) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.store_info_fragment, container, false)
        _view = view
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        StatusBarUtil.setLightMode(activity)

//        _viewModel = ViewModelProviders.of(this).get(StoreViewModel::class.java)

        topbar_title.text = getString(R.string.store_information)

//            transforma em valor em moeda e verifica se é 0 ou nulo
        var storeFee = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(STORE?.taxaEntrega)
        if (STORE?.taxaEntrega == 0f || STORE?.taxaEntrega == null) {
            deliveryFee.setTextColor(resources.getColor(R.color.green_free_item))
            storeFee = "Gratis"
        }

        storeName.text = STORE?.nomeFantasia
        storeDescription.text = STORE?.descricao
        deliveryTime.text = "${returnHour(STORE?.hAbre)} às ${returnHour(STORE?.hFecha)}"
        deliveryDefaultTimer.text = "${STORE?.tempoMinimo} - ${STORE?.tempoMaximo} min"
        deliveryFee.text = "$storeFee"
        back_button.setOnClickListener {
            back()
        }

    }

    fun returnHour (number: Int?): String {
        var newString = number.toString()
        if (newString.length == 3) {
            newString = "0$newString"
        }

        newString = "${newString.substring(0, 2)}:${newString.substring(2, 4)}"

        return newString
    }


    companion object {
        fun newInstance() = StoreFragment()
    }


    private fun back(){
//        StatusBarUtil.setLightMode(activity)
        activity!!.supportFragmentManager.popBackStack()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()
    }

    override fun onBackPressed() {
        back()
    }
}