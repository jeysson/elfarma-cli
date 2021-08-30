package hashtag.elfarma.core.repository

import hashtag.elfarma.core.models.PaymentMethod
import hashtag.elfarma.core.models.Store
import io.reactivex.Observable

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

    fun getPaymentMethods(loja: Int?) : Observable<ArrayList<PaymentMethod>>
}