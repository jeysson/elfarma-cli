package hashtag.alldelivery.ui.store

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hashtag.alldelivery.AllDeliveryApplication.Companion.PAGE_OBSERVER
import hashtag.alldelivery.core.models.BusinessEvent
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.repository.IStoreRepository
import hashtag.alldelivery.core.utils.SingleLiveEvent

class StoresViewModel(private val _storeRep: IStoreRepository) : ViewModel() {

    var eventErro = SingleLiveEvent<BusinessEvent>()
    private var _stores: MutableLiveData<List<Store>> = MutableLiveData()


    //    Itens de paginação
    private var _storesNewSearch: MutableLiveData<List<Store>> = MutableLiveData()
    private var _page = 1
    private var _controlIndice = 10
    private var _lat: Double? = 0.0
    private var _lon: Double? = 0.0
    private var _tipoOrdenacao = 0

    @SuppressLint("CheckResult")
    fun getActiveStores(
        lat: Double?, lon: Double?, tipoOrdenacao: Int
    ): MutableLiveData<List<Store>> {

//        Quando for uma nova busca, a busca se inicia a partir da pagina 1
        _page = 1
        _lat = lat
        _lon = lon
        _tipoOrdenacao = tipoOrdenacao


        _storeRep.getActiveStores(_page, _controlIndice, lat, lon, tipoOrdenacao).subscribe({
            if (it != null && it.isNotEmpty()) {
                _stores.postValue(it)
            } else {
                eventErro.postValue(BusinessEvent("Nenhuma loja encontrada."))
            }
        }, {
            eventErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter informações da loja."))
        })

        return _stores
    }

    @SuppressLint("CheckResult")
    fun getNextPage(latitude: Double?, longitude: Double?, tipoOrdenacao: Int): MutableLiveData<List<Store>> {
        //        Quanto for continuar exibindo os dados, ele pega a proxima pagina
        _page += 1
        PAGE_OBSERVER = _page
        Log.d("PAGE_GET_NEXT", "$_page")

        _storeRep.getActiveStores(_page, _controlIndice, latitude, longitude, tipoOrdenacao).subscribe({
            if (it != null && it.isNotEmpty()) {
                _stores.postValue(it)
            }
        }, {
            eventErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter informações da loja."))
        })

        return _stores
    }

}