package hashtag.elfarma.ui.store

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.jaeger.library.StatusBarUtil
import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.AllDeliveryApplication.Companion.STORE
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.component.ButtonMinusPlus
import hashtag.elfarma.core.models.*
import hashtag.elfarma.core.utils.OnBackPressedListener
import hashtag.elfarma.core.utils.OnChangedValueListener
import hashtag.elfarma.ui.products.GroupProductsAdapter
import hashtag.elfarma.ui.products.ProductSearch
import hashtag.elfarma.ui.products.ProductViewModel
import kotlinx.android.synthetic.main.store_card_toolbar.*
import kotlinx.android.synthetic.main.store_fragment.*
import kotlinx.android.synthetic.main.store_menu_header.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class StoreFragment : Fragment(), OnBackPressedListener,
    OnChangedValueListener {

    private var isUserScrolling: Boolean = false
    val viewModelProduct: ProductViewModel by sharedViewModel()
    val viewModelStore: StoresViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.store_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtil.setDarkMode(activity)

        AllDeliveryApplication.fragmentoAnterior = AllDeliveryApplication.fragmento
        AllDeliveryApplication.fragmento = this.javaClass.simpleName

        (activity as MainActivity).hideBottomNavigation()
        (activity as MainActivity).showBag()

        list.layoutManager = LinearLayoutManager(context)
        list.setHasFixedSize(true)

        store_name.text = STORE?.nomeFantasia
        store_info.text = "${returnHour(STORE?.hAbre)} - ${returnHour(STORE?.hFecha)}  ${getString(R.string.dot)} ver mais"

        store_info.setOnClickListener {
            val manager: FragmentManager = activity!!.supportFragmentManager
            manager.beginTransaction()
            manager.commit {
                setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                replace(
                    R.id.nav_host_fragment,
                    StoreInfoFragment::class.java,
                    ActivityOptions.makeSceneTransitionAnimation(
                        activity
                    ).toBundle()
                )
                addToBackStack(StoreInfoFragment::class.java.simpleName)
            }
        }

        back_button.setOnClickListener {
            back()
        }

        page_header.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {

            var isShow = false;
            var scrollRange = -1;

            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout!!.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    store_title.text = STORE?.nomeFantasia
                    store_description.text =
                        "${returnHour(STORE?.hAbre)} - ${returnHour(STORE?.hFecha)}" + getString(R.string.dot) + " ver mais"
                    tabs.visibility = View.VISIBLE
                } else if (isShow) {
                    isShow = false;
                    store_title.text = null
                    store_description.text = null
                    tabs.visibility = View.GONE
                }

                store_title.alpha =
                    (scrollRange + (verticalOffset * -1)) / scrollRange.toFloat() - 1
                store_description.alpha = store_title.alpha
            }

        })

        search_background.setOnClickListener {
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
                replace(R.id.nav_host_fragment, ProductSearch::class.java, null)
            }
        }
        
        setupObservers()

        syncTabWithRecyclerView()

        initAdapter()
    }

    override fun onStart() {
        super.onStart()

        thread(true) {
            viewModelStore.getStoreBanner(STORE?.id)
        }

        carregarGruposProdutos()
    }

    fun initAdapter(){
        viewModelProduct.adapterGroup = GroupProductsAdapter(
            this)
        viewModelProduct.adapterGroup?.groups = ArrayList<Group>()
        list.adapter = viewModelProduct.adapterGroup
    }

    private fun showBanner() {

        groceries_image_header.setImageResource(R.color.colorPrimary)
        if (STORE?.disponivel == false || STORE?.disponivel == null) {
            txt_closed_header_menu.visibility = VISIBLE
        }else {
            txt_closed_header_menu.visibility = GONE
        }

        if (STORE?.banner != null) {
            image_view_store_closed_overlay_hearder_menu.visibility = VISIBLE
            val imageBytes = Base64.decode(STORE?.banner, 0)
            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val drawable = BitmapDrawable(resources, image)
            groceries_image_header.setImageDrawable(drawable)

            if (STORE?.disponivel == false || STORE?.disponivel == null) {
                image_view_store_closed_overlay_hearder_menu.setBackgroundResource(R.color.black_overlay_70)
                txt_closed_header_menu.visibility = VISIBLE
            }else {
                image_view_store_closed_overlay_hearder_menu.setBackgroundResource(R.color.black_overlay)
                txt_closed_header_menu.visibility = GONE
            }
        }
    }

    fun returnHour (number: Int?): String {
        var newString = number.toString()
        if (newString.length == 3) {
            newString = "0$newString"
        }

        newString = "${newString.substring(0, 2)}:${newString.substring(2, 4)}"

        return newString
    }

    fun ObterImagens(position: Int) =
        thread(true) {
            /*
            * Busca as imagens referentes aos produtos
            */
            val grp = viewModelProduct.adapterGroup?.groups?.get(position)
            var fts = viewModelProduct.getImagesGroup(grp?.id)
            grp?.carregouImagens = true
            //
            for (prod in grp?.products!!) {
                prod?.productImages = ArrayList(fts.filter { p -> p.produtoId == prod.id })
                prod?.carregouImagens = true
            }

            runOnUiThread {
                viewModelProduct.adapterGroup?.adapters?.get(grp?.id)?.notifyDataSetChanged()
            }
        }


    private fun setupObservers() {

        /*viewModelStore.eventLoadImage.observe(viewLifecycleOwner){
            thread(true) {
                /*
                * Busca as imagens referentes aos produtos
                */
                val position = it
                val grp = viewModelProduct.adapterGroup?.groups?.get(position)
                var fts = viewModelProduct.getImagesGroup(grp?.id)
                grp?.carregouImagens = true
                //
                for (prod in grp?.products!!){
                    prod?.productImages = ArrayList(fts.filter { p-> p.produtoId == prod.id })
                    prod?.carregouImagens = true
                }
               // Log.d("LOADIMG", "Posição: "+position)
                viewModelProduct.adapterGroup?.notifyItemChanged(position)
            }
        }*/

        viewModelStore.eventLoadBanner.observe(viewLifecycleOwner){
            showBanner()
        }

        viewModelProduct.loading.observe(viewLifecycleOwner){
            if(it){
                loading.visibility = VISIBLE
                list.visibility = GONE
            }else{
                loading.visibility = INVISIBLE
                list.visibility = VISIBLE
            }
        }

        viewModelProduct.groups.observe(viewLifecycleOwner){

            tabs.removeAllTabs()

            for ((index, grupo) in it.withIndex()) {
                var tab = tabs.newTab()
                tab.text = grupo.nome as CharSequence
                tabs.addTab(tab, index)
               /* thread(true) {
                    viewModelProduct.getGroupProducts(STORE?.id, grupo.id)
                }*/
            }
        }

        viewModelProduct.eventoErro.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer<BusinessEvent> {
                it?.let {
                    toast(it.message.toString())
                }
            })
    }

    private fun carregarGruposProdutos(){
        thread(true){
            viewModelProduct.getAllGroups(STORE?.id)
        }
    }

    private fun syncTabWithRecyclerView() {

        // Move recylerview to the position selected by user
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (!isUserScrolling) {
                    val position = tab.position
                    (list.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        position,
                        0
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Detect recyclerview position and select tab respectively.
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isUserScrolling = true
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    isUserScrolling = false
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var layoutManager = list.layoutManager as LinearLayoutManager

                if (isUserScrolling) {
                    var itemPosition = 0
                    if (dy > 0) {
                        // scrolling up
                        itemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                    } else {
                        // scrolling down
                        itemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                    }
                    val tab = tabs.getTabAt(itemPosition)
                    tab?.select()
                }
            }
        });
    }

    private fun back(){
        StatusBarUtil.setLightMode(activity)
        activity!!.supportFragmentManager.popBackStackImmediate()
       /* activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()*/

        (activity as MainActivity).showBottomNavigation()
    }

    override fun onBackPressed() {
        back()
    }

    override fun OnChangedValue(obj: ButtonMinusPlus, prod: Product, value: Int){

        (activity as MainActivity).changeValueBag(obj, prod, value)
    }

    override fun onResume() {
        super.onResume()
       // activity!!.supportFragmentManager.beginTransaction().show(this).commit()
    }
}