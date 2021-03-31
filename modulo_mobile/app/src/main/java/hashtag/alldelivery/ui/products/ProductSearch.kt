package hashtag.alldelivery.ui.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication.Companion.SEARCH_STORE
import hashtag.alldelivery.AllDeliveryApplication.Companion.STORE
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Product
import hashtag.alldelivery.core.utils.OnBackPressedListener
import kotlinx.android.synthetic.main.product_search_fragment.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.concurrent.thread


class ProductSearch : Fragment(), OnBackPressedListener {

    private lateinit var _view: View
    private lateinit var _productAdapter: ProductAdapter
    private lateinit var _productList: ArrayList<Product>
    private val _viewModelProduct: ProductViewModel by sharedViewModel()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page = 1
    var itemsPerPage = 6
    var filter: String? = null

    private val _recyclerProductList by lazy { _view.findViewById<RecyclerView>(R.id.recycler_product_list_search) }
    private val _editSearch by lazy { _view.findViewById<EditText>(R.id.edit_text_search_item) }
    private val _cancelButton by lazy { _view.findViewById<Button>(R.id.btn_cancel_search) }

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

                if (!isLoading && !isLastPage) {
                    //if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    if (recyclerLayout.findLastCompletelyVisibleItemPosition() == (itemsPerPage * page) - 1) {
                        isLoading = true
                        loadingSearch.visibility = View.VISIBLE
                        page += 1
                        if (filter != null)
                            getMoreItems(filter)
                        else
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
                if (text.length > 2) {
                    findItemByName(text!!)
                }
            }
        }

        _cancelButton.setOnClickListener {
            if (_editSearch.text.isNullOrBlank()) {
                back()
            } else {
                _editSearch.setText("")
            }

        }

        initAdapter()
        setObservables()
    }

    override fun onStart() {
        super.onStart()
            getItems()
    }

    fun initAdapter(){
        _viewModelProduct.adapterProduct = ProductAdapter(
            this, SEARCH_STORE, null)
        _viewModelProduct.adapterProduct?.itens = ArrayList<Product>()
        _recyclerProductList.adapter = _viewModelProduct.adapterProduct
    }

    fun setObservables(){
      /*  _viewModelProduct.eventoProductSearch.observe(viewLifecycleOwner){
            _recyclerProductList.visibility = VISIBLE
        }*/
        _viewModelProduct.loading.observe(viewLifecycleOwner){

            isLoading = it

            if(it) {
                loadingSearch.visibility = View.VISIBLE

               /* if(page== 1)
                    _recyclerProductList.visibility = View.GONE*/
            }else{
                loadingSearch.visibility = View.INVISIBLE
              //  _recyclerProductList.visibility = View.VISIBLE
            }
        }
    }

    private fun findItemByName(text: CharSequence) {
        /*
        * Procura na lista completa da loja
        */
        page = 1
        if (text != null && text.toString() != "") {
            filter = text.toString()
            getItems(filter)
        }
        else
            getItems()
    }

    private fun getItems(filter: String? = null) {
        val productSearch = this
        page = 1
        isLastPage = false
        //config adapter
        _viewModelProduct.getPagingProducts(
            STORE!!.id,
            filter,
            page,
            itemsPerPage
        )
    }

    fun getMoreItems(filter: String? = null) {

        _viewModelProduct.getPagingProducts(
            STORE!!.id,
            filter,
            page,
            itemsPerPage
        )
    }

    private fun back(){
        activity!!.supportFragmentManager.popBackStack()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()
    }

    override fun onBackPressed() {
        back()
    }
}