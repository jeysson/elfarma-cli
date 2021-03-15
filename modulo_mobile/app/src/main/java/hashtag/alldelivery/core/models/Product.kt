package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName

class Product{

    @SerializedName("id")
    var id : Int? = null
    @SerializedName("nome")
    var nome : String? = null
    @SerializedName("descricao") val descricao : String? = null
    @SerializedName("preco") val preco : Double? = null
    //@SerializedName("grupoProdutos") val grupoProdutos : List<GroupProduct>? = null
    var productImages: List<ProductImage>? = ArrayList<ProductImage>()
    var carregouImagens: Boolean = false
    constructor()
}