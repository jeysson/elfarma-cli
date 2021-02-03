package hashtag.alldelivery.ui.address

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import kotlinx.android.synthetic.main.address_list_item.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.address_delivery_activity.*

class DeliveryAddress : AppCompatActivity() {

    var PERMISSION_ID = 1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private lateinit var addressViewModel: AddressViewModel
    lateinit var adapter: AddressListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.address_delivery_activity)
        supportActionBar?.hide()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        icon_address.setImageResource(R.drawable.ic_location)
        title_address.text = getString(R.string.address_list_location_item_title)
        description_address.text = "Carregando..."
        kebab.visibility = View.GONE

        back_button.setOnClickListener {
            finish()
        }

        location.setOnClickListener {
            finish()
        }

        search_box.setOnClickListener {
            val intent = Intent(this, AddressSet::class.java)
            // start your next activity
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        getLastLocation()
        setupObservers()
    }

    private fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnable()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
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
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result

                    if(location == null){
                        getNewLocation()
                    }else
                    {
                        AllDeliveryApplication.LAT_LONG = LatLng(location.latitude, location.longitude)
                        description_address.text = AllDeliveryApplication.getAddress(baseContext, location.latitude, location.longitude)
                    }
                }
            }else{
                Toast.makeText(this, "Por favor, habilite seu serviço de localização!", Toast.LENGTH_SHORT)
            }
        }else{
            RequestPermission()
        }
    }

    private fun getNewLocation(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private  val locationCallback = object:LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            var lastLocation = p0?.lastLocation

            if (lastLocation != null) {
                AllDeliveryApplication.LAT_LONG = LatLng(lastLocation.latitude, lastLocation.longitude)
            }

            description_address.text = AllDeliveryApplication.getAddress(baseContext, lastLocation!!.latitude, lastLocation.longitude)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       if(requestCode == PERMISSION_ID){
           if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Log.d("[Address]","Permissão concedida!")
               getLastLocation()
           }
       }
    }

    private fun isLocationEnable(): Boolean
    {
        var location = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return location.isProviderEnabled(LocationManager.GPS_PROVIDER) || location.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)
    }

    fun CheckPermission():Boolean{
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun RequestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private fun setupObservers() {

        adapter = AddressListAdapter(this)
        list_address.adapter = adapter
        list_address.layoutManager = LinearLayoutManager(this)
        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        addressViewModel.allAddress.observe(this, Observer { address ->
            // Update the cached copy of the words in the adapter.
            address?.let { adapter.setAddress(it) }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}