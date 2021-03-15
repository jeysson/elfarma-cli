package hashtag.alldelivery.core.network

import hashtag.alldelivery.core.models.Message
import hashtag.alldelivery.core.models.Order
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderApi {

    @POST("pedido/registrar/")
    fun checkoutOrder(@Body payload: Order): Observable<Message>
}