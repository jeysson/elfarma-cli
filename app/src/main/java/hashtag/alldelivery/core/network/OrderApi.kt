package hashtag.alldelivery.core.network

import hashtag.alldelivery.core.models.Message
import hashtag.alldelivery.core.models.Order
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApi {

    @POST("pedido/registrar/")
    fun checkoutOrder(@Body payload: Order): Observable<Message>

    @GET("pedido/obterhistorico/")
    fun getOrderHistory(@Query("codUser") user: Int,
                        @Query("indice") page: Int,
                        @Query("tamanho") total: Int): Observable<Message>

    @GET("pedido/obter/")
    fun getOrder(@Query("idPedido") user: Int): Observable<Message>
}