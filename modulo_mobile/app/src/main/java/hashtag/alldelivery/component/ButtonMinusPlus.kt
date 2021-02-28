package hashtag.alldelivery.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.card.MaterialCardView
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.utils.OnChangedValueListener


/**
 * TODO: document your custom view class.
 */
class ButtonMinusPlus : ConstraintLayout{

    private var textItemCount: TextView? = null
    private var cardViewIncrementItem: MaterialCardView? = null
    private var plusButton: ImageView? = null
    private var evento: OnChangedValueListener? = null
    private var isOpen = false
    private var _total:Int = 0
    private var _produto: Product? = null

    var produto: Product?
    get() = _produto
    set(value) {
        _produto = value
    }

    var total: Int
    get() = _total
    set(value) {
        _total = value

        if(_total > 0)
            isOpen = animationListener(plusButton!!, cardViewIncrementItem!!, isOpen)

        textItemCount?.text = _total.toString()
        evento?.OnChangedValue(_produto!!, _total)
    }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context?, attrs: AttributeSet?, defStyle: Int?) {

        var view = LayoutInflater.from(context).inflate(R.layout.button, this, false)
        val incrementButton = view.findViewById<Button>(R.id.increment_selling_item_button)
        val decrementButton = view.findViewById<Button>(R.id.decrement_selling_item_button)
        textItemCount = view.findViewById<TextView>(R.id.txt_item_result)
        cardViewIncrementItem =
            view.findViewById<MaterialCardView>(R.id.card_view_increment_decrement_item)
        plusButton = view.findViewById<ImageView>(R.id.image_button_plus_item)
        //
        textItemCount?.text = _total.toString()
        plusButton?.setOnClickListener {
            _total += 1
            textItemCount?.text = _total.toString()
            isOpen = animationListener(plusButton!!, cardViewIncrementItem!!, isOpen)
            evento?.OnChangedValue(_produto!!, _total)
        }
        incrementButton.setOnClickListener {
            _total += 1
            textItemCount?.text = _total.toString()
            evento?.OnChangedValue(_produto!!, _total)
        }
        decrementButton.setOnClickListener {
            if(_total > 0)
                _total -= 1

            if (_total < 1) {
                isOpen = animationListener(plusButton!!, cardViewIncrementItem!!, true)

            } else {
                textItemCount?.text = _total.toString()
            }

            evento?.OnChangedValue(_produto!!, _total)
        }
        //
        val set = ConstraintSet()
        addView(view)
        set.clone(this)
        set.match(view, this)
    }

    fun ConstraintSet.match(view: View, parentView: View) {
        this.connect(view.id, ConstraintSet.TOP, parentView.id, ConstraintSet.TOP)
        this.connect(view.id, ConstraintSet.START, parentView.id, ConstraintSet.START)
        this.connect(view.id, ConstraintSet.END, parentView.id, ConstraintSet.END)
        this.connect(view.id, ConstraintSet.BOTTOM, parentView.id, ConstraintSet.BOTTOM)
    }

    fun animateAppear(plusButton: ImageView, cardViewIncrementItem: MaterialCardView) {
        plusButton.animate().apply {
            alpha(0f)
            duration = 200
        }.withEndAction {
            plusButton.animate().translationY(200f)
            val plusAnimate = TranslateAnimation(0f, 0f, 0f , 0f)
            plusAnimate.duration = 200
            plusAnimate.fillAfter = true

            cardViewIncrementItem.animate().alpha(1f).duration = 200
            val cardAnimate = TranslateAnimation(0f, 0f, 0f , 0f)
            cardAnimate.duration = 200
            cardAnimate.fillAfter = true

            cardViewIncrementItem.startAnimation(cardAnimate)
            plusButton.startAnimation(plusAnimate)
        }
    }

    fun animateDisappear(plusButton: ImageView, cardViewIncrementItem: MaterialCardView) {
        cardViewIncrementItem.animate().apply {
            alpha(0f)
            duration = 200
        }.withEndAction {
            plusButton.animate().translationY(0f).withEndAction {
                plusButton.animate().alpha(1f).duration = 200
            }
            val plusAnimate = TranslateAnimation(0f, 0f, 0f , 0f)
            plusAnimate.duration = 200
            plusAnimate.fillAfter = true

            val cardAnimate = TranslateAnimation(0f, 0f, 0f , 0f)
            cardAnimate.duration = 200
            cardAnimate.fillAfter = true

            cardViewIncrementItem.startAnimation(cardAnimate)
            plusButton.startAnimation(plusAnimate)
        }
    }

    fun animationListener(plusButton: ImageView, cardViewIncrementItem: MaterialCardView, isOpen: Boolean): Boolean {
        var opened = false
        if (isOpen){
            animateDisappear(plusButton, cardViewIncrementItem)
            opened = false
        }else if (!isOpen){
            animateAppear(plusButton, cardViewIncrementItem)
            opened = true
        }

        return opened
    }

    fun addOnChangeValueListener(ev: OnChangedValueListener){
        evento = ev
    }
}
