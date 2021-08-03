package hashtag.alldelivery.core.network

import hashtag.alldelivery.core.models.PaymentMethod
import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface StoreApi {
    @GET("loja/paginar?")
    fun getActiveStores(@Header("Authorization") authHeader: String,
                        @Query("indice") indice: Int,
                        @Query("tamanho") tamanho: Int,
                        @Query("lat") lat: Double,
                        @Query("lon") lon: Double,
                        @Query("tipoordenacao") tipoOrdenacao: Int
    ) : Observable<ArrayList<Store>>

    @GET("loja/paginar?")
    fun getPagingStores(@Header("Authorization") authHeader: String,
        @Query("indice") indice: Int?,
        @Query("tamanho") tamanho: Int?,
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("tipoordenacao") tipoOrdenacao: Int?
    ) : Observable<ArrayList<Store>>

    @GET("loja/logo?")
    fun getStoreLogo(@Header("Authorization") authHeader: String,
                     @Query("loja") loja: Int?) : Observable<Store>

    @GET("loja/banner?")
    fun getStoreBanner(@Header("Authorization") authHeader: String,
                       @Query("loja") loja: Int?) : Observable<Store>

    @GET("loja/formaspagamento?")
    fun getPaymentMethods(@Header("Authorization") authHeader: String,
                          @Query("loja") loja: Int?): Observable<java.util.ArrayList<PaymentMethod>>
}