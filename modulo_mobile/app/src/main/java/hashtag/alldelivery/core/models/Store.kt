package hashtag.alldelivery.core.models

import com.google.gson.annotations.SerializedName

class Store{
    @SerializedName("id") val id : Int? = null
    @SerializedName("cnpj") val cnpj : Int? = null
    @SerializedName("nomeFantasia") var nomeFantasia : String? = null
    @SerializedName("nomeRazao") var nomeRazao : String? = null
    @SerializedName("descricao") val descricao : String? = null
    @SerializedName("email") val email : String? = null
    @SerializedName("cep") val cep : Int?= null
    @SerializedName("endereco") val endereco : String? = null
    @SerializedName("numero") val numero : Int? = null
    @SerializedName("complemento") val complemento : String? = null
    @SerializedName("bairro") val bairro : String? = null
    @SerializedName("cidade") val cidade : String? = null
    @SerializedName("uf") val uf : String? = null
    @SerializedName("telefoneComercial") val telefoneComercial : Long? = null
    @SerializedName("telefoneAlternativo") val telefoneAlternativo : Long? = null
    @SerializedName("telefoneCelular") val telefoneCelular : Long? = null
    @SerializedName("contato") val contato : String? = null
    @SerializedName("lat") val lat : String? = null
    @SerializedName("long") val long : String? = null
    @SerializedName("disponivel") val disponivel : Boolean = false
    @SerializedName("ativo") val ativo : Boolean = false

    constructor()
}