package hashtag.elfarma.core.models

import com.google.gson.annotations.SerializedName

class Store{

    @SerializedName("id")
    var id : Int? = null
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
    @SerializedName("disponivel") val disponivel : Boolean = false
    @SerializedName("ativo") val ativo : Boolean = false
    @SerializedName("logo")
    var logo : String? = null
    @SerializedName("imgBanner") val imgBanner : String? = null
    @SerializedName("banner")
    var banner : String? = null
    @SerializedName("tempoMinimo")
    var tempoMinimo : Int? = null
    @SerializedName("tempoMaximo")
    var tempoMaximo : Int? = null
    @SerializedName("hAbre") val hAbre : Int? = null
    @SerializedName("hFecha") val hFecha : Int? = null
    @SerializedName("lat") val lat : String? = null
    @SerializedName("long") val long : String? = null
    @SerializedName("taxaEntrega")
    var taxaEntrega : Float? = null
    @SerializedName("pedidoMinimo")
    var pedidoMinimo: Float? = null
    @SerializedName("distancia") val distancia : String? = null

    var logoCarregado: Boolean = false

    var bannerCarregado: Boolean = false

    var head: Boolean = false
    var publi: Boolean = false

    constructor()
}