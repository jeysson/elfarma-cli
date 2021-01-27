package hashtag.alldelivery.ui.address

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Address
import hashtag.alldelivery.ui.lojas.StoresListItemAdapter
import kotlinx.android.synthetic.main.address_find_activity.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class FindAddress : AppCompatActivity() {

    lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.address_find_activity)
        supportActionBar?.hide()

        Places.initialize(this, getString(R.string.google_api_places))
        placesClient = Places.createClient(this)

        back_button.setOnClickListener {
            finish()
        }

        field.addTextChangedListener(
            object : TextWatcher{

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
                    var list  = arrayListOf(Address())
  //                  var geoCoder = Geocoder(this@FindAddress, Locale.getDefault())
//                    var address = geoCoder.getFromLocationName(s.toString(), 4)

                    findPlaces(s.toString(), AllDeliveryApplication.latlong!!.latitude, AllDeliveryApplication.latlong!!.longitude)
    /*                address.forEach {
                        var ad = Address()
                        ad.title = it.thoroughfare
                        ad.description = it.subLocality
                        ad.description2 = it.subAdminArea
                        list.add(ad)
                    }

                    val adapter = AddressListItemAdapter(list)
                    list_address.layoutManager = LinearLayoutManager(this@FindAddress)
                    list_address.adapter = adapter*/
                }
            }
        )
    }

    private fun findPlaces(text: String, lat:Double, long:Double){
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        var token = AutocompleteSessionToken.newInstance();

        // Use the builder to create a FindAutocompletePredictionsRequest.
        var request = FindAutocompletePredictionsRequest.builder()
            // Call either setLocationBias() OR setLocationRestriction().
      //   /*   .setLocationBias(getRetangleBounds(lat, long))
            .setCountry("br")
           // .setTypeFilter(TypeFilter.ESTABLISHMENT)*/
            .setSessionToken(token)
            .setQuery(text)
            .build()


        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            response.autocompletePredictions.forEach { prediction ->
                Log.i("Places", prediction.getPlaceId());
                Log.i("Places", prediction.getPrimaryText(null).toString());
            }
        }.addOnFailureListener{exception ->
            if (exception is ApiException) {
                var apiException = exception as ApiException;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        }.addOnCompleteListener {task->
            task.result!!.autocompletePredictions.forEach {
                    prediction ->
                Log.i("Places 2", prediction.getPlaceId());
                Log.i("Places 2", prediction.getPrimaryText(null).toString());
            }
        }


    }

    private fun getRetangleBounds(lat: Double, long: Double): RectangularBounds? {
        if (lat == null) {
            return null;
        }

        if (long == null) {
            return null;
        }

        return RectangularBounds.newInstance(LatLng(lat - 0.07, long - 0.07), LatLng(lat + 0.07, long + 0.07));
    }
}