package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class Order {

    @SerializedName("id") val id: Int? = null
    @SerializedName("loja")
    var store: Store? = null
    @SerializedName("formaPagamento")
    var paymentMethod: PaymentMethod? = null
    @SerializedName("data") val date:Date? = null
    @SerializedName("dataPedido") val dateOrder: Date? = null
    @SerializedName("endereco")
    var address: Address? = null
    @SerializedName("usuarioid")var userId: Int = 1
    @SerializedName("itens")var itens: ArrayList<OrderItem>? = null

    constructor(){
        itens = ArrayList<OrderItem>()
    }

}