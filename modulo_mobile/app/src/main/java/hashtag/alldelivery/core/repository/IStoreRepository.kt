package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable

interface IStoreRepository {

    fun getActiveStores(ordenationType: Int): Observable<List<Store>>

}