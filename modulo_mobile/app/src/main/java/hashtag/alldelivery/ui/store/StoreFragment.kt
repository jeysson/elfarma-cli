package hashtag.alldelivery.ui.store

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.STORE
import hashtag.alldelivery.R
import hashtag.alldelivery.core.models.BusinessEvent
import hashtag.alldelivery.core.models.Group
import hashtag.alldelivery.ui.products.GroupProductsAdapter
import hashtag.alldelivery.ui.products.ProductSearch
import hashtag.alldelivery.ui.products.ProductViewModel
import kotlinx.android.synthetic.main.store_card_toolbar.*
import kotlinx.android.synthetic.main.store_fragment.*
import kotlinx.android.synthetic.main.store_menu_header.*
import org.jetbrains.anko.support.v4.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel


class StoreFragment : Fragment(){

    private var isUserScrolling: Boolean = false
    val viewModelProduct: ProductViewModel by sharedViewModel()

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
                addToBackStack(null)
            }
        }

        back_button.setOnClickListener {
            activity?.onBackPressed()
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
            manager.commit {
                setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                replace(
                    R.id.nav_host_fragment,
                    ProductSearch::class.java,
                    ActivityOptions.makeSceneTransitionAnimation(
                        activity
                    ).toBundle()
                )
                addToBackStack(null)
            }
        }

        setupObservers()
        carregarProdutos()
        syncTabWithRecyclerView()
    }

    fun returnHour (number: Int?): String {
        var newString = number.toString()
        if (newString.length == 3) {
            newString = "0$newString"
        }

        newString = "${newString.substring(0, 2)}:${newString.substring(2, 4)}"

        return newString
    }

    private fun setupObservers() {
        viewModelProduct.eventoErro.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer<BusinessEvent> {
                it?.let {
                    toast(it.message.toString())
                }
            })
    }

    private fun carregarProdutos()
    {
        val adapter = GroupProductsAdapter(this, ArrayList<Group>())
      //  adapter.setHasStableIds(true)

            list.layoutManager = LinearLayoutManager(context)
            list.adapter = adapter
            list.setHasFixedSize(true)

            viewModelProduct.getAllGroups(AllDeliveryApplication.STORE?.id)
                .observe(viewLifecycleOwner, Observer<List<Group>> {
                    it?.let {
                        adapter.groups = it

                        for ((index, grupo) in it.withIndex()) {
                            var tab = tabs.newTab()
                            tab.text = grupo.nome as CharSequence
                            tabs.addTab(tab, index)
                        }
                        loading.visibility = View.GONE
                        list.visibility = View.VISIBLE
                        adapter.notifyDataSetChanged()
                    }
                })
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

}