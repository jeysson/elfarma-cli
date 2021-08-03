package hashtag.alldelivery.core.repository

import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.core.models.Group
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.ProductImage
import hashtag.alldelivery.core.network.ProductApi
import hashtag.alldelivery.core.network.StoreApi
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Query

class ProductRepository(private val dataSource: ProductApi): IProductRepository, BaseRepository() {

    override fun getAllProducts(id: Int?): Observable<ArrayList<Product>> {
        return runOnBackground(dataSource.getAllProducts("Bearer " + AllDeliveryApplication.USER?.token, id))
    }

    override fun getAllGroups(id: Int?): Observable<ArrayList<Group>> {
        return runOnBackground(dataSource.getAllGroups("Bearer " + AllDeliveryApplication.USER?.token, id))
    }

    override fun getProductsGroup(store: Int?, group: Int?): Observable<ArrayList<Product>> {
        return dataSource.getProductsGroup("Bearer " + AllDeliveryApplication.USER?.token, store, group)
    }

    override fun getProductsGroupAsync(store: Int?, group: Int?): Observable<ArrayList<Product>> {
        return runOnBackground(dataSource.getProductsGroupAsync("Bearer " + AllDeliveryApplication.USER?.token, store, group))
    }

    override fun getImages(id: Int?): Call<ArrayList<ProductImage>> {
        return dataSource.getImages("Bearer " + AllDeliveryApplication.USER?.token, id)
    }

    override fun getImagesAsync(id: Int?): Observable<ArrayList<ProductImage>> {
        return runOnBackground(dataSource.getImagesAsync("Bearer " + AllDeliveryApplication.USER?.token, id))
    }

    override fun getPagingProducts(
        store: Int?,
        group: Int?,
        page: Int?,
        total: Int?
    ): Observable<ArrayList<Product>> {
        return dataSource.getPagingProducts("Bearer " + AllDeliveryApplication.USER?.token, store, group, page, total)
    }

    override fun getPagingProducts(
        store: Int?,
        filter: String?,
        page: Int?,
        total: Int?
    ): Observable<ArrayList<Product>> {
        return dataSource.getPagingProducts("Bearer " + AllDeliveryApplication.USER?.token, store, filter, page, total)
    }

    override fun getPagingProducts(
        filter: String?,
        page: Int?,
        total: Int?
    ): Observable<ArrayList<Product>> {
        return dataSource.getPagingProducts("Bearer " + AllDeliveryApplication.USER?.token, filter, page, total)
    }

    override fun getImagesGroupo(id: Int?) : Call<ArrayList<ProductImage>>{
        return dataSource.getImagesGroupo("Bearer " + AllDeliveryApplication.USER?.token, id)
    }
}