package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName

class OrderItem {
    private var _produto: Product? = null

    @SerializedName("id") val id: Int? = null
    @SerializedName("produtoid") var productId: Int? = null
    @SerializedName("quantidade") var quantity: Int? = null
    @SerializedName("preco") var price: Double? = null
    @SerializedName("produto") var produto: Product? = null

    constructor(prod: Product?, qtd: Int?, vlr: Double?){
        produto = Product()
        produto?.id = prod?.id
        produto?.nome = prod?.nome
        produto?.preco = vlr
        quantity = qtd
        price = vlr
    }
}