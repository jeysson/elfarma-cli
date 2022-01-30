package hashtag.elfarma.core.repository

import hashtag.elfarma.core.models.PaymentMethod
import hashtag.elfarma.core.models.Store
import io.reactivex.Observable
import retrofit2.http.Query

interface IStoreRepository {

    fun getActiveStores(
        segmento: Int?,
        indice: Int,
        tamanho: Int,
        lat: Double?,
        lon: Double?,
        tipoOrdenacao: Int
    ): Observable<ArrayList<Store>>

    fun getPagingStores(
        segmento: Int?,
        page: Int?,
        total: Int?,
        lat: Double?,
        lon: Double?,
        tipoordenacao: Int?): Observable<ArrayList<Store>>

    fun getStoreLogo(loja: Int?) : Observable<Store>

    fun getStoreBanner(loja: Int?) : Observable<Store>

    fun getPaymentMethods(loja: Int?) : Observable<ArrayList<PaymentMethod>>
}