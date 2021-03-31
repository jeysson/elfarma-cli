package br.com.gruposimoes.expedicao.core.models

import com.google.gson.annotations.SerializedName

class UserLoginResponse(
        val token: String,
        @SerializedName("user_id") val id: Int,
        @SerializedName("name") val name: String)
