package hashtag.alldelivery.core.network

import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface StoreApi {
    @GET("loja/paginar?")
    fun getActiveStores(
        @Query("indice") indice: Int = 1,
        @Query("tamanho") tamanho: Int = 10,
        @Query("lat") lat: Double = -3.09488,
        @Query("lon") lon: Double = -20.0462758,
        @Query("tipoordenacao") tipoOrdenacao: Int
    ) : Observable<List<Store>>
}