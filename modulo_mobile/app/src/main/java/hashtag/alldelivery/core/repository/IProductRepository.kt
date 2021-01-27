package hashtag.alldelivery.core.repository

import hashtag.alldelivery.core.models.Group
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.ProductImage
import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response

interface IProductRepository {

    fun getAllProducts(id: Int?): Observable<List<Product>>

    fun getAllGroups(id: Int?): Observable<List<Group>>

    fun getProductsGroup(store: Int?, group: Int?): Call<ArrayList<Product>>

    fun getProductsGroupAsync(store: Int?, group: Int?): Observable<List<Product>>

    fun getImages(id: Int?): Call<List<ProductImage>>

    fun getImagesAsync(id: Int?): Observable<List<ProductImage>>

    fun getPagingProducts(store: Int?, group: Int?, page: Int?, total: Int?): Call<ArrayList<Product>>
}