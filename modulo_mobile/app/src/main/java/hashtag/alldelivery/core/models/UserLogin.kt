package br.com.gruposimoes.expedicao.core.models

import com.google.gson.annotations.SerializedName

class UserLogin(val username: String,
                val password: String,
                @SerializedName("access_enrolment") val accessEnrolment: Boolean=true)