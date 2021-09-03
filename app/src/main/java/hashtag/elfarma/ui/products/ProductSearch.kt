package hashtag.elfarma.ui.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaeger.library.StatusBarUtil
import hashtag.elfarma.AllDeliveryApplication.Companion.SEARCH_STORE
import hashtag.elfarma.AllDeliveryApplication.Companion.STORE
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.component.ButtonMinusPlus
import hashtag.elfarma.core.models.Product
import hashtag.elfarma.core.utils.OnBackPressedListener
import hashtag.elfarma.core.utils.OnChangedValueListener
import kotlinx.android.synthetic.main.product_search_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class ProductSearch : Fragment(), OnBackPressedListener, OnChangedValueListener {

    private lateinit var _view: View
    private lateinit var _productAdapter: ProductAdapter
    private lateinit var _productList: ArrayList<Product>
    private val _viewModelProduct: ProductViewModel by sharedViewModel()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var page = 1
    var itemsPerPage = 6
    var filter: String? = null

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
        recycler_product_list_search.layoutManager = GridLayoutManager(context, 2)
        recycler_product_list_search.setHasFixedSize(true)
        recycler_product_list_search.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val recyclerLayout = (recycler_product_list_search.layoutManager as GridLayoutManager)

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

        edit_text_search_item.doOnTextChanged { text, start, count, after ->
            if (text.isNullOrBlank()) {
                getMoreItems()
                btn_cancel_search.text = getString(R.string.cancelar)
            } else {
                btn_cancel_search.text = getString(R.string.limpar)
                if (text.length > 2) {
                    findItemByName(text!!)
                }
            }
        }

        btn_cancel_search.setOnClickListener {
            if (edit_text_search_item.text.isNullOrBlank()) {
                back()
            } else {
                edit_text_search_item.setText("")
            }

        }

        initAdapter()
        setObservables()
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).hideBottomNavigation()
        getItems()
    }

    fun initAdapter(){
        _viewModelProduct.adapterProduct = ProductAdapter(
            this, SEARCH_STORE, null)
        _viewModelProduct.adapterProduct?.itens = ArrayList<Product>()
        recycler_product_list_search.adapter = _viewModelProduct.adapterProduct
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

    override fun OnChangedValue(obj: ButtonMinusPlus, prod: Product, value: Int){

        (activity as MainActivity).changeValueBag(obj, prod, value)
    }

    private fun back(){
        activity!!.supportFragmentManager.popBackStack()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()
    }

    override fun onBackPressed() {
        back()
    }

    override fun onResume() {
        super.onResume()
    }
}