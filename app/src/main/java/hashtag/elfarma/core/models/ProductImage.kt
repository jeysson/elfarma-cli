package hashtag.elfarma.core.models

import com.google.gson.annotations.SerializedName

class ProductImage {
    @SerializedName("produtoId") var produtoId : Int? = null
    @SerializedName("seq") var seq : Int? = null
    @SerializedName("nome") var nome : String? = null
    @SerializedName("fotoBase64") var fotoBase64 : String? = null
    @SerializedName("produto") var produto : String? = null
}