package hashtag.elfarma.ui.perfil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hashtag.elfarma.AllDeliveryApplication.Companion.USER
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.core.utils.MaskEditUtil
import hashtag.elfarma.core.utils.OnBackPressedListener
import hashtag.elfarma.ui.order.OrderViewModel
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.fragment_cadastrar.*
import kotlinx.android.synthetic.main.search_fragment.*
import org.jetbrains.anko.support.v4.toast
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [CadastrarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CadastrarFragment : Fragment(), OnBackPressedListener {

    private val perfilViewModel: PerfilViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cadastrar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topbar_title.text = "Cados Complementares"
        editNome.setText(USER?.nome)
        editsobrenome.setText(USER?.sobrenome)
        editEmail.setText(USER?.email)
        //
        editCPF.addTextChangedListener(MaskEditUtil.mask(editCPF, MaskEditUtil.FORMAT_CPF))
        editFone.addTextChangedListener(MaskEditUtil.mask(editFone, MaskEditUtil.FORMAT_FONE))
        //
        back_button.setOnClickListener { back() }

        action_button.setOnClickListener {
            atualizar()
        }
    }

    fun atualizar(){
        if(editCPF.text.isNullOrEmpty() || editCPF.text.length != 14){
            toast("Por favor, informe seu CPF!")
            return
        }

        if(editFone.text.isNullOrEmpty()|| editFone.text.length != 14){
            toast("Por favor, informe seu telefone!")
            return
        }

        USER?.cpf = editCPF.text.toString().replace(".", "")
            .replace("-", "").toLong()
        USER?.telefone = editFone.text.toString().replace("(", "")
            .replace(")", "").replace("-", "").toLong()

        perfilViewModel.update(USER!!)

        back()
    }

    override fun onBackPressed() {
        back()
    }

    private fun back(){
        try{
            activity!!.supportFragmentManager.popBackStack()
           /* activity!!.supportFragmentManager.beginTransaction()
                .remove(this).commit()*/
        }catch (e: Exception){
            (activity as MainActivity).select(R.id.navigation_home)
        }
    }
}