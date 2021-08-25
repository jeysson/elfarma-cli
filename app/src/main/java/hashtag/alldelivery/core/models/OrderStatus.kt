package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName
import java.util.*

class OrderStatus {
    @SerializedName("id") val id: Int? = null
    @SerializedName("nome") val nome: String? = null
    @SerializedName("descricao") val descricao: String? = null
    @SerializedName("sequencia") val sequencia: Int? = null
}