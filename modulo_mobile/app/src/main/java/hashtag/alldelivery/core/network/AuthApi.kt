package hashtag.alldelivery.core.network

import br.com.gruposimoes.expedicao.core.models.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth-token/")
    fun login(@Body user: UserLogin): Observable<UserLoginResponse>
}