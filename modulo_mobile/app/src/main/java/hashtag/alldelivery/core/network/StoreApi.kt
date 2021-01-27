package hashtag.alldelivery.core.network

import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface StoreApi {
    @GET("loja/ativas")
    fun getActiveStores() : Observable<List<Store>>
}