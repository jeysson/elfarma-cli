package hashtag.alldelivery.ui.lojas

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hashtag.alldelivery.core.models.BusinessEvent
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.repository.IStoreRepository
import hashtag.alldelivery.core.utils.SingleLiveEvent

class StoresViewModel(private val storeRep: IStoreRepository) : ViewModel(){

    var stores: MutableLiveData<List<Store>> = MutableLiveData()
    var eventoErro = SingleLiveEvent<BusinessEvent>()

    fun getActiveStores(): MutableLiveData<List<Store>> {

        storeRep.getActiveStores().subscribe({
            if(it != null && it.isNotEmpty()) {
                stores.postValue(it)
            } else {
                eventoErro.postValue(BusinessEvent("Nenhuma loja encontrada."))
            }
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter informações da loja."))
        })

        return stores
    }

}