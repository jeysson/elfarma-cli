package hashtag.elfarma.ui.address

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.R
import kotlinx.android.synthetic.main.address_map_toolbar.*
import kotlinx.android.synthetic.main.address_set.*
import org.jetbrains.anko.toast
import java.util.*

class AddressSet : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {

    private lateinit var mMap: GoogleMap
    private lateinit var latLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.address_set)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.search_map) as SupportMapFragment
        mapFragment.getMapAsync(this)


//        Quebrando aplicação quando exibe o mapa
//        supportActionBar!!.hide()

        back_button.setOnClickListener{
            finish()
        }

        continue_button.setOnClickListener{
            if(AllDeliveryApplication.LAT_LONG != null) {
                val intent = Intent(this, DetailAddress::class.java)
                // start your next activity
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                finish()
            }else{
                toast("Selecione um endereço!")
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        if(AllDeliveryApplication.LAT_LONG != null) {
            mMap = googleMap
            mMap.setOnCameraMoveStartedListener(this)
            mMap.setOnCameraIdleListener(this)

            // Add a marker in Sydney and move the camera
            latLng = AllDeliveryApplication.LAT_LONG!!
            val sydney = LatLng(latLng!!.latitude, latLng!!.longitude)
            //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18.0f))
        }
    }

    @SuppressLint("RestrictedApi")
    private fun DefButton(){
        location_fab.visibility = View.GONE

    }

    override fun onCameraMoveStarted(p0: Int) {
        map_pin.setImageResource(R.drawable.ic_map_pin_hover)
        continue_button.visibility = View.INVISIBLE
        continue_button.text = getString(R.string.new_address_confirmation_button_text)
    }

    override fun onCameraIdle() {
        map_pin.setImageResource(R.drawable.ic_map_pin)
        continue_button.visibility = View.VISIBLE
        continue_button.text = getString(R.string.new_address_confirmation_button_text)

        var latlong = mMap.cameraPosition.target
        var geoCoder = Geocoder(baseContext, Locale.getDefault())
        var address = geoCoder.getFromLocation(latlong.latitude, latlong.longitude, 1)

        title_address.text =  address[0].thoroughfare + ", " + address[0].featureName
        subtitle_address.text = address[0].subLocality+", "+ address[0].subAdminArea+" - "+address[0].adminArea

        AllDeliveryApplication.LAT_LONG = latlong
        AllDeliveryApplication.EDIT = false
    }
}