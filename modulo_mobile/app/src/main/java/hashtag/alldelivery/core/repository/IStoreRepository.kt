package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IStoreRepository {

    fun getActiveStores(
        indice: Int,
        tamanho: Int,
        lat: Double?,
        lon: Double?,
        tipoOrdenacao: Int
    ): Observable<ArrayList<Store>>

    fun getPagingStores(
        page: Int?,
        total: Int?,
        lat: Double?,
        lon: Double?,
        tipoordenacao: Int?): Observable<ArrayList<Store>>

    fun getStoreLogo(loja: Int?) : Observable<Store>

    fun getStoreBanner(loja: Int?) : Observable<Store>
}