package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName

class GroupProduct {
    @SerializedName("grupo")val group: Group? = null
    @SerializedName("produtoId")val productId: Product? = null

    constructor()
}