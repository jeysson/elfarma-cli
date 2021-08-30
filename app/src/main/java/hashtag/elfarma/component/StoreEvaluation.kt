package hashtag.elfarma.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.widget.LinearLayout
import hashtag.elfarma.R

/**
 * TODO: document your custom view class.
 */
class StoreEvaluation : LinearLayout {

    private var _re_style_new: Int? = null
    private var _re_style_new_star_size: Int? = null
    private var _re_style_new_text_size: String? =  null
    private var _re_style_quantity: String?  = null
    private var _re_style_quantity_text_size: Int? =  null
    private var _re_style_rate: String?  = null
    private var _re_style_rate_text_size: Int? =  null

    private var textPaint: TextPaint? = null
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f

    /**
     * In the example view, this drawable is drawn above the text.
     */
    var exampleDrawable: Drawable? = null

    var re_style_new: Int?
        get() = _re_style_new
    set(value) {
        _re_style_new = value
    }

    var re_style_new_star_size: Int?
    get() = _re_style_new_star_size
    set(value) {
        _re_style_new_star_size = value
    }

    var re_style_new_text_size: String?
    get() = _re_style_new_text_size
    set(value) {
        _re_style_new_text_size = value
    }
    var re_style_quantity: String?
    get() = _re_style_quantity
    set(value) {
        _re_style_quantity= value
    }
    var re_style_quantity_text_size: Int?
    get() = _re_style_quantity_text_size
    set(value) {
        _re_style_quantity_text_size = value
    }
    var re_style_rate: String?
    get() = _re_style_rate
    set(value) {
        _re_style_rate = value
    }
    var re_style_rate_text_size: Int?
    get() = _re_style_rate_text_size
    set(value) {
        _re_style_rate_text_size = value
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.StoreEvaluation, defStyle, 0
        )

        a.recycle()

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }
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
