package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.network.StoreApi
import io.reactivex.Observable
import retrofit2.Call

class StoreRepository(
    private val dataSource: StoreApi
): IStoreRepository, BaseRepository() {

    override fun getActiveStores(
        indice: Int,
        tamanho: Int,
        lat: Double?,
        lon: Double?,
        tipoOrdenacao: Int
    ): Observable<ArrayList<Store>> {
        val newLat = -30.09488
        val newLon = -60.0462758
        if (lat == null || lon == null){
            return runOnBackground(dataSource.getActiveStores(indice,tamanho, newLat, newLon, tipoOrdenacao))
        }
        return runOnBackground(dataSource.getActiveStores(indice,tamanho, lat, lon, tipoOrdenacao))
    }

    override fun getPagingStores(
        page: Int?,
        total: Int?,
        lat: Double?,
        lon: Double?,
        tipoordenacao: Int?
    ): Observable<ArrayList<Store>> {
        return runOnBackground(dataSource.getPagingStores(page, total, lat, lon, tipoordenacao))
    }

    override fun getStoreLogo(loja: Int?): Observable<Store> {
        return runOnBackground(dataSource.getStoreLogo(loja))
    }

    override fun getStoreBanner(loja: Int?): Observable<Store> {
        return runOnBackground(dataSource.getStoreLogo(loja))
    }
}