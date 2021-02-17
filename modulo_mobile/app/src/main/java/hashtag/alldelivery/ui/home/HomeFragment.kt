package hashtag.alldelivery.ui.home

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.maps.model.LatLng
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.ADDRESS
import hashtag.alldelivery.AllDeliveryApplication.Companion.ADDRESS_PREFS
import hashtag.alldelivery.AllDeliveryApplication.Companion.ID_KEY
import hashtag.alldelivery.AllDeliveryApplication.Companion.NEW_SEARCH_REQUEST_CODE
import hashtag.alldelivery.AllDeliveryApplication.Companion.LAT_LONG
import hashtag.alldelivery.AllDeliveryApplication.Companion.REFRESH_DELAY_TIMER
import hashtag.alldelivery.AllDeliveryApplication.Companion.SORT_FILTER
import hashtag.alldelivery.R
import hashtag.alldelivery.component.Loading
import hashtag.alldelivery.core.models.*
import hashtag.alldelivery.core.receiver.NetworkReceiver
import hashtag.alldelivery.ui.address.AddressViewModel
import hashtag.alldelivery.ui.address.DeliveryAddress
import hashtag.alldelivery.ui.filter.FiltersActivity
import hashtag.alldelivery.ui.products.ProductsListItemAdapter
import hashtag.alldelivery.ui.store.StoresListItemAdapter
import hashtag.alldelivery.ui.store.StoresViewModel
import kotlinx.android.synthetic.main.filter_bar_container.*
import kotlinx.android.synthetic.main.filter_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_fragment.home_cards
import kotlinx.android.synthetic.main.product_search_fragment.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment(), NetworkReceiver.NetworkConnectivityReceiverListener {

    private lateinit var stores: List<Store>
    private val _storeViewModel: StoresViewModel by sharedViewModel()
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var isConnected: Boolean = false
    private lateinit var _view: View
    private lateinit var _currentAddress: Address
    private val _swipeRefresh by lazy { _view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh) }
    private val _homeCards by lazy { _view.findViewById<RecyclerView>(R.id.home_cards) }
    private val _homeLoading by lazy { _view.findViewById<Loading>(R.id.loading) }
    private lateinit var _adapter: StoresListItemAdapter
    private var _storeList = ArrayList<Store>()

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page = 1
    var itemsPerPage = 8

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
        //_adapter = StoresListItemAdapter(activity as AppCompatActivity, _storeList)
        //_homeCards.adapter = _adapter
        _homeCards.setHasFixedSize(true)
        _homeCards.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val recyclerLayout = (_homeCards.layoutManager as LinearLayoutManager)

                if (!isLoading && !isLastPage) {
                    //if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    if (recyclerLayout.findLastCompletelyVisibleItemPosition() == (itemsPerPage * page) - 1) {
                        isLoading = true
                        loading.visibility = View.VISIBLE
                        page += 1
                        getMoreItems()
                    }
                }
            }
        })
//        Definindo a cor azul para o swipeRefresh
        _swipeRefresh.setColorSchemeColors(getColor(view.context, R.color.colorPrimary))

        _homeLoading.visibility = VISIBLE

        setupObservers()
        getCurrentAddress()
        loadFilters()
        carregarTodosEnderecos()

        address_with_scheduling.setOnClickListener {
            val intent = Intent(context, DeliveryAddress::class.java)
            startActivityForResult(intent, NEW_SEARCH_REQUEST_CODE)
//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

        _swipeRefresh.setOnRefreshListener {
//        Timer para atrazar o inicio do swipeRefresh -> UX
            Handler(Looper.getMainLooper()).postDelayed({
                lifecycleScope.launch(Dispatchers.IO) {
                    getItems()
                }
            }, AllDeliveryApplication.REFRESH_DELAY_TIMER)

        }

        Handler(Looper.getMainLooper()).postDelayed({

        }, AllDeliveryApplication.REFRESH_DELAY_TIMER)
    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        this.isConnected = isConnected
        if (!isConnected)
            toast("O dispositivo não está conectado")
        else{
            page = 1
            getMoreItems()
        }
    }

    private fun setupObservers() {
        _storeViewModel.eventErro.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer<BusinessEvent> {
                it?.let {
                    toast(it.message.toString())
                }
            })

        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
    }

    private fun carregarTodosEnderecos() {
        addressViewModel.getAll().observe(viewLifecycleOwner) {
            AllDeliveryApplication.ADDRESS_LIST.addAll(it)
        }
    }

    private fun getCurrentAddress() = GlobalScope.async {
        var preferenceAddress : Int = -1

        activity?.apply {
//            Pega o Id Salvo no sharedPreferences
            val preferences = getSharedPreferences(
                ADDRESS_PREFS,
                MODE_PRIVATE
            )
            preferenceAddress = preferences.getInt(ID_KEY, -1)
        }

        address.text = getString(R.string.address_list_location_activate)

        if (preferenceAddress == -1){
            _currentAddress = addressViewModel.firstAddress()
        }else {
            _currentAddress = addressViewModel.loadById(preferenceAddress)
        }

        if (_currentAddress != null) {
            ADDRESS = _currentAddress
            LAT_LONG = LatLng(_currentAddress.lat!!, _currentAddress.longi!!)

            address.text = AllDeliveryApplication.getShortAddress(
                _view.context,
                _currentAddress.lat!!,
                _currentAddress.longi!!,
                _currentAddress.number
            )

            lifecycleScope.launch(Dispatchers.IO) {
                getItems()
            }
        }else {
            lifecycleScope.launch(Dispatchers.IO) {
                getItems()
            }
        }
    }

    private fun loadFilters() = GlobalScope.async {
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
//              Se RequestCode e resultCode forem verdadeiros, é porque o user clicou em mostrar resultados
//              Timer para atrazar o encerramento do swipeRefresh -> UX
                _homeLoading.visibility = VISIBLE
                _homeCards.visibility = GONE
                page = 1
                lifecycleScope.launch(Dispatchers.IO) {
                    getItems()
                }
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
        }

    }

    private suspend fun getItems() {
        Log.d("INICIO", "incializando...")
        isLoading = true
        val homeFragment = this
        //config adapter
        _storeList = _storeViewModel.getPagingStores(
            page,
            itemsPerPage,
            LAT_LONG?.latitude,
            LAT_LONG?.longitude,
            SORT_FILTER
        )

        withContext(Dispatchers.Main) {
            _adapter = StoresListItemAdapter(
                homeFragment,
                _homeCards.layoutManager as LinearLayoutManager,
                _storeList
            )
            _homeCards.adapter = _adapter
            _adapter.notifyDataSetChanged()

            loading.visibility = INVISIBLE
            _homeCards.visibility = VISIBLE
            _swipeRefresh.isRefreshing = false
            isLoading = false
        }
    }

    fun getMoreItems() {
        Log.d("MAIS", "obtendo...")
        _homeCards.post {
            lifecycleScope.launch(Dispatchers.IO) {
                _storeList = _storeViewModel.getPagingStores(
                    page,
                    itemsPerPage,
                     LAT_LONG?.latitude,
                    LAT_LONG?.longitude,
                    SORT_FILTER
                )
                withContext(Dispatchers.Main) {
                    _adapter.addItems(_storeList)
                    _adapter.notifyDataSetChanged()
                    _homeLoading.visibility = INVISIBLE
                    _homeCards.visibility = VISIBLE
                }
                isLastPage = _storeList.size == 0
                isLoading = false
            }
        }
    }
}
