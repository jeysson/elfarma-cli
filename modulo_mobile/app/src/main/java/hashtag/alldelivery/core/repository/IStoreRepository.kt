package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable
import retrofit2.Call

interface IStoreRepository {

    fun getActiveStores(
        indice: Int,
        tamanho: Int,
        lat: Double?,
        lon: Double?,
        tipoOrdenacao: Int
    ): Observable<List<Store>>

    fun getPagingStores(page: Int?, total: Int?, lat: Double?, lon: Double?, tipoordenacao: Int?): Call<ArrayList<Store>>
}