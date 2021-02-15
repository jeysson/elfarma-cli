package hashtag.alldelivery.ui.products

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.REFRESH_DELAY_TIMER_STORE
import hashtag.alldelivery.AllDeliveryApplication.Companion.STORE
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Product
import kotlinx.android.synthetic.main.product_search_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ProductSearch : Fragment() {

    private lateinit var _view: View
    private lateinit var _productAdapter: ProductsListItemAdapter
    private lateinit var _productList: ArrayList<Product>
    private val _viewModelProduct: ProductViewModel by sharedViewModel()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page = 1
    var itemsPerPage = 6

    private val _recyclerProductList by lazy { _view.findViewById<RecyclerView>(R.id.recycler_product_list_search) }
    private val _editSearch by lazy { _view.findViewById<EditText>(R.id.edit_text_search_item) }
    private val _cancelButton by lazy { _view.findViewById<Button>(R.id.btn_cancel_search) }

    private lateinit var _fullList: List<Product>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.product_search_fragment, container, false)
        _view = view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.setLightMode(activity)
        getAll()

        //config rv
        _recyclerProductList.layoutManager = GridLayoutManager(context, 2)
        _recyclerProductList.setHasFixedSize(true)
        _recyclerProductList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val recyclerLayout = (_recyclerProductList.layoutManager as GridLayoutManager)
                /*val visibleItemCount = recyclerLayout.childCount
                val totalItemCount = recyclerLayout.itemCount
                val firstVisibleItemPosition = vmm.findFirstVisibleItemPosition()
                 */

                if (!isLoading && !isLastPage) {
                    //if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    if (recyclerLayout.findLastCompletelyVisibleItemPosition() == (itemsPerPage * page) - 1) {
                        isLoading = true
                        loadingSearch.visibility = View.VISIBLE
                        page += 1
                        getMoreItems()
                    }
                }
            }
        })


        _editSearch.doOnTextChanged { text, start, count, after ->
            if (text.isNullOrBlank()) {
                getMoreItems()
                _cancelButton.text = getString(R.string.cancelar)
            } else {
                _cancelButton.text = getString(R.string.limpar)
                if (text.length > 2){
                    findItemByName(text!!)
                }
            }
        }

        _cancelButton.setOnClickListener {
            if (_editSearch.text.isNullOrBlank()){
                activity?.onBackPressed()
            }else {
                _editSearch.setText("")
            }

        }

        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch(Dispatchers.IO) {
                getItems()
            }
        }, REFRESH_DELAY_TIMER_STORE)

    }

    fun getAll() {
        _viewModelProduct.getAllProducts(STORE?.id).observe(viewLifecycleOwner) {
            _fullList = it
        }
    }

    fun getMoreItems() {
        _recyclerProductList.post {
            lifecycleScope.launch(Dispatchers.IO) {
                _productList = _viewModelProduct.getPagingProducts(
                    AllDeliveryApplication.STORE!!.id,
                    -1,
                    page,
                    itemsPerPage
                )
                withContext(Dispatchers.Main) {
                    _productAdapter.addItems(_productList)
                    _productAdapter.notifyDataSetChanged()
                    loadingSearch.visibility = View.INVISIBLE
                }
                isLastPage = _productList.size == 0
                isLoading = false
            }
        }
    }

    private suspend fun getItems() {
        val productSearch = this
        //config adapter
        _productList = _viewModelProduct.getPagingProducts(
            AllDeliveryApplication.STORE!!.id,
            -1,
            page,
            itemsPerPage
        )

        withContext(Dispatchers.Main) {
            _productAdapter = ProductsListItemAdapter(
                productSearch,
                null,
                _recyclerProductList.layoutManager as GridLayoutManager,
                _productList
            )
            _recyclerProductList.adapter = _productAdapter
            _productAdapter.notifyDataSetChanged()
            loadingSearch.visibility = View.INVISIBLE
        }
    }

    private fun findItemByName(text: CharSequence) {
//        Procura na lista completa da loja
        val byName = _fullList.filter { it.nome!!.contains(text, true) }
        val filteredItems = mutableListOf<Product>()
        filteredItems.addAll(byName)

        when {
            filteredItems.isNotEmpty() -> {

                _productAdapter = ProductsListItemAdapter(
                    this,
                    null,
                    _recyclerProductList.layoutManager as GridLayoutManager,
                    filteredItems as java.util.ArrayList<Product>
                )
                _recyclerProductList.adapter = _productAdapter

            }
            filteredItems.isEmpty() -> {
                Toast.makeText(_view.context, "Nenhum item encontrado", Toast.LENGTH_SHORT).show()
                getMoreItems()
            }
            else -> {
                getMoreItems()
            }
        }
    }


    companion object {
        fun newInstance() = ProductSearch()
    }
}