package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.network.StoreApi
import io.reactivex.Observable

class StoreRepository(private val dataSource: StoreApi): IStoreRepository, BaseRepository() {

    override fun getActiveStores(): Observable<List<Store>> {
        return runOnBackground(dataSource.getActiveStores())
    }
}