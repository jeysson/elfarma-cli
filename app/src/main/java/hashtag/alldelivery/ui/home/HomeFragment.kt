package hashtag.alldelivery.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.ADDRESS
import hashtag.alldelivery.AllDeliveryApplication.Companion.FIRST_VISIBLE
import hashtag.alldelivery.AllDeliveryApplication.Companion.LAST_VISIBLE
import hashtag.alldelivery.AllDeliveryApplication.Companion.LAT_LONG
import hashtag.alldelivery.AllDeliveryApplication.Companion.NEW_SEARCH_REQUEST_CODE
import hashtag.alldelivery.AllDeliveryApplication.Companion.SORT_FILTER
import hashtag.alldelivery.AllDeliveryApplication.Companion.STORE
import hashtag.alldelivery.R
import hashtag.alldelivery.component.Loading
import hashtag.alldelivery.core.models.*
import hashtag.alldelivery.core.receiver.NetworkReceiver
import hashtag.alldelivery.ui.address.AddressViewModel
import hashtag.alldelivery.ui.address.DeliveryAddress
import hashtag.alldelivery.ui.filter.FiltersActivity
import hashtag.alldelivery.ui.store.StoreFragment
import hashtag.alldelivery.ui.store.StoresAdapter
import hashtag.alldelivery.ui.store.StoresViewModel
import kotlinx.android.synthetic.main.address_list_item.*
import kotlinx.android.synthetic.main.filter_bar_container.*
import kotlinx.android.synthetic.main.filter_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.publi_home.*
import org.jetbrains.anko.support.v4.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment(), NetworkReceiver.NetworkConnectivityReceiverListener, View.OnClickListener  {

    var PERMISSION_ID = 1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    val _storeViewModel: StoresViewModel by sharedViewModel()
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var isConnected: Boolean = false
    private lateinit var _view: View
    private var _currentAddress: MutableLiveData<Address> = MutableLiveData()
    private val _swipeRefresh by lazy { _view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh) }
    private val _homeCards by lazy { _view.findViewById<RecyclerView>(R.id.home_cards) }
    private val _homeLoading by lazy { _view.findViewById<Loading>(R.id.loading) }

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page = 1
    var itemsPerPage = 10

    var adapter: PubliSliderAdapter? = null
    lateinit var list: IntArray
    lateinit var dots: Array<TextView?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.setLightMode(activity)

        _view = view
        _homeCards.layoutManager = LinearLayoutManager(context)
        _homeCards.itemAnimator = DefaultItemAnimator()
        _homeCards.setHasFixedSize(true)
        //_homeCards.setItemViewCacheSize(1024 * 1024 * 5)

        _swipeRefresh.setColorSchemeColors(getColor(view.context, R.color.colorPrimary))
        addressViewModel  = ViewModelProvider(this).get(AddressViewModel::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        address.text = getString(R.string.address_list_location_activate)
        //loadPubli()
        getLastLocation()

        initAdapter()
        scrollListener()
        setupObservers()
        loadFilters()
        carregarTodosEnderecos()

        address_with_scheduling.setOnClickListener {
            selectAddress()
        }

        _swipeRefresh.setOnRefreshListener {
            getItems()
        }
    }

    fun initAdapter(){
        _storeViewModel.adapter = StoresAdapter(
            this
        )

        _storeViewModel.adapter?.itens = ArrayList<Store>()
        _homeCards.adapter = _storeViewModel.adapter
    }

    private fun scrollListener() {
        _homeCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val recyclerLayout = (_homeCards.layoutManager as LinearLayoutManager)

                if (!isLoading && !isLastPage) {
                    if (recyclerLayout.findLastCompletelyVisibleItemPosition() == (itemsPerPage * page) - 1) {
                        //                      corrige bug de fade in quando atualiza o adapter
                        FIRST_VISIBLE = recyclerLayout.findFirstVisibleItemPosition()
                        LAST_VISIBLE = recyclerLayout.findLastVisibleItemPosition()

                        isLoading = true
                        loading.visibility = VISIBLE
                        page += 1
                        getMoreItems()
                    }
                }
            }
        })
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        this.isConnected = isConnected
        if (!isConnected)
            toast("O dispositivo não está conectado")
        else {
            page = 1
            isLastPage = false
            getItems()
        }
    }

    private fun setupObservers() {
        /*escuta o carregamento dos endereços*/
        _currentAddress.observe(viewLifecycleOwner){
            if(it == null)
                selectAddress()
            else
                getItems()
        }

        /*escuta o retorno das consultas*/
        _storeViewModel.loading.observe(viewLifecycleOwner){

            isLoading = it

            if(it) {
                _homeLoading.visibility = View.VISIBLE

                if(page== 1)
                    _homeCards.visibility = View.GONE
            }else{
                _swipeRefresh.isRefreshing = false
                _homeLoading.visibility = View.INVISIBLE
                _homeCards.visibility = View.VISIBLE
            }
        }

        /*escuta se chegou na ultima página*/
        _storeViewModel.lastPage.observe(viewLifecycleOwner){
            isLastPage = it
        }

        _storeViewModel.eventErro.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                toast(it.message.toString())
            }
        }

        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
    }

    private fun carregarTodosEnderecos() {
        addressViewModel.getAll().observe(viewLifecycleOwner) {
            AllDeliveryApplication.ADDRESS_LIST.addAll(it)
        }
    }

    private fun getCurrentAddress() {

       addressViewModel.firstAddress().observe(viewLifecycleOwner){
            _currentAddress.postValue(it)

            if (it != null) {
                ADDRESS = it
                LAT_LONG = LatLng(it?.lat!!, it?.longi!!)

                address.text = AllDeliveryApplication.getShortAddress(
                    _view.context,
                    it?.lat!!,
                    it?.longi!!,
                    it?.number
                )
            }
        }
    }

    private fun loadFilters() {
        val list = ArrayList<Filter>()
        val f1 = Filter(getString(R.string.filtros))
        list.add(f1)

        val adapter = FilterListAdapter(list) {
            val intent = Intent(_view.context, FiltersActivity::class.java)
            startActivityForResult(intent, NEW_SEARCH_REQUEST_CODE)
        }
        adapter.setFilter(list)
        quick_filters.adapter = adapter
        quick_filters.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_SEARCH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
//              Busca realizada por outras páginas
                _homeLoading.visibility = VISIBLE
                _homeCards.visibility = GONE
                page = 1
                getItems()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (ADDRESS?.id != null) {
            LAT_LONG?.let {
                address.text = AllDeliveryApplication.getShortAddress(
                    _view.context, ADDRESS!!.lat!!,
                    ADDRESS!!.longi!!,
                    ADDRESS!!.number!!
                )
            }
        } else if (LAT_LONG != null) {
            LAT_LONG?.let {
                address.text = AllDeliveryApplication.getShortAddress(
                    _view.context, LAT_LONG!!.latitude,
                    LAT_LONG!!.longitude,
                    null
                )
            }
        }else{
            toast("Selecione seu endereço!")
           // selectAddress()
        }
    }

    private fun getItems() {
        if(LAT_LONG != null){
            page = 1
            isLastPage = false
            _homeCards.visibility = GONE
            //config adapter
            _storeViewModel.getPagingStores(
                page,
                itemsPerPage,
                LAT_LONG?.latitude,
                LAT_LONG?.longitude,
                SORT_FILTER
            )
        }else{
            _swipeRefresh.isRefreshing = false
            selectAddress()
        }
    }

    fun getMoreItems() {
        _storeViewModel.getPagingStores(
            page,
            itemsPerPage,
            LAT_LONG?.latitude,
            LAT_LONG?.longitude,
            SORT_FILTER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("[Address]", "Permissão concedida!")
                getLastLocation()
            }else
                activity?.finish()
        }
    }

    private fun getLastLocation() {
        if (CheckPermission()) {
            if (isLocationEnable()) {
                if (checkSelfPermission(
                        activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        activity!!,
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

                getCurrentAddress()
            } else {
                toast("Por favor, habilite seu serviço de localização!")
            }
        } else {
            RequestPermission()
        }
    }

    private fun isLocationEnable(): Boolean {
        var location = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return location.isProviderEnabled(LocationManager.GPS_PROVIDER) || location.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!,
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
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    fun CheckPermission(): Boolean {
        return checkSelfPermission(
            activity!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(
            activity!!,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun RequestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            var lastLocation = p0?.lastLocation

            if (lastLocation != null) {
                AllDeliveryApplication.LAT_LONG =
                    LatLng(lastLocation.latitude, lastLocation.longitude)
            }

            description_address.text = AllDeliveryApplication.getAddress(
                activity!!,
                lastLocation!!.latitude,
                lastLocation.longitude
            )
        }
    }

    fun selectAddress(){
        val intent = Intent(context, DeliveryAddress::class.java)
        startActivityForResult(intent, NEW_SEARCH_REQUEST_CODE)
    }

    override fun onClick(view: View?) {
        if (view is CardView) {

            var position = view!!.tag as Int
            STORE = _storeViewModel.adapter?.itens!!.get(position)
            val manager: FragmentManager = activity!!.supportFragmentManager
            manager.beginTransaction()
            manager.commit(true) {
                setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )

                addToBackStack(null)
                replace(R.id.nav_host_fragment, StoreFragment::class.java, null)
            }
        }
    }


    fun loadPubli(){

        dots = arrayOfNulls<TextView>(6)

        list = IntArray(6)
        list[0] = R.mipmap.ic_vitaminac_foreground
        list[1] = R.mipmap.ic_dorflex_foreground
        list[2] = R.mipmap.ic_hypera_foreground
        list[3] = R.mipmap.ic_fenaflex_foreground
        list[4] = R.mipmap.ic_skincare_foreground
        list[5] = R.mipmap.ic_vitamedley_foreground

        adapter = PubliSliderAdapter(list)
        image_container.setAdapter(adapter)

        setIndicators()

        /*image_container.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectedDots(position)
                super.onPageSelected(position)
            }
        })*/
    }

    private fun selectedDots(position: Int) {
        for (i in dots.indices) {
            if (i == position) {
                dots.get(i)?.setTextColor(list.get(position))
            } else {
                dots.get(i)?.setTextColor(resources.getColor(R.color.black_overlay))
            }
        }
    }

    private fun setIndicators() {
        for (i in dots.indices) {
            dots[i] = TextView(this.context)
            dots.get(i)?.setText(Html.fromHtml("&#9679;"))
            dots.get(i)?.setTextSize(18f)
            dots_container.addView(dots.get(i))
        }
    }
}
