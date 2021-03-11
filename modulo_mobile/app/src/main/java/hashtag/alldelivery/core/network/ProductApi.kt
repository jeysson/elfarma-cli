package hashtag.alldelivery.core.network

import hashtag.alldelivery.core.models.Group
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.ProductImage
import hashtag.alldelivery.core.models.Store
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApi {
    @GET("produto/todos")
    fun getAllProducts(@Query("loja") id: Int?) : Observable<ArrayList<Product>>

    @GET("produto/grupos")
    fun getAllGroups(@Query("loja") id: Int?) : Observable<ArrayList<Group>>

    @GET("produto/produtosgrupo")
    fun getProductsGroup(@Query("loja") store: Int?,
                         @Query("grupo") group: Int?) : Observable<ArrayList<Product>>

    @GET("produto/produtosgrupo")
    fun getProductsGroupAsync(@Query("loja") store: Int?, @Query("grupo") group: Int?) : Observable<ArrayList<Product>>

    @GET("produto/imagens")
    fun getImages(@Query("produto") id: Int?) : Call<ArrayList<ProductImage>>

    @GET("produto/imagens")
    fun getImagesAsync(@Query("produto") id: Int?) : Observable<ArrayList<ProductImage>>

    @GET("produto/paginar")
    fun getPagingProducts(@Query("loja")store: Int?,@Query("grupo") group: Int?,@Query("indice") page: Int?,@Query("tamanho") total: Int?) : Call<ArrayList<Product>>

    @GET("produto/imagensgrupo")
    fun getImagesGroupo(@Query("grupo") id: Int?) : Call<ArrayList<ProductImage>>
}