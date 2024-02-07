package hashtag.elfarma.core.models

class User {

    val sobrenome: String? = null

    val nome: String? = null

    var id: Int? = null

    var email: String? = null

    var senha: String? = null

    var validated: Boolean? = false

    var senhaProv: Boolean? = false

    var anonimo: Boolean? = false

    var token: String? = null

    var tokenFCM: String? = null

    var cpf: Long? = null

    var telefone: Long? = null

    constructor(){
        id = 1
    }
}
