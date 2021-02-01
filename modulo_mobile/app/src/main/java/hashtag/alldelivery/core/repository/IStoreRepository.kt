package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable

interface IStoreRepository {

    fun getActiveStores(
        indice: Int,
        tamanho: Int,
        lat: Double?,
        lon: Double?,
        tipoOrdenacao: Int
    ): Observable<List<Store>>

}