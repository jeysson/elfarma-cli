package hashtag.alldelivery.ui.filter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hashtag.alldelivery.AllDeliveryApplication.Companion.RESULTS
import hashtag.alldelivery.AllDeliveryApplication.Companion.SORT_FILTER
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.EnumSortBy
import kotlinx.android.synthetic.main.filter_activity.*
import kotlinx.android.synthetic.main.filters_button.*


class FiltersActivity : AppCompatActivity() {

    private val orderAZIcon by lazy { img_order_a_z }
    private val orderAZCard by lazy { card_view_order_a_z }
    private val orderAZText by lazy { txt_order_a_z }
    private var isOrderAZChecked = false

    private val orderLocationIcon by lazy { img_order_location }
    private val orderLocationCard by lazy { card_view_order_location }
    private val orderLocationText by lazy { txt_order_location }
    private var isOrderLocationChecked = false

    private val orderTimerIcon by lazy { img_order_timer }
    private val orderTimerCard by lazy { card_view_order_timer }
    private val orderTimerText by lazy { txt_order_timer }
    private var isOrderTimerChecked = false

    private val orderDeliveryFeeIcon by lazy { img_order_delivery_fee }
    private val orderDeliveryFeeCard by lazy { card_view_order_delivery_fee }
    private val orderDeliveryFeeText by lazy { txt_order_delivery_fee }
    private var isOrderDeliveryFeeChecked = false

    private val clearButton by lazy { btn_limpar_filters }
    private val backButton by lazy { btn_back_filters }
    private val showResultsButton by lazy { btn_show_results_filters }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_activity)


//        Verifica o filtro que esta ativo
        when (SORT_FILTER) {
            EnumSortBy.SORT_BY_A_Z.ordinal -> {
                orderAZIsChecked()

            }
            EnumSortBy.SORT_BY_LOCALIZATION.ordinal -> {
                orderLocationIsChecked()
                clearButton.setTextColor(getColor(R.color.colorPrimary))
            }
            EnumSortBy.SORT_BY_TIMER.ordinal -> {
                orderTimerIsChecked()
                clearButton.setTextColor(getColor(R.color.colorPrimary))
            }
            EnumSortBy.SORT_BY_DELIVERY_FEE.ordinal -> {
                orderDeliveryFeeIsChecked()
                clearButton.setTextColor(getColor(R.color.colorPrimary))
            }
        }

        setStrokeCardColor(SORT_FILTER)

//        Seleciona qual tipo de ordem será aplicado no filtro
        orderAZCard.setOnClickListener {
            orderAZIsChecked()
            clearButton.setTextColor(getColor(R.color.medium_gray))
        }

        orderLocationCard.setOnClickListener {
            orderLocationIsChecked()
            clearButton.setTextColor(getColor(R.color.colorPrimary))
        }

        orderTimerCard.setOnClickListener {
            orderTimerIsChecked()
            clearButton.setTextColor(getColor(R.color.colorPrimary))
        }

        orderDeliveryFeeCard.setOnClickListener {
            orderDeliveryFeeIsChecked()
            clearButton.setTextColor(getColor(R.color.colorPrimary))

        }

        clearButton.setOnClickListener {
            clearFilters()
            orderAZIsChecked()
            clearButton.setTextColor(getColor(R.color.medium_gray))
        }

        backButton.setOnClickListener {
            finish()
        }

        showResultsButton.setOnClickListener {

//            Deve encerrar activity atual e retornar true -> Carregar nova lista de lojas
            val returnIntent = Intent()
            returnIntent.putExtra(RESULTS, true)
            setResult(RESULT_OK, returnIntent)
            finish()

        }

    }

//    Deve mudar a cor da borda de cada card, de acordo com o valor
    private fun setStrokeCardColor(filterNumber: Int){
        orderAZCard.strokeColor = getColor(R.color.medium_gray)
        orderLocationCard.strokeColor = getColor(R.color.medium_gray)
        orderTimerCard.strokeColor = getColor(R.color.medium_gray)
        orderDeliveryFeeCard.strokeColor = getColor(R.color.medium_gray)

        when (filterNumber){
            0 -> orderAZCard.strokeColor = getColor(R.color.colorPrimary)
            1 -> orderLocationCard.strokeColor = getColor(R.color.colorPrimary)
            2 -> orderTimerCard.strokeColor = getColor(R.color.colorPrimary)
            3 -> orderDeliveryFeeCard.strokeColor = getColor(R.color.colorPrimary)
        }

    }

    private fun clearFilters() {

        orderAZCard.background.setTint(getColor(R.color.deprecated_white_transparent))
        orderAZText.setTextColor(getColor(R.color.deprecated_dark_grey))
        isOrderAZChecked = false

        orderLocationCard.background.setTint(getColor(R.color.deprecated_white_transparent))
        orderLocationText.setTextColor(getColor(R.color.deprecated_dark_grey))
        isOrderLocationChecked = false

        orderTimerCard.background.setTint(getColor(R.color.deprecated_white_transparent))
        orderTimerText.setTextColor(getColor(R.color.deprecated_dark_grey))
        isOrderTimerChecked = false

        orderDeliveryFeeCard.background.setTint(getColor(R.color.deprecated_white_transparent))
        orderDeliveryFeeText.setTextColor(getColor(R.color.deprecated_dark_grey))
        isOrderDeliveryFeeChecked = false

    }

    private fun orderAZIsChecked() {
        clearFilters()

//        Muda a cor item A-Z

        orderAZCard.background.setTint(getColor(R.color.colorPrimary_75_translucent))
        orderAZText.setTextColor(getColor(R.color.colorPrimary))
        isOrderAZChecked = true
        SORT_FILTER = EnumSortBy.SORT_BY_A_Z.ordinal

    }

    private fun orderLocationIsChecked() {
        clearFilters()

//        Muda a cor do item localização
        orderLocationCard.background.setTint(getColor(R.color.colorPrimary_75_translucent))
        orderLocationText.setTextColor(getColor(R.color.colorPrimary))
        isOrderLocationChecked = true
        SORT_FILTER = EnumSortBy.SORT_BY_LOCALIZATION.ordinal

    }

    private fun orderTimerIsChecked() {
        clearFilters()

//        Muda a cor do item tempo de entrega
        orderTimerCard.background.setTint(getColor(R.color.colorPrimary_75_translucent))
        orderTimerText.setTextColor(getColor(R.color.colorPrimary))
        isOrderTimerChecked = true
        SORT_FILTER = EnumSortBy.SORT_BY_TIMER.ordinal

    }

    private fun orderDeliveryFeeIsChecked() {
        clearFilters()

//        Muda a cor do item taxa de entrega
        orderDeliveryFeeCard.background.setTint(getColor(R.color.colorPrimary_75_translucent))
        orderDeliveryFeeText.setTextColor(getColor(R.color.colorPrimary))
        isOrderDeliveryFeeChecked = true
        SORT_FILTER = EnumSortBy.SORT_BY_DELIVERY_FEE.ordinal

    }
}