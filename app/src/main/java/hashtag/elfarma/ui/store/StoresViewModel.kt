package hashtag.elfarma.ui.store

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hashtag.elfarma.AllDeliveryApplication.Companion.STORE
import hashtag.elfarma.core.models.BusinessEvent
import hashtag.elfarma.core.models.Store
import hashtag.elfarma.core.repository.IStoreRepository
import hashtag.elfarma.core.utils.SingleLiveEvent
import kotlin.collections.ArrayList

class StoresViewModel(private val _storeRep: IStoreRepository) : ViewModel(){

    var adapter: StoresAdapter? = null
    var eventErro = SingleLiveEvent<BusinessEvent>()
    var eventLoadLogo = SingleLiveEvent<Int>()
    var eventLoadBanner = SingleLiveEvent<Store>()
    var eventLoadImage = SingleLiveEvent<Int>()
    var stores: MutableLiveData<ArrayList<Store>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var lastPage: MutableLiveData<Boolean> = MutableLiveData()

    //    Itens de paginação
    private var _page = 1
    private var _controlIndice = 10

    @SuppressLint("CheckResult")
    fun getActiveStores(
        lat: Double?, lon: Double?, tipoOrdenacao: Int
    ): MutableLiveData<ArrayList<Store>> {

        _storeRep.getActiveStores(_page, _controlIndice, lat, lon, tipoOrdenacao).subscribe({
            stores.postValue(it)
            if (it.isNullOrEmpty()) {
                eventErro.postValue(BusinessEvent("Nenhuma loja encontrada."))
            }
        }, {
            eventErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter informações da loja."))
        })

        return stores
    }

    fun getPagingStores(
        page: Int?,
        total: Int?,
        lat: Double?,
        lon: Double?,
        tipoordenacao: Int?
    ): MutableLiveData<ArrayList<Store>> {
        loading.postValue(true)

        _storeRep.getPagingStores(page, total, lat, lon, tipoordenacao).subscribe ({
          //  var countInicio = adapter?.itens!!.size-1

            if(page == 1){
                val arr = ArrayList<Store>()
                val mch = Store()
                mch.publi = true
                arr.add(mch)
                val st = Store()
                st.nomeFantasia = "Farmácias e Drogarias"
                st.head = true
                arr.add(st)
                arr.addAll(it)

             //   adapter?.setHasStableIds(true)
                adapter?.itens?.clear()
                adapter?.notifyDataSetChanged()
                adapter?.addItems(arr)
            }else
                adapter?.addItems(it)

             stores.postValue(it)
            if (it.isNullOrEmpty()) {
                eventErro.postValue(BusinessEvent("Nenhuma loja encontrada."))
            }

            lastPage.postValue(it.size == 0)
            loading.postValue(false)

        }, {
            loading.postValue(false)
            eventErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter informações da loja."))
        })
        return stores
    }

    fun getStoreLogo(loja: Int?){
        _storeRep.getStoreLogo(loja).subscribe ({
            var store = adapter?.itens?.firstOrNull { p-> p.id == it.id }
            store?.logo = it.logo
            store?.logoCarregado = true
            var idx = adapter?.itens?.indexOf(store)
            adapter?.notifyItemChanged(idx!!)
            eventLoadLogo.postValue(idx)
        }, {
           // eventErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter informações da loja."))
        })
    }

    fun getStoreBanner(loja: Int?){
        _storeRep.getStoreBanner(loja).subscribe ({

            STORE?.banner = it.banner

            eventLoadBanner.postValue(STORE!!)
        }, {
            //eventErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter informações da loja."))
        })
    }
}