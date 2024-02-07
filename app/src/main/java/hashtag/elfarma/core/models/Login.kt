package hashtag.elfarma.core.models

import com.google.gson.annotations.Expose

class Login() {
    @field:Expose var nome: String? = null

    @field:Expose var sobrenome: String? = null

    @field:Expose var email: String? = null

    @field:Expose var senha: String? = null

    @field:Expose var codeverification: String? = null

    @field:Expose var tokenFCM: String? = null
}
