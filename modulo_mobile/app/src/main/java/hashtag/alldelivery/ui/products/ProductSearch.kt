package hashtag.alldelivery.ui.products

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.Product
import kotlinx.android.synthetic.main.product_search_fragment.*
import kotlinx.android.synthetic.main.product_search_fragment.list
import kotlinx.android.synthetic.main.search_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ProductSearch : Fragment() {

    private lateinit var adapt: ProductsListItemAdapter
    private lateinit var produtos: ArrayList<Product>
    val viewModelProduct: ProductViewModel by sharedViewModel()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var pagina = 1
    var itensPorPagina = 6


    private lateinit var viewModel: ProductViewModel


    companion object {
        fun newInstance() = ProductSearch()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        StatusBarUtil.setLightMode(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtil.setLightMode(activity)

        //config rv
        var lm = GridLayoutManager(context, 2)
        list.layoutManager = lm
        list.setHasFixedSize(true)
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                var vmm = (list.layoutManager as GridLayoutManager)
                /*val visibleItemCount = vmm.childCount
                val totalItemCount = vmm.itemCount
                val firstVisibleItemPosition = vmm.findFirstVisibleItemPosition()
                 */

                if (!isLoading && !isLastPage) {
                    //if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        if(vmm.findLastCompletelyVisibleItemPosition() == (itensPorPagina * pagina) - 1){
                            isLoading = true
                            loadingSearch.visibility = View.VISIBLE
                            pagina = pagina +1
                            getMoreItems()
                        }
                }
            }
        })

        cancel_button.setOnClickListener{
            activity?.onBackPressed()
        }

        lifecycleScope.launch(Dispatchers.IO){
            obterProdutos()
        }

        filter.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarProdutos(s!!)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })
    }

    fun getMoreItems(){
        list.post{
            lifecycleScope.launch(Dispatchers.IO){
                produtos = viewModelProduct?.getPagingProducts(AllDeliveryApplication.STORE!!.id, -1, pagina, itensPorPagina)
                withContext(Dispatchers.Main){
                    adapt.addItems(produtos)
                    adapt.notifyDataSetChanged()
                    loadingSearch.visibility = View.INVISIBLE
                }
                isLastPage = produtos.size == 0
                isLoading = false
            }
        }
    }

     private suspend fun obterProdutos(){
        var ps = this
        //config adapter
        produtos = viewModelProduct?.getPagingProducts(AllDeliveryApplication.STORE!!.id, -1, pagina, itensPorPagina)

        withContext(Dispatchers.Main){
            adapt = ProductsListItemAdapter(ps, null, list.layoutManager as GridLayoutManager, produtos)
            list.adapter = adapt
            adapt.notifyDataSetChanged()
            loadingSearch.visibility = View.INVISIBLE
        }
    }

    private fun filtrarProdutos(s:CharSequence){

        var produtosFiltrados = produtos.filter {  p -> p.descricao!!.contains(s)  }
        adapt = ProductsListItemAdapter(this, null, list.layoutManager as GridLayoutManager, produtosFiltrados as java.util.ArrayList<Product>)
        list.adapter = adapt

    }
}