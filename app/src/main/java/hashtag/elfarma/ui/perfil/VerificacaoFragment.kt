package hashtag.elfarma.ui.perfil

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.core.async.JsonPostData
import hashtag.elfarma.core.models.Login
import hashtag.elfarma.core.models.Message
import hashtag.elfarma.core.utils.OnTaskCompleted
import kotlinx.android.synthetic.main.fragment_verificacao.*
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [VerificacaoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VerificacaoFragment : Fragment(), View.OnClickListener, OnTaskCompleted {
    var waitDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verificacao, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        btVerificar.setOnClickListener(this)
        //
        setWaitDialog("Validando as credenciais.")
        //
        txtPrimeiro.requestFocus()
        //
        txtPrimeiro.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length == 1) {
                    txtSegundo.requestFocus()
                }
            }
        })
        //
        //
        txtSegundo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length == 1) {
                    txtTerceiro.requestFocus()
                }
            }
        })
        //
        //
        txtTerceiro.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length == 1) {
                    txtQuarto.requestFocus()
                }
            }
        })
        //
        //
        txtQuarto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length == 1) {
                    txtQuinto.requestFocus()
                }
            }
        })
        //
        //
        txtQuinto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length == 1) {
                    txtSexto.requestFocus()
                }
            }
        })
        //
        //
        txtSexto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length == 1) {
                    btVerificar.requestFocus()
                }
            }
        })
    }

    private fun setWaitDialog(text: String) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = view!!.findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView =
            LayoutInflater.from(this.context).inflate(R.layout.dialog_wait, viewGroup, false)
        val txtMess = dialogView.findViewById<View>(R.id.txtDialogWait) as TextView
        txtMess.text = text
        //Now we need an AlertDialog.Builder object
        val builder = AlertDialog.Builder(
            this.context!!
        )

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView)
        //finally creating the alert dialog and displaying it
        waitDialog = builder.create()
    }

    override fun onClick(v: View?) {
        try {
            waitDialog!!.show()
            if (txtPrimeiro.text.toString().isEmpty() ||
                txtSegundo.text.toString().isEmpty() ||
                txtTerceiro.text.toString().isEmpty() ||
                txtQuarto.text.toString().isEmpty() ||
                txtQuinto.text.toString().isEmpty() ||
                txtSexto.text.toString().isEmpty()
            ) {
                throw Exception("Informe todos os valores!")
            }
            val code = txtPrimeiro.text.toString() +
                    txtSegundo.text.toString() +
                    txtTerceiro.text.toString() +
                    txtQuarto.text.toString() +
                    txtQuinto.text.toString() +
                    txtSexto.text.toString()
            val login = Login()
            login.email = AllDeliveryApplication.USER?.email!!
            login.senha = ""
            login?.codeverification = code
            //
            JsonPostData(
                AllDeliveryApplication.APIAddress.VERIFICATION.toString(), login,
                this, ""/*"Bearer " + AllDeliveryApplication.getUser().getAccessToken()*/
            ).execute()
        } catch (ex: Exception) {
            waitDialog!!.hide()
            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onTaskCompleted(data: JSONObject?) {
        if (data != null) {
            try {
                waitDialog!!.hide()
                val mm: Message = Gson().fromJson<Any>(
                    data.toString(),
                    object : TypeToken<Message?>() {}.type
                ) as Message
                if (mm.code!! > 300) {
                    throw java.lang.Exception(mm.message)
                } else {

                    waitDialog!!.dismiss()

                    (activity as MainActivity).select(R.id.navigation_home)
                }
            } catch (e: JSONException) {
                waitDialog!!.hide()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            } catch (e: java.lang.Exception) {
                waitDialog!!.hide()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        } else {
            waitDialog!!.hide()
        }
    }
}