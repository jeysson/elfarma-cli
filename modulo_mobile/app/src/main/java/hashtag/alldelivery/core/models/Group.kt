package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName

class Group {
    @SerializedName("id") val id : Int? = null
    @SerializedName("nome") val nome : String? = null
    @SerializedName("loja") val loja : String? = null
    @SerializedName("ordem") val ordem : Int? = null

    var products: ArrayList<Product>? = ArrayList<Product>()

    constructor()
}