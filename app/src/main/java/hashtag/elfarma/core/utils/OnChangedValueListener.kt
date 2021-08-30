package hashtag.elfarma.core.utils

import hashtag.elfarma.core.models.Product

interface OnChangedValueListener {

    fun OnChangedValue(prod: Product, value: Int){

    }
}