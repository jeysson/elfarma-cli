package hashtag.elfarma.core.utils

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import hashtag.elfarma.core.models.Store

class StoreDiffCallback(private val oldList: ArrayList<Store>, private val newList: ArrayList<Store>): DiffUtil.Callback() {
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
                oldList[oldItemPosition].nomeFantasia == newList[newItemPosition].nomeFantasia &&
                oldList[oldItemPosition].cnpj == newList[newItemPosition].cnpj &&
                oldList[oldItemPosition].logo == newList[newItemPosition].logo
    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        return super.getChangePayload(oldPosition, newPosition)
    }
}