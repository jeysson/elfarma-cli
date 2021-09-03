package hashtag.elfarma.ui.splash

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.core.async.JsonPostData
import hashtag.elfarma.core.models.Login
import hashtag.elfarma.core.models.Message
import hashtag.elfarma.core.models.User
import hashtag.elfarma.core.utils.DateDeserializer
import hashtag.elfarma.core.utils.OnTaskCompleted
import hashtag.elfarma.ui.address.AddressViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.splash.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity(), OnTaskCompleted {

    var PERMISSION_ID = 1000

    private lateinit var addressViewModel: AddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash)
        supportActionBar?.hide()
       /* window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
       window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)*/
        contentSplash.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        carregarTodosEnderecos()

    }

    private fun carregarTodosEnderecos() = doAsync {

        var list = addressViewModel.getAll()
        //
        if(list != null) {
            AllDeliveryApplication.ADDRESS_LIST.addAll(list)
            //
            getLastLocation()
        }
    }

    fun loadUser(){

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {

                //  Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val currentUser = Firebase.auth.currentUser

            if (currentUser != null) {
                val login = Login()
                login.email = currentUser.email
                login.tokenFCM = task.result
                //
                JsonPostData(
                    AllDeliveryApplication.APIAddress.LOGIN.toString(), login,
                    this, ""
                ).execute()
            } else {
                val login = Login()
                login.tokenFCM = task.result

                JsonPostData(
                    AllDeliveryApplication.APIAddress.LOGINTOKEN.toString(), login,
                    this, ""
                ).execute()
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("[Address]", "Permissão concedida!")
                getLastLocation()
            }else
                finish()
        }
    }

    private fun getLastLocation() = doAsync {
        if (CheckPermission()) {
            if (isLocationEnable()) {
                if (ContextCompat.checkSelfPermission(
                        this@SplashActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        this@SplashActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                getCurrentAddress()
            } else {
                toast("Por favor, habilite seu serviço de localização!")
            }
        } else {
            RequestPermission()
        }
    }

    fun CheckPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun RequestPermission(){
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun isLocationEnable(): Boolean {
        var location = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return location.isProviderEnabled(LocationManager.GPS_PROVIDER) || location.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getCurrentAddress() {

        var add = addressViewModel.firstAddress()

        if (add != null) {
            AllDeliveryApplication.ADDRESS = add
            AllDeliveryApplication.LAT_LONG = LatLng(add?.lat!!, add?.longi!!)
        }

        loadUser()
    }

    override fun onTaskCompleted(data: JSONObject?) {

        if (data != null) {
            try {
                val mm: Message = Gson().fromJson<Any>(
                    data.toString(),
                    object : TypeToken<Message?>() {}.type
                ) as Message
                if (mm.code!! > 300) {
                    throw Exception(mm.message)
                } else {
                    val gson = GsonBuilder().registerTypeAdapter(
                        Date::class.java,
                        DateDeserializer()
                    ).create()
                    val user: User = gson.fromJson(
                        data.getJSONObject("dados").toString(),
                        object : TypeToken<User?>() {}.type
                    )
                    //
                    if(Firebase.auth.currentUser != null){
                        AllDeliveryApplication.USER = user
                    }else {

                        AllDeliveryApplication.USER = User()
                        AllDeliveryApplication.USER?.token = user.token
                        AllDeliveryApplication.USER?.tokenFCM = user.tokenFCM
                        AllDeliveryApplication.USER?.anonimo = true
                    }

                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } catch (e: JSONException) {

                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {

                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}