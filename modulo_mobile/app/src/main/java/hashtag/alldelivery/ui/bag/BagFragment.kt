package hashtag.alldelivery.ui.bag

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.MainActivity
import hashtag.alldelivery.R
import hashtag.alldelivery.core.utils.OnBackPressedListener
import kotlinx.android.synthetic.main.bag_fragment.*
import kotlinx.android.synthetic.main.bag_fragment_toolbar.*

class BagFragment : Fragment(), OnBackPressedListener {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bag_fragment, container, false)
        content_list.layoutManager
        content_list.setHasFixedSize(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_button.setOnClickListener {
            back()
        }
    }

    private fun back(){
        StatusBarUtil.setLightMode(activity)
        activity!!.supportFragmentManager.popBackStack()
        activity!!.supportFragmentManager.beginTransaction()
            .remove(this).commit()

        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onBackPressed() {
        back()
    }
}