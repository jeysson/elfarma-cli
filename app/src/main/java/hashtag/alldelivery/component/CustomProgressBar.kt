package hashtag.alldelivery.component

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import hashtag.alldelivery.R

/**
 * TODO: document your custom view class.
 */
class CustomProgressBar : ProgressBar {

    private var _valueAnimator: ValueAnimator? = null
    private var _animate: Boolean = false


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
            attrs, R.styleable.CustomProgressBar, defStyle, 0
        )
        a.recycle()
    }

    fun  getAnimatorListener(
        customProgressBar: CustomProgressBar,
        animatorListener: Animator.AnimatorListener?,
        i: Int,
        i2: Int,
        j: Long,
        i3: Int,
        obj: Any?
    ): ObjectAnimator? {

        var animatorListener = animatorListener

        var j = j

        if (i3 and 1 != 0) {
            animatorListener = null
        }

        val i4 = if (i3 and 2 != 0) 0 else i
        val i5 = if (i3 and 4 != 0) 100 else i2

        if (i3 and 8 != 0) {
            j = 1000
        }

        return customProgressBar.setObjectAnimator(animatorListener, i4, i5, j)
    }

    fun setObjectAnimator(
        animatorListener: Animator.AnimatorListener?,
        i: Int,
        i2: Int,
        j: Long
    ): ObjectAnimator {

        val ofInt = ObjectAnimator.ofInt(this, "progress", *intArrayOf(i, i2))

        if (animatorListener != null) {
            ofInt.addListener(animatorListener)
        }

        ofInt.duration = j
        ofInt.interpolator = LinearInterpolator()

        return ofInt
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        cancelAnimate()
    }

    fun setAnimate(z: Boolean) {
        if (_animate != z) {
            _animate = z
            secondaryProgress = if (z) 100 else 0
        }
    }

    override fun setSecondaryProgress(i: Int) {
        if (i > 0) {
            val valueAnimator2 = _valueAnimator
            if (valueAnimator2 == null) {
                val ofInt = ValueAnimator.ofInt(*intArrayOf(progress, i))
                ofInt.duration = 2500
                ofInt.setEvaluator(ArgbEvaluator())
                ofInt.repeatCount = -1
                ofInt.repeatMode = ValueAnimator.RESTART
                ofInt.addUpdateListener(CustomAnimatorUpdateListener(this))
                _valueAnimator = ofInt
            } else if (valueAnimator2 != null) {
                if(progress == 100)
                    progress = 1
                else
                    progress++
                valueAnimator2.setIntValues(*intArrayOf(progress, 100))
            }
            val valueAnimator3 = _valueAnimator!!
            if (valueAnimator3 != null && !valueAnimator3.isStarted) {
                valueAnimator3.start()
                return
            }
            return
        }

        val valueAnimator4 = _valueAnimator
        if (valueAnimator4 != null) {
            valueAnimator4!!.cancel()
        }
        super.setSecondaryProgress(i)
    }

    fun cancelAnimate(){
        if(_valueAnimator != null){
            _valueAnimator?.cancel()
        }
    }

    internal class CustomAnimatorUpdateListener(val customProgressBar: CustomProgressBar) : AnimatorUpdateListener {
        override fun onAnimationUpdate(valueAnimator: ValueAnimator) {

            val animatedValue = valueAnimator.animatedValue

            if (animatedValue != null) {
                customProgressBar.setSecondaryProgress((animatedValue as Int).toInt())
                return
            }

            throw TypeCastException("null cannot be cast to non-null type kotlin.Int")
        }
    }

}