package hashtag.alldelivery.ui.home

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.BusinessEvent
import hashtag.alldelivery.core.models.Filter
import hashtag.alldelivery.core.models.Store
import hashtag.alldelivery.core.receiver.NetworkReceiver
import hashtag.alldelivery.ui.address.AddressViewModel
import hashtag.alldelivery.ui.address.DeliveryAddress
import hashtag.alldelivery.ui.lojas.StoresListItemAdapter
import hashtag.alldelivery.ui.lojas.StoresViewModel
import kotlinx.android.synthetic.main.filter_bar_container.*
import kotlinx.android.synthetic.main.filter_fragment.*
import kotlinx.android.synthetic.main.fragment_home.address
import kotlinx.android.synthetic.main.fragment_home.home_cards
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jetbrains.anko.support.v4.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.scope.ScopeCallback

class HomeFragment : Fragment(), NetworkReceiver.NetworkConnectivityReceiverListener  {

    private lateinit var stores: List<Store>
    private val viewModel: StoresViewModel by sharedViewModel()
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var isConnected: Boolean = false

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE


        address_with_scheduling.setOnClickListener {
            val intent = Intent(context, DeliveryAddress::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

        setupObservers()
        carregarLojas()
        address.text = getString(R.string.address_list_location_activate)
        carregarEndereco ()
        carregarFiltros()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        this.isConnected = isConnected
        if(!isConnected)
            toast("O dispositivo não está conectado")
        else
            carregarLojas()
    }

    private fun setupObservers() {
        viewModel.eventoErro.observe(this, androidx.lifecycle.Observer<BusinessEvent> {
            it?.let {
                toast(it.message.toString())
            }
        })

        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
    }

    private fun carregarLojas()
    {
        viewModel.getActiveStores().observe(this, Observer<List<Store>> {
            it?.let {
                var x = arrayListOf<Store>(Store())
                x.addAll(it)
                val adapter = StoresListItemAdapter(activity as AppCompatActivity, x)
                home_cards.layoutManager = LinearLayoutManager(context)
                home_cards.adapter = adapter
                loading.visibility = View.GONE
            }
        })
    }

    private fun carregarEndereco() = GlobalScope.async{
        var add = addressViewModel.firstAddress()
        if(add != null) {
            AllDeliveryApplication.address = add
            AllDeliveryApplication.latlong = LatLng(add.lat!!, add.longi!!)

            address.text = getString(R.string.address_found_near,
                context?.let { AllDeliveryApplication.getShortAddress(it, add.lat!!, add.longi!!) })
        }
    }

    private fun carregarFiltros() = GlobalScope.async{
        var list = ArrayList<Filter>()
        var f1 = Filter("nome 1")
        list.add(f1)
        var f2 = Filter("nome 2")
        list.add(f2)
        var f3 = Filter("nome 3")
        list.add(f3)

        var adapter = FilterListAdapter(list)
        adapter.setFilter(list)
        quick_filters.adapter = adapter
        quick_filters.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun onResume() {

        super.onResume()

        AllDeliveryApplication.latlong?.let {
            address.text = getString(R.string.address_found_near,
                context?.let { AllDeliveryApplication.getShortAddress(it, AllDeliveryApplication.latlong!!.latitude,
                    AllDeliveryApplication.latlong!!.longitude) })
        }


    }
}