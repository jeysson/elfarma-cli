package hashtag.alldelivery.ui.store

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hashtag.alldelivery.core.models.BusinessEvent
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.repository.IStoreRepository
import hashtag.alldelivery.core.utils.SingleLiveEvent

class StoresViewModel(private val _storeRep: IStoreRepository) : ViewModel() {

    var eventErro = SingleLiveEvent<BusinessEvent>()
    private var _stores: MutableLiveData<List<Store>> = MutableLiveData()


    //    Itens de paginação
    private var _page = 1
    private var _controlIndice = 10

    @SuppressLint("CheckResult")
    fun getActiveStores(
        lat: Double?, lon: Double?, tipoOrdenacao: Int
    ): MutableLiveData<List<Store>> {

        _storeRep.getActiveStores(_page, _controlIndice, lat, lon, tipoOrdenacao).subscribe({
            _stores.postValue(it)
            if (it.isNullOrEmpty()) {
                eventErro.postValue(BusinessEvent("Nenhuma loja encontrada."))
            }
        }, {
            eventErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter informações da loja."))
        })

        return _stores
    }

    fun getPagingStores(
        page: Int?,
        total: Int?,
        lat: Double?,
        lon: Double?,
        tipoordenacao: Int?
    ): ArrayList<Store> {
        var resp = _storeRep.getPagingStores(page, total, lat, lon, tipoordenacao).execute()
        return resp.body()!!
    }

}