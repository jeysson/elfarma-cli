package hashtag.elfarma.core.network

import hashtag.elfarma.core.models.Message
import hashtag.elfarma.core.models.PaymentMethod
import hashtag.elfarma.core.models.Store
import hashtag.elfarma.core.models.User
import io.reactivex.Observable
import retrofit2.http.*

interface UserApi {
    @POST("usuario/atualizar")
    fun update(@Header("Authorization") authHeader: String,
                        @Body payload: User
    ) : Observable<Message>

}