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
import kotlinx.android.synthetic.main.fragment_home.address
import kotlinx.android.synthetic.main.fragment_home.home_cards
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myView = view

        loading.visibility = View.VISIBLE


        address_with_scheduling.setOnClickListener {
            val intent = Intent(context, DeliveryAddress::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

        setupObservers()
        carregarLojas()
        carregarEndereco()
        carregarFiltros()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        this.isConnected = isConnected
        if (!isConnected)
            toast("O dispositivo não está conectado")
        else
            carregarLojas()
    }

    private fun setupObservers() {
        viewModel.eventoErro.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer<BusinessEvent> {
                it?.let {
                    toast(it.message.toString())
                }
            })

        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
    }

    private fun carregarLojas() {
        viewModel.getActiveStores().observe(viewLifecycleOwner, Observer<List<Store>> {
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

    private fun carregarEndereco() = GlobalScope.async {
        myAddress = addressViewModel.firstAddress()
        if (myAddress != null) {

            AllDeliveryApplication.address = myAddress
            AllDeliveryApplication.latlong = LatLng(myAddress.lat!!, myAddress.longi!!)

            address.text = AllDeliveryApplication.getShortAddress(
                myView.context,
                myAddress.lat!!,
                myAddress.longi!!,
                myAddress.number
            )
        } else address.text = getString(R.string.address_list_location_activate)
    }

    private fun carregarFiltros() = GlobalScope.async {
        val list = ArrayList<Filter>()
        val f1 = Filter(getString(R.string.filtros))
        list.add(f1)

        val adapter = FilterListAdapter(list) {
            startActivity(Intent(myView.context, FiltersActivity::class.java))
        }
        adapter.setFilter(list)
        quick_filters.adapter = adapter
        quick_filters.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun onResume() {

        super.onResume()

        AllDeliveryApplication.latlong?.let {
            address.text = AllDeliveryApplication.getShortAddress(
                myView.context, AllDeliveryApplication.address!!.lat!!,
                AllDeliveryApplication.address!!.longi!!,
                AllDeliveryApplication.address!!.number!!
            )
        }
    }


}
