package hashtag.alldelivery.ui.address

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Address
import kotlinx.android.synthetic.main.address_map_toolbar.*
import kotlinx.android.synthetic.main.address_detail_activity.*
import java.util.*

class DetailAddress : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var address: List<android.location.Address>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.address_detail_activity)

//        supportActionBar?.hide()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.search_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)

        back_button.setOnClickListener {
            AllDeliveryApplication.ADDRESS = null
            finish()
        }

        var latlong = AllDeliveryApplication.LAT_LONG
        var geoCoder = Geocoder(baseContext, Locale.getDefault())

        address = geoCoder.getFromLocation(latlong!!.latitude, latlong.longitude, 1)
        header_title_address.text = address[0].thoroughfare //+ ", " + address[0].featureName
        header_subtitle_address.text =
            address[0].subLocality + ", " + address[0].subAdminArea + " - " + address[0].adminArea

        if (AllDeliveryApplication.ADDRESS != null && AllDeliveryApplication.EDIT) {
            var add = AllDeliveryApplication.ADDRESS
            header_title_address.text = add!!.address + ", " + add.number
            header_subtitle_address.text = add.neighborhood + ", " + add.city + " - " + add.state
            number_input.setText(add.number)
            complement_input.setText(add.complement)
            reference_input.setText(add.landmark)
        }

        number_input.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    header_title_address.text = address[0].thoroughfare + ", " + s.toString()
                }
            }
        )

        save_button.setOnClickListener {
            if (number_input.text.toString().isNullOrBlank()) {
                Toast.makeText(this, "Informe o número!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val addressList = AllDeliveryApplication.ADDRESS_LIST
            val latLng = AllDeliveryApplication.LAT_LONG!!
            val add = Address( )
            var isUpdateAddress = false

            if(AllDeliveryApplication.ADDRESS != null) {
                add.id = AllDeliveryApplication.ADDRESS!!.id
            }

            add.address = address[0].thoroughfare
            add.number = number_input.text.toString()
            add.complement = complement_input.text.toString()
            add.neighborhood = address[0].subLocality
            add.city = address[0].subAdminArea
            add.state = address[0].adminArea
            add.landmark = reference_input.text.toString()
            add.lat = latLng.latitude
            add.longi = latLng.longitude

            /*Deve verificar se já existe um item no banco com o mesmo id*/
            for (i in 0 until addressList.size) {
                if (addressList[i].id == add.id) {

                    val two = addressList[i].id
                    add.id = AllDeliveryApplication.ADDRESS!!.id
                    isUpdateAddress = true
                }
            }

            /*  Atualiza ou insere, de acordo com a variável*/
            if (isUpdateAddress){
                addressViewModel.update(add)
            } else if(!isUpdateAddress) {
                addressViewModel.insert(add)
            }

            /*AllDeliveryApplication.address = add*/
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap != null) {
            mMap = googleMap
        }
        // Add a marker in Sydney and move the camera
        var latLng = AllDeliveryApplication.LAT_LONG!!
        val position = LatLng(latLng!!.latitude, latLng!!.longitude)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f))
        map_pin.visibility = View.VISIBLE
    }
}