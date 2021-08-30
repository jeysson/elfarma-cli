package hashtag.elfarma.core.utils

import hashtag.elfarma.ui.products.GroupProductsAdapter

interface LoadViewItemAdpter {
    fun OnLoadViewItemAdpter(group: Int?, position: Int, holfer: GroupProductsAdapter.ProductItemViewHolder)
}