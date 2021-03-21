package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

class OrderHistory {
    @SerializedName("id") val id: Int? = null
    @SerializedName("loja") val store: String? = null
    @SerializedName("logo") val logo: String? = null
    @SerializedName("nomeItem") val first_item_name: String? = null
    @SerializedName("quantidadeItem") val item_quantity: Int? = null
    @SerializedName("quantidade") val itensSize: Int? = null
    @SerializedName("data") val date: Date? = null
    @SerializedName("status") val status: Int? = null
}
