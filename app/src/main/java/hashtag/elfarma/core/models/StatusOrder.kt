package hashtag.elfarma.core.models

import com.google.gson.annotations.SerializedName


class StatusOrder {
    @SerializedName("id")  var id: Int? = null
    @SerializedName("nome")  var nome: String? = null
    @SerializedName("descricao")  var descricao: String? = null
    @SerializedName("sequencia")  var sequencia: Int? = null
}