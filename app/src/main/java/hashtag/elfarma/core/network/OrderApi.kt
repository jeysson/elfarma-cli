package hashtag.elfarma.core.network

import hashtag.elfarma.core.models.Message
import hashtag.elfarma.core.models.Order
import io.reactivex.Observable
import retrofit2.http.*

interface OrderApi {

    @POST("pedido/registrar/")
    fun checkoutOrder(@Header("Authorization") authHeader: String,
                      @Body payload: Order): Observable<Message>

    @GET("pedido/obterhistorico/")
    fun getOrderHistory(@Header("Authorization") authHeader: String,
                        @Query("codUser") user: Int,
                        @Query("indice") page: Int,
                        @Query("tamanho") total: Int): Observable<Message>

    @GET("pedido/obter/")
    fun getOrder(@Header("Authorization") authHeader: String,
                 @Query("idPedido") user: Int): Observable<Message>

    @GET("pedido/obteravaliacaopendente/")
    fun getOrdersWaitingEvaluate(@Header("Authorization")authHeader: String,
                                 @Query("codUser") user: Int): Observable<Message>

    @POST("pedido/salvaravaliacao/")
    fun saveEvaluate(@Header("Authorization")authHeader: String,
                     @Body payload: Order): Observable<Message>
}