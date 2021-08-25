package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

class OrderHistory {
    @SerializedName("id")
    var id: Int? = null
    @SerializedName("loja") val store: String? = null
    @SerializedName("logo") val logo: String? = null
    @SerializedName("nomeItem1") val first_item_name: String? = null
    @SerializedName("quantidadeItem1") val first_item_quantity: Int? = null
    @SerializedName("nomeItem2") val second_item_name: String? = null
    @SerializedName("quantidadeItem2") val second_item_quantity: Int? = null
    @SerializedName("quantidade") val itensSize: Int? = null
    @SerializedName("data") val date: Date? = null
    @SerializedName("status") val status: OrderStatus? = null
    @SerializedName("avaliacao") val avaliacao: Float? = null
    @SerializedName("diasAvaliacao") val diasavaliacao: Int? = null
}
