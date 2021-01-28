package hashtag.alldelivery.ui.filter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hashtag.alldelivery.R
import hashtag.alldelivery.core.utils.Constants.SORT_BY_A_Z
import hashtag.alldelivery.core.utils.Constants.SORT_BY_DELIVERY_FEE
import hashtag.alldelivery.core.utils.Constants.SORT_BY_LOCALIZATION
import hashtag.alldelivery.core.utils.Constants.SORT_BY_TIMER
import hashtag.alldelivery.core.utils.Constants.SORT_FILTER
import kotlinx.android.synthetic.main.activity_filters.*
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

    private val clearButton by lazy { txt_limpar_filters }
    private val backButton by lazy { arrow_back_filters }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

//        Verifica o filtro que esta ativo
        when (SORT_FILTER) {
            SORT_BY_A_Z -> orderAZIsChecked()
            SORT_BY_LOCALIZATION -> orderLocationIsChecked()
            SORT_BY_TIMER -> orderTimerIsChecked()
            SORT_BY_DELIVERY_FEE -> orderDeliveryFeeIsChecked()
        }

        orderAZIcon.setOnClickListener {
            orderAZIsChecked()
        }

        orderLocationIcon.setOnClickListener {
            orderLocationIsChecked()
        }

        orderTimerIcon.setOnClickListener {
            orderTimerIsChecked()
        }

        orderDeliveryFeeIcon.setOnClickListener {
            orderDeliveryFeeIsChecked()
        }

        clearButton.setOnClickListener {
            clearFilters()
            orderAZIsChecked()
        }

        backButton.setOnClickListener {
            finish()
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
        SORT_FILTER = SORT_BY_A_Z

    }

    private fun orderLocationIsChecked() {
        clearFilters()

//        Muda a cor do item localização
        orderLocationCard.background.setTint(getColor(R.color.colorPrimary_75_translucent))
        orderLocationText.setTextColor(getColor(R.color.colorPrimary))
        isOrderLocationChecked = true
        SORT_FILTER = SORT_BY_LOCALIZATION

    }

    private fun orderTimerIsChecked() {
        clearFilters()

//        Muda a cor do item tempo de entrega
        orderTimerCard.background.setTint(getColor(R.color.colorPrimary_75_translucent))
        orderTimerText.setTextColor(getColor(R.color.colorPrimary))
        isOrderTimerChecked = true
        SORT_FILTER = SORT_BY_TIMER

    }

    private fun orderDeliveryFeeIsChecked() {
        clearFilters()

//        Muda a cor do item taxa de entrega
        orderDeliveryFeeCard.background.setTint(getColor(R.color.colorPrimary_75_translucent))
        orderDeliveryFeeText.setTextColor(getColor(R.color.colorPrimary))
        isOrderDeliveryFeeChecked = true
        SORT_FILTER = SORT_BY_DELIVERY_FEE

    }
}