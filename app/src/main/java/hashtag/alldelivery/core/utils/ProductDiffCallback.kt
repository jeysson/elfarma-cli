package hashtag.alldelivery.core.utils

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.Store

class ProductDiffCallback(private val oldList: ArrayList<Product>, private val newList: ArrayList<Product>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id === newList.get(newItemPosition).id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id &&
                oldList[oldItemPosition].nome == newList[newItemPosition].nome &&
                oldList[oldItemPosition].preco == newList[newItemPosition].preco &&
                oldList[oldItemPosition].productImages == newList[newItemPosition].productImages

    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        return super.getChangePayload(oldPosition, newPosition)
    }
}