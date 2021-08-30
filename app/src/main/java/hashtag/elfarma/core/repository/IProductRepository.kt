package hashtag.elfarma.core.repository

import hashtag.elfarma.core.models.Group
import hashtag.elfarma.core.models.Product
import hashtag.elfarma.core.models.ProductImage
import io.reactivex.Observable
import retrofit2.Call

interface IProductRepository {

    fun getAllProducts(id: Int?): Observable<ArrayList<Product>>

    fun getAllGroups(id: Int?): Observable<ArrayList<Group>>

    fun getProductsGroup(store: Int?, group: Int?): Observable<ArrayList<Product>>

    fun getProductsGroupAsync(store: Int?, group: Int?): Observable<ArrayList<Product>>

    fun getImages(id: Int?): Call<ArrayList<ProductImage>>

    fun getImagesAsync(id: Int?): Observable<ArrayList<ProductImage>>

    fun getPagingProducts(store: Int?, group: Int?, page: Int?, total: Int?): Observable<ArrayList<Product>>

    fun getPagingProducts(store: Int?, filter: String?, page: Int?, total: Int?): Observable<ArrayList<Product>>

    fun getPagingProducts(filter: String?, page: Int?, total: Int?): Observable<ArrayList<Product>>

    fun getImagesGroupo(id: Int?) : Call<ArrayList<ProductImage>>
}