package hashtag.elfarma.ui.products

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hashtag.elfarma.core.models.*
import hashtag.elfarma.core.repository.IProductRepository
import hashtag.elfarma.core.utils.SingleLiveEvent
import kotlin.concurrent.thread

class ProductViewModel(private val producRep: IProductRepository) : ViewModel() {

    var adapterProduct: ProductAdapter? = null
    var adapterGroup: GroupProductsAdapter? = null
    var groups: MutableLiveData<List<Group>> = MutableLiveData()
    var products: MutableLiveData<ArrayList<Product>> = MutableLiveData()
    var images: MutableLiveData<List<ProductImage>> = MutableLiveData()
    var eventoProductSearch = SingleLiveEvent<ArrayList<Product>>()
    var eventoErro = SingleLiveEvent<BusinessEvent>()
    var loading: SingleLiveEvent<Boolean> = SingleLiveEvent()

    @SuppressLint("CheckResult")
    fun getAllProducts(id: Int?): MutableLiveData<ArrayList<Product>> {

        producRep.getAllProducts(id).subscribe({
            if(it != null && it.isNotEmpty()) {
                products.postValue(it)
            } else {
                eventoErro.postValue(BusinessEvent("Nenhum produto encontrado."))
            }
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter os produtos para a loja."))
        })

        return products
    }

    fun getGroupProducts(store: Int?, group: Int?) :MutableLiveData<ArrayList<Product>> {

        producRep.getProductsGroup(store, group).subscribe( {

            var grp = adapterGroup?.groups?.firstOrNull(){p-> p.id == group}
            grp?.products = it
            var idx = adapterGroup?.groups?.indexOf(grp) as Int
            products.postValue(it)
            adapterGroup?.notifyItemChanged(idx)
            if(it.isNullOrEmpty()) {
                eventoErro.postValue(BusinessEvent("Nenhum produto encontrado."))
            }
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter os produtos para a loja."))
        })
        return products
    }

    @SuppressLint("CheckResult")
    fun getAllGroups(id: Int?): MutableLiveData<List<Group>> {

        loading.postValue(true)

        producRep.getAllGroups(id).subscribe({

            adapterGroup?.groups?.clear()
            adapterGroup?.notifyDataSetChanged()
            adapterGroup?.addItems(it)

            groups.postValue(it)

            if(it.isNullOrEmpty()) {
                eventoErro.postValue(BusinessEvent("Nenhum grupo encontrado."))
            }

            loading.postValue(false)

        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter os grupos para a loja."))
        })

        return groups
    }

     fun getImages(id: Int?): List<ProductImage>{
        return producRep.getImages(id).execute().body()!!
    }

    @SuppressLint("CheckResult")
    fun getImagesAsync(id: Int?): MutableLiveData<List<ProductImage>>{
        producRep.getImagesAsync(id).subscribe({
            images.postValue(it)
            if(it.isNullOrEmpty()) {
                eventoErro.postValue(BusinessEvent("Nenhuma imagem encontrada."))
            }
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter as imagens para o produto."))
        })

        return  images
    }

    fun getPagingProducts(store: Int?, group: Int?, page: Int?, total: Int?) {
        loading.postValue(true)
        producRep.getPagingProducts(store, group, page, total).subscribe({
            var countInicio = adapterProduct?.itens!!.size-1

            adapterProduct?.addItems(it)
            adapterProduct?.notifyItemRangeChanged(countInicio, adapterProduct?.itens!!.size)
            eventoProductSearch.postValue(it)
            loading.postValue(false)
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter as imagens para o produto."))
        })
    }

    fun getPagingProducts(store: Int?, filter: String?, page: Int?, total: Int?) {
        thread(true) {
            loading.postValue(true)
        }

        producRep.getPagingProducts(store, filter, page, total).subscribe({

            if(page == 1){
                adapterProduct?.itens?.clear()
                adapterProduct?.notifyDataSetChanged()
            }
            //
            adapterProduct?.addItems(it)
            //
            eventoProductSearch.postValue(it)
            loading.postValue(false)
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter as imagens para o produto."))
        })
    }

    fun getPagingProducts(filter: String?, page: Int?, total: Int?) {
        thread(true) {
            loading.postValue(true)
        }

        producRep.getPagingProducts(filter, page, total).subscribe({

            if(page == 1){
                adapterProduct?.itens?.clear()
                adapterProduct?.notifyDataSetChanged()
            }
            //
            adapterProduct?.addItems(it)
            //
            eventoProductSearch.postValue(it)
            loading.postValue(false)
        },{
            eventoErro.postValue(BusinessEvent("Erro de conexão. Não foi possível obter as imagens para o produto."))
        })
    }

    fun getImagesGroup(id: Int?): ArrayList<ProductImage>{
        return producRep.getImagesGroupo(id).execute().body()!!
    }
}