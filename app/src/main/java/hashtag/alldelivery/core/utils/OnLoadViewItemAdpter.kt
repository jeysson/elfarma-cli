package hashtag.alldelivery.core.utils

import hashtag.alldelivery.ui.products.GroupProductsAdapter

interface LoadViewItemAdpter {
    fun OnLoadViewItemAdpter(group: Int?, position: Int, holfer: GroupProductsAdapter.ProductItemViewHolder)
}