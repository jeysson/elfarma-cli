package hashtag.alldelivery.ui.home

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.FILTER_REQUEST_CODE
import hashtag.alldelivery.AllDeliveryApplication.Companion.REFRESH_DELAY_TIMER
import hashtag.alldelivery.AllDeliveryApplication.Companion.SORT_FILTER
import hashtag.alldelivery.AllDeliveryApplication.Companion.LAT_LONG
import hashtag.alldelivery.AllDeliveryApplication.Companion.PAGE_OBSERVER
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Address
import hashtag.alldelivery.core.models.BusinessEvent
import hashtag.alldelivery.core.models.Filter
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.receiver.NetworkReceiver
import hashtag.alldelivery.ui.address.AddressViewModel
import hashtag.alldelivery.ui.address.DeliveryAddress
import hashtag.alldelivery.ui.filter.FiltersActivity
import hashtag.alldelivery.ui.lojas.StoresListItemAdapter
import hashtag.alldelivery.ui.lojas.StoresViewModel
import kotlinx.android.synthetic.main.filter_bar_container.*
import kotlinx.android.synthetic.main.filter_fragment.*
import kotlinx.android.synthetic.main.filter_fragment.swipe_refresh
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_fragment.home_cards
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jetbrains.anko.support.v4.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment(), NetworkReceiver.NetworkConnectivityReceiverListener {

    private lateinit var stores: List<Store>
    private val viewModel: StoresViewModel by sharedViewModel()
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var isConnected: Boolean = false
    private lateinit var myView: View
    private lateinit var myAddress: Address
    private val swipeRefresh by lazy { swipe_refresh }

    private lateinit var adapter: StoresListItemAdapter
    private var _storeList: MutableList<Store> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myView = view
        home_cards.layoutManager = LinearLayoutManager(context)
        adapter = StoresListItemAdapter(activity as AppCompatActivity, _storeList)
        home_cards.adapter = adapter
        home_cards.setHasFixedSize(true)

//        Definindo a cor azul para o swipeRefresh
        swipeRefresh.setColorSchemeColors(getColor(view.context, R.color.colorPrimary))

        loading.visibility = View.VISIBLE

        setupObservers()
        carregarLojas()
        carregarUltimoEndereco()
        carregarFiltros()
        carregarTodosEnderecos()
        setScrollView()

        address_with_scheduling.setOnClickListener {
            val intent = Intent(context, DeliveryAddress::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

        swipeRefresh.setOnRefreshListener {
//        Timer para atrazar o inicio do swipeRefresh -> UX
            Handler(Looper.getMainLooper()).postDelayed({
                carregarLojas()
            }, REFRESH_DELAY_TIMER)

        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        this.isConnected = isConnected
        if (!isConnected)
            toast("O dispositivo não está conectado")
        else
            carregarLojas()
    }

    private fun setupObservers() {
        viewModel.eventErro.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer<BusinessEvent> {
                it?.let {
                    toast(it.message.toString())
                }
            })

        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
    }

    fun showResults(list: ArrayList<Store>) {
        _storeList.addAll(list)
        _storeList.forEach {
            Log.d("STORE_ITENS:", "${it.nomeFantasia}")
        }
        Log.d("STORE_ITENS:", "Pagina : $PAGE_OBSERVER")
        adapter.notifyDataSetChanged()
    }

    fun carregarLojas() {

        viewModel.getActiveStores(
            LAT_LONG?.latitude,
            LAT_LONG?.longitude,
            SORT_FILTER
        ).observe(viewLifecycleOwner, Observer<List<Store>> {
            it?.let {
                var x = arrayListOf<Store>(Store())
                x.addAll(it)

                showResults(x)

                loading.visibility = View.GONE
            }
        })

//        Timer para atrazar o encerramento do swipeRefresh -> UX
        Handler(Looper.getMainLooper()).postDelayed({
            swipeRefresh.isRefreshing = false
        }, REFRESH_DELAY_TIMER)

    }

    private fun setScrollView() {
        home_cards.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val target = recyclerView.layoutManager as LinearLayoutManager?

                val totalItemCount = target!!.itemCount

                val lastVisible = target.findLastVisibleItemPosition()

                val lastItem = lastVisible + 5 >= totalItemCount

                if (totalItemCount > 0 && lastItem) {
                    viewModel.getNextPage(
                        LAT_LONG?.latitude,
                        LAT_LONG?.longitude,
                        SORT_FILTER
                    ).observe(viewLifecycleOwner, Observer<List<Store>> {
                        it?.let {
                            var x = arrayListOf<Store>(Store())
                            x.addAll(it)
                            showResults(x)
                        }
                    })
                }
            }
        })
    }

    private fun carregarTodosEnderecos() {
        addressViewModel.getAll().observe(viewLifecycleOwner) {
            AllDeliveryApplication.ADDRESS_LIST.addAll(it)
        }
    }

    private fun carregarUltimoEndereco() = GlobalScope.async {
        myAddress = addressViewModel.firstAddress()
        address.text = getString(R.string.address_list_location_activate)

        if (myAddress != null) {
            AllDeliveryApplication.ADDRESS = myAddress
            AllDeliveryApplication.LAT_LONG = LatLng(myAddress.lat!!, myAddress.longi!!)

            address.text = AllDeliveryApplication.getShortAddress(
                myView.context,
                myAddress.lat!!,
                myAddress.longi!!,
                myAddress.number
            )
        }
    }

    private fun carregarFiltros() = GlobalScope.async {
        val list = ArrayList<Filter>()
        val f1 = Filter(getString(R.string.filtros))
        list.add(f1)

        val adapter = FilterListAdapter(list) {
            val intent = Intent(myView.context, FiltersActivity::class.java)
            startActivityForResult(intent, FILTER_REQUEST_CODE)
        }
        adapter.setFilter(list)
        quick_filters.adapter = adapter
        quick_filters.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
//              Se RequestCode e resultCode forem verdadeiros, é porque o user clicou em mostrar resultados
//              Timer para atrazar o encerramento do swipeRefresh -> UX

                loading.visibility = VISIBLE
                home_cards.visibility = GONE

                Handler(Looper.getMainLooper()).postDelayed({
                    home_cards.visibility = VISIBLE
                    carregarLojas()
                }, REFRESH_DELAY_TIMER)

            }
        }
    }

    override fun onResume() {

        super.onResume()

        AllDeliveryApplication.LAT_LONG?.let {
            address.text = AllDeliveryApplication.getShortAddress(
                myView.context, AllDeliveryApplication.ADDRESS!!.lat!!,
                AllDeliveryApplication.ADDRESS!!.longi!!,
                AllDeliveryApplication.ADDRESS!!.number!!
            )
        }
    }


}
