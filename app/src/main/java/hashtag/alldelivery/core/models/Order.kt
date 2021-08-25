package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class Order {
    @SerializedName("vlrtroco")
    var vlrtroco: Double? = null

    @SerializedName("id")
    var id: Int? = null
    @SerializedName("loja")
    var store: Store? = null
    @SerializedName("formaPagamento")
    var paymentMethod: PaymentMethod? = null
    @SerializedName("data") val date:Date? = null
    @SerializedName("dataPedido") val dateOrder: Date? = null
    @SerializedName("endereco")
    var address: Address? = null
    @SerializedName("usuarioid")var userId: Int? = null
    @SerializedName("itens")var itens: ArrayList<OrderItem>? = null
    @SerializedName("histStatus")var listStatus: ArrayList<StatusOrder>? = null
    @SerializedName("avaliacoes")var evaluateds: ArrayList<Evaluated>? = null
    @SerializedName("comentarioAvaliacao")var comment: String? = null

    constructor(){
        itens = ArrayList<OrderItem>()
    }

}

