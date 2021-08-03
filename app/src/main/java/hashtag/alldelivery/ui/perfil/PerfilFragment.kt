package hashtag.alldelivery.ui.perfil

import android.app.ActivityOptions
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProviders
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.USER
import hashtag.alldelivery.R
import hashtag.alldelivery.ui.store.StoreInfoFragment
import kotlinx.android.synthetic.main.perfil_fragment.*
import org.jetbrains.anko.sdk27.coroutines.onClick


class PerfilFragment : Fragment(), View.OnClickListener {

    private lateinit var perfilViewModel: PerfilViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        perfilViewModel =
            ViewModelProviders.of(this).get(PerfilViewModel::class.java)
        val root = inflater.inflate(R.layout.perfil_fragment, container, false)
      //  val textView: TextView = root.findViewById(R.id.text_dashboard)
      /*  perfilViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
       /* if(USER != null){
            user_name.text = USER?.nome +" "+USER?.sobrenome
            email.text = USER?.email
            barbuttons.visibility = View.INVISIBLE
        }
        //
        btlogar.setOnClickListener(this)
        btCadastrar.setOnClickListener(this)*/
    }

    override fun onClick(view: View?) {
        val manager: FragmentManager = activity!!.supportFragmentManager
       /* when (view!!.id) {
            R.id.btlogar -> {
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
                        LoginFragment::class.java,
                        ActivityOptions.makeSceneTransitionAnimation(
                            activity
                        ).toBundle()
                    )
                    addToBackStack(null)
                }
            }
             R.id.btCadastrar -> {
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
                         CadastrarFragment::class.java,
                         ActivityOptions.makeSceneTransitionAnimation(
                             activity
                         ).toBundle()
                     )
                     addToBackStack(null)
                 }
             }
        }*/
    }

}