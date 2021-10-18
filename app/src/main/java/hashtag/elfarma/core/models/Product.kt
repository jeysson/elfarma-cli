package hashtag.elfarma.core.models

import com.google.gson.annotations.SerializedName

class Product{

    @SerializedName("id")
    var id : Int? = null
    @SerializedName("nome")
    var nome : String? = null
    @SerializedName("descricao")
    var descricao : String? = null
    @SerializedName("preco")
    var preco : Double? = null
    @SerializedName("precoPromocional")
    var precoPromocional : Double? = null
    @SerializedName("loja")
    var store : Store? = null

    @SerializedName("produtoFotos")
    var productImages: ArrayList<ProductImage>? = ArrayList<ProductImage>()
    var carregouImagens: Boolean = false
    constructor()
}