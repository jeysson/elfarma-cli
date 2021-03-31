package hashtag.alldelivery.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import hashtag.alldelivery.R

class CustomRatingBar: LinearLayout{
    private var _context: Context? = null

    var crb_rating: Float = 0f
    var crb_filledIconTint: Int = 0
    var crb_emptyIconTint: Int = 0
    var crb_iconHeight: Int = 0
    var crb_margin: Int = 0
    var crb_filledHalfIcon: Int = 0
    var crb_filledIcon: Int = 0
    var crb_emptyIcon: Int = 0
    var crb_maxRating: Int = 0

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

    fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {

        _context = context

        val obtainStyledAtt = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomRatingBar,
            defStyle,
            0
        )

        setRating(obtainStyledAtt.getFloat(R.styleable.CustomRatingBar_crb_rating, 0.0f))
        crb_maxRating = obtainStyledAtt.getInt(R.styleable.CustomRatingBar_crb_maxRating, 0)
        crb_emptyIcon = obtainStyledAtt.getResourceId(R.styleable.CustomRatingBar_crb_emptyIcon, 0)
        crb_filledIcon = obtainStyledAtt.getResourceId(
            R.styleable.CustomRatingBar_crb_filledIcon,
            0
        )
        crb_filledHalfIcon = obtainStyledAtt.getResourceId(
            R.styleable.CustomRatingBar_crb_filledHalfIcon,
            0
        )
        crb_margin = obtainStyledAtt.getDimensionPixelSize(
            R.styleable.CustomRatingBar_crb_margin,
            0
        )
        crb_iconHeight = obtainStyledAtt.getDimensionPixelSize(
            R.styleable.CustomRatingBar_crb_iconHeight,
            0
        )
        crb_emptyIconTint = obtainStyledAtt.getResourceId(
            R.styleable.CustomRatingBar_crb_emptyIconTint,
            0
        )
        crb_filledIconTint = obtainStyledAtt.getResourceId(
            R.styleable.CustomRatingBar_crb_filledIconTint,
            0
        )
        obtainStyledAtt.recycle()
        initialize(context)
    }

    fun getRating(): Float? {
        return crb_rating
    }

    fun setRating(f: Float) {
        crb_rating = f
        initialize(_context!!)
    }

    fun initialize(context: Context) {
        removeAllViews()

        val i: Int = crb_maxRating

        if (1 <= i) {
            var i2 = 1
            while (true) {
                val appCompatImageView = AppCompatImageView(context)
                appCompatImageView.scaleType = ScaleType.FIT_CENTER
                val f = i2.toFloat()
                if (f <= crb_rating) {
                    appCompatImageView.setImageResource(crb_filledIcon)
                    if (crb_filledIconTint > 0) {
                        appCompatImageView.setColorFilter(context.getColor(crb_filledIconTint))
                    }
                } else {
                    appCompatImageView.setImageResource(crb_emptyIcon)
                    if (crb_emptyIconTint > 0) {
                        appCompatImageView.setColorFilter(context.getColor(crb_emptyIconTint))
                    }
                }
                val f2: Float = crb_rating
                var z = false
                var num: Int? = null
                var i3 = -2
                if (f - f2 <= 0.toFloat() || f - f2 >= 1.toFloat() || !calc()) {
                    var valueOf: Int? = Integer.valueOf(crb_iconHeight)
                    if (valueOf!!.toInt() <= 0) {
                        valueOf = null
                    }
                    val intValue = valueOf ?: -2
                    val valueOf2: Int = Integer.valueOf(crb_iconHeight)
                    if (valueOf2 > 0) {
                        z = true
                    }
                    if (z) {
                        num = valueOf2
                    }
                    if (num != null) {
                        i3 = num.toInt()
                    }
                    val layoutParams = LayoutParams(intValue, i3)
                    if (i2 > 1) {
                        layoutParams.leftMargin = crb_margin
                    }
                    appCompatImageView.layoutParams = layoutParams
                    addView(appCompatImageView)
                } else {
                    val frameLayout = FrameLayout(context)
                    val layoutParams2 = LayoutParams(-2, -2)
                    if (i2 > 1) {
                        layoutParams2.leftMargin = crb_margin
                    }
                    frameLayout.layoutParams = layoutParams2
                    var valueOf3: Int? = Integer.valueOf(crb_iconHeight)
                    if (valueOf3!!.toInt() <= 0) {
                        valueOf3 = null
                    }
                    val intValue2 = valueOf3 ?: -2
                    var valueOf4: Int? = Integer.valueOf(crb_iconHeight)
                    if (valueOf4!!.toInt() <= 0) {
                        valueOf4 = null
                    }
                    appCompatImageView.layoutParams = FrameLayout.LayoutParams(
                        intValue2,
                        valueOf4 ?: -2
                    )
                    frameLayout.addView(appCompatImageView)
                    val appCompatImageView2 = AppCompatImageView(context)
                    appCompatImageView2.scaleType = ScaleType.FIT_CENTER
                    var valueOf5: Int? = Integer.valueOf(crb_iconHeight)
                    if (valueOf5!!.toInt() <= 0) {
                        valueOf5 = null
                    }
                    val intValue3 = valueOf5 ?: -2
                    val valueOf6: Int = Integer.valueOf(crb_iconHeight)
                    if (valueOf6 > 0) {
                        z = true
                    }
                    if (z) {
                        num = valueOf6
                    }
                    if (num != null) {
                        i3 = num.toInt()
                    }
                    appCompatImageView2.layoutParams = LayoutParams(intValue3, i3)
                    appCompatImageView2.setImageResource(crb_filledHalfIcon)
                    if (crb_filledIconTint > 0) {
                        appCompatImageView2.setColorFilter(context.getColor(crb_filledIconTint))
                    }
                    frameLayout.addView(appCompatImageView2)
                    addView(frameLayout)
                }
                if (i2 != i) {
                    i2++
                } else {
                    return
                }
            }
        }
    }

    fun calc(): Boolean {
        return Math.round(crb_rating * 10.0f).toFloat() / 10.0f % 1.0f > 0.toFloat()
    }
}