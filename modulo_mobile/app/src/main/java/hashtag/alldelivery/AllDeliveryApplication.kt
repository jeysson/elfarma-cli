package hashtag.alldelivery

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import hashtag.alldelivery.core.di.repositoryModule
import hashtag.alldelivery.core.di.viewModelModule
import hashtag.alldelivery.core.models.Address
import hashtag.alldelivery.core.models.Order
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.network.networkModule
import org.koin.android.ext.android.startKoin
import wiki.depasquale.mcache.Cache
import java.util.*


class AllDeliveryApplication : Application() {

    companion object {
        var homeFragment: Fragment? = null
        var productDetailFragment: Fragment? = null
        var storeFragment: Fragment? = null

        var Pedido: Order? = null
        var FIRST_VISIBLE = 0
        var LAST_VISIBLE = 0

        var EDIT: Boolean = false
        var LAT_LONG: LatLng? = null
        var ADDRESS: Address? = null
        val ADDRESS_LIST = mutableListOf<Address>()
        lateinit var CONTEXT: Context
        var STORE: Store? = null
        var PRODUCT: Product? = null

        const val REFRESH_DELAY_TIMER: Long = 1000
        const val REFRESH_DELAY_TIMER_STORE: Long = 500
        const val ADDRESS_PREFS = "ADDRESS_PREFS"
        const val ID_KEY = "MY_PERSONAL_ID"

//        Filtro Default 1 (filtro por Localização)
        var SORT_FILTER = 0

        //      RequestCode Control que solicita a exibição de novos itens, vindo da pagina filtros
        const val RESULTS = "RESULTS"
        const val NEW_SEARCH_REQUEST_CODE = 1

        //        Para validação de paginas exibidas por InfinityScrow
        var PAGE_OBSERVER = 1

        //       =======

        fun getAddress(context: Context, lat: Double, long: Double): String {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val address = geoCoder.getFromLocation(lat, long, 1)

            if (address[0].thoroughfare.isNullOrEmpty()){
                address[0].thoroughfare = ""
            }
            if (address[0].subLocality.isNullOrEmpty()){
                address[0].subLocality = ""
            }
            if (address[0].subAdminArea.isNullOrEmpty()){
                address[0].subAdminArea = ""
            }
            if (address[0].adminArea.isNullOrEmpty()){
                address[0].adminArea = ""
            }

            return address[0].thoroughfare + " - " + address[0].subLocality + ", " + address[0].subAdminArea + " - " + address[0].adminArea
        }

        fun getShortAddress(
            context: Context,
            lat: Double,
            long: Double,
            addressNumber: String?
        ): String? {
            if (LAT_LONG != null) {
                var geoCoder = Geocoder(context, Locale.getDefault())
                var address = geoCoder.getFromLocation(lat, long, 1)
                if (addressNumber == null){

                    var value: String? =  ""
                    if (!address[0].thoroughfare.isNullOrEmpty() && !address[0].subLocality.isNullOrEmpty()){
                        value =  address[0].thoroughfare + ", próximo há " + address[0].subLocality
                    }else if (address[0].thoroughfare.isNullOrEmpty() && !address[0].subLocality.isNullOrEmpty()) {
                        value = "Próximo há " + address[0].subLocality
                    }else if (!address[0].thoroughfare.isNullOrEmpty() && address[0].subLocality.isNullOrEmpty()) {
                        value = address[0].thoroughfare
                    }else if (address[0].thoroughfare.isNullOrEmpty() && address[0].subLocality.isNullOrEmpty()){
                        value = null
                    }

                    return value
                } else {
                    return address[0].thoroughfare + ", " + addressNumber
                }
            } else
                return "Ativar localização"
        }
    }

    override fun onCreate() {
        super.onCreate()

        AllDeliveryApplication.CONTEXT = this

        startKoin(
            this, listOf(
                networkModule,
                repositoryModule,
                viewModelModule
            )
        )

        Cache
            //.withGlobalMode(CacheMode.FILE)
            .with(this)

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog() //                    .penaltyDeath()
                    .build()
            )
        }
    }

}