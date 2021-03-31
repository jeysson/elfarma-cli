package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName

class Product{

    @SerializedName("id")
    var id : Int? = null
    @SerializedName("nome")
    var nome : String? = null
    @SerializedName("descricao") val descricao : String? = null
    @SerializedName("preco")
    var preco : Double? = null
    @SerializedName("loja") val store : Store? = null
    var productImages: ArrayList<ProductImage>? = ArrayList<ProductImage>()
    var carregouImagens: Boolean = false
    constructor()
}