package hashtag.elfarma

import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.gms.maps.model.LatLng
import hashtag.elfarma.core.di.repositoryModule
import hashtag.elfarma.core.di.viewModelModule
import hashtag.elfarma.core.models.*
import hashtag.elfarma.core.network.networkModule
import hashtag.elfarma.ui.order.User
import org.koin.android.ext.android.startKoin
import wiki.depasquale.mcache.Cache
import java.util.*


class AllDeliveryApplication : Application() {



    companion object {

        const val CONCLUIDO = 7
        var pedidocheckout: Boolean = false


        // public      static      String      tokenFCM            = FirebaseInstanceId.getInstance().getToken();
        private const val URLADDRESS = "elfarmaapi.hashtagmobile.com.br"

        // private     static      String      URLADDRESS          = "192.168.0.16/mybb.api";
        private const val PROTOCOL = "https://"
        //private     static      String      URLADDRESS          = "192.168.0.38/mybb.api";

        const val SEARCH_NO = 0
        const val SEARCH_STORE = 1
        const val SEARCH_ALL = 2

        var SENDORDER: Boolean = false
        var USER: User? = null
        var Pedido: Order? = null
        var PedidoHistory: OrderHistory? = null
        var OrderEvaluated: Order? = null
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
        const val BAG_ORDER_ADDRESS_CHANGE = 2
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

        fun changeFragment(
            manager: FragmentManager,
            fragment: Class<out Fragment>,
            id: Int,
            anim: Int
        ){
            var frag = manager.findFragmentById(id)

            if(frag == null){
                if(anim == 1){
                    manager.beginTransaction()
                    manager.commit {
                        setCustomAnimations(
                            R.anim.enter_from_left,
                            R.anim.exit_to_right,
                            R.anim.enter_from_right,
                            R.anim.exit_to_left
                        )

                        addToBackStack(null)
                        replace(id, fragment, null)
                    }

                }else{
                    manager.beginTransaction()
                    manager.commit {
                        setCustomAnimations(
                            R.anim.enter_from_up,
                            R.anim.exit_to_down,
                            R.anim.enter_from_down,
                            R.anim.exit_to_up
                        )

                        addToBackStack(null)
                        add(R.id.nav_host_fragment, fragment, null)
                    }
                }
            }else{
                if(anim == 1){
                    manager.beginTransaction()
                    manager.commit {
                        setCustomAnimations(
                            R.anim.enter_from_left,
                            R.anim.exit_to_right,
                            R.anim.enter_from_right,
                            R.anim.exit_to_left
                        )

                        addToBackStack(null)
                        replace(id, frag, "")
                    }

                }else{
                    manager.beginTransaction()
                    manager.commit {
                        setCustomAnimations(
                            R.anim.enter_from_up,
                            R.anim.exit_to_down,
                            R.anim.enter_from_down,
                            R.anim.exit_to_up
                        )

                        addToBackStack(null)
                        add(R.id.nav_host_fragment, frag, null)
                    }
                }
            }
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

    enum class APIAddress(  /*!!***********************************************************************************/
        private val string: String
    ) {
        /*NEW(PROTOCOL + URLADDRESS + "/api/users/new/"),*/

        LOGIN(PROTOCOL + URLADDRESS + "/api/usuario/autenticar/"),
        LOGINTOKEN(PROTOCOL + URLADDRESS + "/api/usuario/autenticartoken/"),
        VERIFICATION(PROTOCOL + URLADDRESS + "/api/usuario/verificar/")/*,
        PASSWORDRECORVER(
            com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/users/passwordrecover/"
        ),
        CHANGEPASSWORD(com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/users/changepassword/"), UPDATE(
            com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/users/update/"
        ),
        UPDATEPHOTOSVALIDATION(com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/photos/updatephotosvalidation/"), UPDATEPHOTO(
            com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/photos/updatephoto/"
        ),
        UPDATEPHOTOS(com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/photos/updatephotos/"), PHOTOSVALIDATION(
            com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/photos/photosvalidation?id=:id"
        ),
        PHOTOS(com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/photos/photos?id=:id"), PHOTO(
            com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/photos/photo?id=:id"
        ),
        DELETEPHOTO(com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/photos/deletephoto/"), TIMELINE(
            com.lovers.cherie.constants.Application.PROTOCOL + com.lovers.cherie.constants.Application.URLADDRESS + "/api/publishers/timeline/"
        )*/;

        override fun toString(): String {
            return string
        } /*!!***********************************************************************************/
    }
}