package hashtag.alldelivery.core.network

import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface StoreApi {
    @GET("loja/paginar?")
    fun getActiveStores(
        @Query("indice") indice: Int,
        @Query("tamanho") tamanho: Int,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("tipoordenacao") tipoOrdenacao: Int
    ) : Observable<List<Store>>
}