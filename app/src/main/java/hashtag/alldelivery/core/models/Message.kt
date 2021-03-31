package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName

class Message {
    @SerializedName("codigo") val code:Int? = null
    @SerializedName("mensagem") val message:String? = null
    @SerializedName("dados") var data:Any? = null
}