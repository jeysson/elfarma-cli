package hashtag.elfarma.core.network

import hashtag.elfarma.core.models.Group
import hashtag.elfarma.core.models.Product
import hashtag.elfarma.core.models.ProductImage
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ProductApi {
    @GET("produto/todos")
    fun getAllProducts(@Header("Authorization") authHeader: String, @Query("loja") id: Int?) : Observable<ArrayList<Product>>

    @GET("produto/grupos")
    fun getAllGroups(@Header("Authorization") authHeader: String, @Query("loja") id: Int?) : Observable<ArrayList<Group>>

    @GET("produto/produtosgrupo")
    fun getProductsGroup(@Header("Authorization") authHeader: String,
                         @Query("loja") store: Int?,
                         @Query("grupo") group: Int?) : Observable<ArrayList<Product>>

    @GET("produto/produtosgrupo")
    fun getProductsGroupAsync(@Header("Authorization") authHeader: String, @Query("loja") store: Int?, @Query("grupo") group: Int?) : Observable<ArrayList<Product>>

    @GET("produto/imagens")
    fun getImages(@Header("Authorization") authHeader: String, @Query("produto") id: Int?) : Call<ArrayList<ProductImage>>

    @GET("produto/imagens")
    fun getImagesAsync(@Header("Authorization") authHeader: String, @Query("produto") id: Int?) : Observable<ArrayList<ProductImage>>

    @GET("produto/paginar")
    fun getPagingProducts(@Header("Authorization") authHeader: String,
                          @Query("loja")store: Int?,
                          @Query("grupo") group: Int?,
                          @Query("indice") page: Int?,
                          @Query("tamanho") total: Int?) : Observable<ArrayList<Product>>

    @GET("produto/buscarporloja")
    fun getPagingProducts(@Header("Authorization") authHeader: String,
                          @Query("loja")store: Int?,
                          @Query("nomeProduto") filter: String?,
                          @Query("indice") page: Int?,
                          @Query("tamanho") total: Int?) : Observable<ArrayList<Product>>

    @GET("produto/buscar")
    fun getPagingProducts(@Header("Authorization") authHeader: String,
                          @Query("nomeProduto") filter: String?,
                          @Query("indice") page: Int?,
                          @Query("tamanho") total: Int?) : Observable<ArrayList<Product>>

    @GET("produto/imagensgrupo")
    fun getImagesGroupo(@Header("Authorization") authHeader: String,
                        @Query("grupo") id: Int?) : Call<ArrayList<ProductImage>>
}