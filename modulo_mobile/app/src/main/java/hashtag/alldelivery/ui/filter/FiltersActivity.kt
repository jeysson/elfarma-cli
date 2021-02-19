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
    private val orderAZCard by lazy { internal_view_order_a_z }
    private val orderAZBorder by lazy { card_view_order_a_z }
    private val orderAZText by lazy { txt_order_a_z }
    private val orderAZView by lazy { view_order_a_z }
    private var isOrderAZChecked = false

    private val orderLocationIcon by lazy { img_order_location }
    private val orderLocationCard by lazy { internal_view_order_location }
    private val orderLocationBorder by lazy { card_view_order_location }
    private val orderLocationText by lazy { txt_order_location }
    private val orderLocationView by lazy { view_order_location }
    private var isOrderLocationChecked = false

    private val orderTimerIcon by lazy { img_order_timer }
    private val orderTimerCard by lazy { internal_view_order_timer }
    private val orderTimerBorder by lazy { card_view_order_timer }
    private val orderTimerText by lazy { txt_order_timer }
    private val orderTimerView by lazy { view_order_timer }
    private var isOrderTimerChecked = false

    private val orderDeliveryFeeIcon by lazy { img_order_delivery_fee }
    private val orderDeliveryFeeCard by lazy { internal_view_order_delivery_fee }
    private val orderDeliveryFeeBorder by lazy { card_view_order_delivery_fee }
    private val orderDeliveryFeeText by lazy { txt_order_delivery_fee }
    private val orderDeliveryFeeView by lazy { view_order_delivery_fee }
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
                setStrokeCardColor(SORT_FILTER)
                setIconColor(SORT_FILTER)
            }
            EnumSortBy.SORT_BY_LOCALIZATION.ordinal -> {
                orderLocationIsChecked()
                clearButton.setTextColor(getColor(R.color.colorPrimary))
                setStrokeCardColor(SORT_FILTER)
                setIconColor(SORT_FILTER)
            }
            EnumSortBy.SORT_BY_TIMER.ordinal -> {
                orderTimerIsChecked()
                clearButton.setTextColor(getColor(R.color.colorPrimary))
                setStrokeCardColor(SORT_FILTER)
                setIconColor(SORT_FILTER)
            }
            EnumSortBy.SORT_BY_DELIVERY_FEE.ordinal -> {
                orderDeliveryFeeIsChecked()
                clearButton.setTextColor(getColor(R.color.colorPrimary))
                setStrokeCardColor(SORT_FILTER)
                setIconColor(SORT_FILTER)
            }
        }

//        Seleciona qual tipo de ordem será aplicado no filtro
        orderAZCard.setOnClickListener {
            orderAZIsChecked()
            clearButton.setTextColor(getColor(R.color.medium_gray))
            setStrokeCardColor(SORT_FILTER)
            setIconColor(SORT_FILTER)
        }

        orderLocationCard.setOnClickListener {
            orderLocationIsChecked()
            clearButton.setTextColor(getColor(R.color.colorPrimary))
            setStrokeCardColor(SORT_FILTER)
            setIconColor(SORT_FILTER)
        }

        orderTimerCard.setOnClickListener {
            orderTimerIsChecked()
            clearButton.setTextColor(getColor(R.color.colorPrimary))
            setStrokeCardColor(SORT_FILTER)
            setIconColor(SORT_FILTER)
        }

        orderDeliveryFeeCard.setOnClickListener {
            orderDeliveryFeeIsChecked()
            clearButton.setTextColor(getColor(R.color.colorPrimary))
            setStrokeCardColor(SORT_FILTER)
            setIconColor(SORT_FILTER)
        }

        clearButton.setOnClickListener {
            clearFilters()
            orderAZIsChecked()
            clearButton.setTextColor(getColor(R.color.medium_gray))
            setStrokeCardColor(SORT_FILTER)
            setIconColor(SORT_FILTER)
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
    private fun setStrokeCardColor(filterNumber: Int) {
        orderAZBorder.background.setTint(getColor(R.color.medium_gray))
        orderLocationBorder.background.setTint(getColor(R.color.medium_gray))
        orderTimerBorder.background.setTint(getColor(R.color.medium_gray))
        orderDeliveryFeeBorder.background.setTint(getColor(R.color.medium_gray))

        when (filterNumber) {
            0 -> orderAZBorder.background.setTint(getColor(R.color.colorPrimary))
            1 -> orderLocationBorder.background.setTint(getColor(R.color.colorPrimary))
            2 -> orderTimerBorder.background.setTint(getColor(R.color.colorPrimary))
            3 -> orderDeliveryFeeBorder.background.setTint(getColor(R.color.colorPrimary))
        }

    }

    private fun setIconColor(filterNumber: Int) {
        orderAZIcon.setColorFilter(getColor(R.color.heavy_grey))
        orderLocationIcon.setColorFilter(getColor(R.color.heavy_grey))
        orderTimerIcon.setColorFilter(getColor(R.color.heavy_grey))
        orderDeliveryFeeIcon.setColorFilter(getColor(R.color.heavy_grey))


        when (filterNumber) {
            0 -> orderAZIcon.setColorFilter(getColor(R.color.colorPrimaryMedium))
            1 -> orderLocationIcon.setColorFilter(getColor(R.color.colorPrimaryMedium))
            2 -> orderTimerIcon.setColorFilter(getColor(R.color.colorPrimaryMedium))
            3 -> orderDeliveryFeeIcon.setColorFilter(getColor(R.color.colorPrimaryMedium))
        }
    }

    private fun clearFilters() {

        orderAZView.setBackgroundResource(R.color.white)
        orderAZText.setTextColor(getColor(R.color.deprecated_dark_grey))
        isOrderAZChecked = false

        orderLocationView.setBackgroundResource(R.color.white)
        orderLocationText.setTextColor(getColor(R.color.deprecated_dark_grey))
        isOrderLocationChecked = false

        orderTimerView.setBackgroundResource(R.color.white)
        orderTimerText.setTextColor(getColor(R.color.deprecated_dark_grey))
        isOrderTimerChecked = false

        orderDeliveryFeeView.setBackgroundResource(R.color.white)
        orderDeliveryFeeText.setTextColor(getColor(R.color.deprecated_dark_grey))
        isOrderDeliveryFeeChecked = false

    }

    private fun orderAZIsChecked() {
        clearFilters()

//        Muda a cor item A-Z
        orderAZView.setBackgroundResource(R.color.colorPrimary_75_translucent)
        orderAZText.setTextColor(getColor(R.color.colorPrimary))
        isOrderAZChecked = true
        SORT_FILTER = EnumSortBy.SORT_BY_A_Z.ordinal

    }

    private fun orderLocationIsChecked() {
        clearFilters()

//        Muda a cor do item localização
        orderLocationView.setBackgroundResource(R.color.colorPrimary_75_translucent)
        orderLocationText.setTextColor(getColor(R.color.colorPrimary))
        isOrderLocationChecked = true
        SORT_FILTER = EnumSortBy.SORT_BY_LOCALIZATION.ordinal

    }

    private fun orderTimerIsChecked() {
        clearFilters()

//        Muda a cor do item tempo de entrega
        orderTimerView.setBackgroundResource(R.color.colorPrimary_75_translucent)
        orderTimerText.setTextColor(getColor(R.color.colorPrimary))
        isOrderTimerChecked = true
        SORT_FILTER = EnumSortBy.SORT_BY_TIMER.ordinal

    }

    private fun orderDeliveryFeeIsChecked() {
        clearFilters()

//        Muda a cor do item taxa de entrega
        orderDeliveryFeeView.setBackgroundResource(R.color.colorPrimary_75_translucent)
        orderDeliveryFeeText.setTextColor(getColor(R.color.colorPrimary))
        isOrderDeliveryFeeChecked = true
        SORT_FILTER = EnumSortBy.SORT_BY_DELIVERY_FEE.ordinal

    }
}