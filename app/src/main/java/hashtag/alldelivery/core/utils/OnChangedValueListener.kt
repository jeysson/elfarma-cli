package hashtag.alldelivery.core.utils

import hashtag.alldelivery.core.models.Product

interface OnChangedValueListener {

    fun OnChangedValue(prod: Product, value: Int){

    }
}