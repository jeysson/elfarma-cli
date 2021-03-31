package hashtag.alldelivery.core.network

import hashtag.alldelivery.core.network.NetworkProperties.BASE_URL
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module

import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object NetworkProperties {
    const val BASE_URL = "BASE_URL"
}

fun createBaseURL() = "https://elfarmaapi.hashtagmobile.com.br/api/"

fun createConverterFactory(): Converter.Factory {
    return GsonConverterFactory.create()
}

inline fun <reified T> createWebService(baseUrl: String, factory: Converter.Factory/*, sessaoCache: SessaoCache*/): T {

    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val httpClient = OkHttpClient.Builder()
    httpClient.proxy(null)
    httpClient.connectTimeout(500, TimeUnit.MILLISECONDS)
    httpClient.writeTimeout(5, TimeUnit.MINUTES)
    httpClient.readTimeout(5, TimeUnit.MINUTES)
    httpClient.connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
    httpClient.addInterceptor(logging)

    httpClient.addInterceptor {
        val original = it.request()
        val builder = original.newBuilder().method(original.method(), original.body())

        /*sessaoCache.token?.let {
            builder.header("Authorization", "Token $it")
        }*/

        val request = builder.build()

        it.proceed(request)
    }

    val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(factory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
    return retrofit.create(T::class.java)
}

val networkModule = module {
    single (NetworkProperties.BASE_URL, definition = { createBaseURL() })
    single { createConverterFactory() }

    single { createWebService<StoreApi>(get(BASE_URL), get()) }
    single { createWebService<ProductApi>(get(BASE_URL), get()) }
    single { createWebService<OrderApi>(get(BASE_URL), get()) }
}
