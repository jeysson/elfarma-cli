package hashtag.alldelivery

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.google.android.gms.maps.model.LatLng
import hashtag.alldelivery.core.di.repositoryModule
import hashtag.alldelivery.core.di.viewModelModule
import hashtag.alldelivery.core.models.Address
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.network.networkModule
import org.koin.android.ext.android.startKoin
import wiki.depasquale.mcache.Cache
import java.util.*


class AllDeliveryApplication: Application() {

    companion object {
        var edit: Boolean = false
        var latlong: LatLng? = null
        var address : Address? = null
        lateinit var context: Context
        var store: Store? = null
        var product: Product? = null

        fun getAddress(context: Context, lat: Double, long: Double): String{
            var geoCoder = Geocoder(context, Locale.getDefault())
            var address = geoCoder.getFromLocation(lat, long, 1)
            return  address[0].thoroughfare +" - "+ address[0].subLocality+", "+ address[0].subAdminArea+" - "+address[0].adminArea
        }

        fun getShortAddress(context: Context, lat: Double, long: Double, addressNumber: String?): String{
            if(latlong != null){
                var geoCoder = Geocoder(context, Locale.getDefault())
                var address = geoCoder.getFromLocation(lat, long, 1)
                return  address[0].thoroughfare +", "+ addressNumber
            }else
                return  ""
        }
    }

    override fun onCreate() {
        super.onCreate()

        AllDeliveryApplication.context = this

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

        if(BuildConfig.DEBUG) {
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