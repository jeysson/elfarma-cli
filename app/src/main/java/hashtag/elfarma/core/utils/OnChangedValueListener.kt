package hashtag.elfarma.core.utils

import hashtag.elfarma.component.ButtonMinusPlus
import hashtag.elfarma.core.models.Product

interface OnChangedValueListener {

    fun OnChangedValue(obj: ButtonMinusPlus, prod: Product, value: Int){

    }
}