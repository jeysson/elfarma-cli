package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.network.StoreApi
import io.reactivex.Observable

class StoreRepository(
    private val dataSource: StoreApi
): IStoreRepository, BaseRepository() {

    override fun getActiveStores(
        indice: Int,
        tamanho: Int,
        lat: Double?,
        lon: Double?,
        tipoOrdenacao: Int
    ): Observable<List<Store>> {
        val newLat = -30.09488
        val newLon = -60.0462758
        if (lat == null || lon == null){
            return runOnBackground(dataSource.getActiveStores(indice,tamanho, newLat, newLon, tipoOrdenacao))
        }
        return runOnBackground(dataSource.getActiveStores(indice,tamanho, lat, lon, tipoOrdenacao))
    }
}