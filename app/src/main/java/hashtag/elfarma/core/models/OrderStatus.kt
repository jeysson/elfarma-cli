package hashtag.elfarma.core.models

import com.google.gson.annotations.SerializedName

class OrderStatus {
    @SerializedName("id") val id: Int? = null
    @SerializedName("nome") val nome: String? = null
    @SerializedName("descricao") val descricao: String? = null
    @SerializedName("sequencia") val sequencia: Int? = null
}