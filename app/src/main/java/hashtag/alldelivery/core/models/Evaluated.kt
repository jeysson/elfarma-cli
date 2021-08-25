package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName

class Evaluated {
    @SerializedName("notaLoja")
    var notaLoja: Float? = null

    @SerializedName("pedido")
    var pedido: Order? = null

    @SerializedName("tipo")
    var tipo: Int? = null
}