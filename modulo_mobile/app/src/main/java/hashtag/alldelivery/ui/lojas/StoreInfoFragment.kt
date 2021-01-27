package hashtag.alldelivery.ui.lojas

import android.app.ActivityOptions
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProviders
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.R
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.store_card_toolbar.*
import kotlinx.android.synthetic.main.store_card_toolbar.back_button
import kotlinx.android.synthetic.main.store_menu_header.*

class StoreInfoFragment : Fragment() {
    companion object {
        fun newInstance() = StoreFragment()
    }

    private lateinit var viewModel: StoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.store_info_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StoreViewModel::class.java)
        // TODO: Use the ViewModel
        back_button.setOnClickListener {
            activity?.onBackPressed()
        }

        topbar_title.text = getString(R.string.store_information)
    }
}