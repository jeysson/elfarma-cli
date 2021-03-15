package hashtag.alldelivery.component

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.airbnb.lottie.value.SimpleLottieValueCallback
import hashtag.alldelivery.R
import org.jetbrains.anko.colorAttr

/**
 * TODO: document your custom view class.
 */
class Loading : LinearLayout {

    private var _lv_color: Int = Color.BLUE // TODO: use a default from R.color...

    private var textPaint: TextPaint? = null
    private var lottieAnimationView: LottieAnimationView? = null


    /**
     * The font color
     */
    var lv_color: Int
        get() = _lv_color
        set(value) {
            _lv_color = value
        }

    /**
     * In the example view, this drawable is drawn above the text.
     */
    var exampleDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        var layout = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
        lottieAnimationView = LottieAnimationView(context)
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        lottieAnimationView = LottieAnimationView(context)
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.Loading, defStyle, 0
        )

        _lv_color = a.getColor(
            R.styleable.Loading_lv_color,
            lv_color
        )

        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }

        //
        var params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        lottieAnimationView!!.layoutParams = params
        lottieAnimationView!!.setAnimation(R.raw.loading)
        lottieAnimationView!!.playAnimation()
        lottieAnimationView!!.repeatCount = LottieDrawable.INFINITE
        lottieAnimationView!!.addValueCallback( KeyPath("**")
                                              , LottieProperty.COLOR_FILTER
                                              , LottieValueCallback<ColorFilter>(SimpleColorFilter(lv_color)))

        this.addView(lottieAnimationView)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        // Draw the example drawable on top of the text.
        exampleDrawable?.let {
            it.setBounds(
                paddingLeft, paddingTop,
                paddingLeft + contentWidth, paddingTop + contentHeight
            )
            it.draw(canvas)
        }
    }
}
