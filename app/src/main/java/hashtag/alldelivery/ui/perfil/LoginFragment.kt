package hashtag.alldelivery.ui.perfil

import android.app.ActivityOptions
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.AllDeliveryApplication.Companion.USER
import hashtag.alldelivery.MainActivity
import hashtag.alldelivery.R
import hashtag.alldelivery.core.async.JsonPostData
import hashtag.alldelivery.core.models.Login
import hashtag.alldelivery.core.models.Message
import hashtag.alldelivery.core.utils.DateDeserializer
import hashtag.alldelivery.core.utils.OnTaskCompleted
import hashtag.alldelivery.ui.order.User
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment(), View.OnClickListener, OnTaskCompleted {

    private lateinit var account: GoogleSignInAccount
    private val RC_SIGN_IN: Int = 9001

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    var waitDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).showBottomNavigation()
        //
        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        //
        googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        //
        loadView()
        // [END initialize_auth]
        //topbar_title.text = "Login"
        //
        /*btLoggin.setOnClickListener(this)
        btCadastrar.setOnClickListener(this)
        btEsquiciSenha.setOnClickListener(this)*/
        /*back_button.setOnClickListener {
            back()
        }*/
        //
        //
        setWaitDialog("Validando as credenciais.")
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
       // autenticar(currentUser!!)
    }

    override fun onClick(view: View?) {
        val fragmentManager = activity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        when (view!!.id) {
            R.id.btgoogle -> SingInGoogle()
            R.id.other_button-> {
                auth.signOut()
                loadView()
            }
          //  R.id.btLoggin -> autenticar()
           /* R.id.btCadastrar -> {
                val cadastrarFragment: Fragment = CadastrarFragment()
                fragmentTransaction.remove(this@LoginFragment)
                fragmentTransaction.add(R.id.frmLogin, cadastrarFragment)
                fragmentTransaction.commit()
            }
            R.id.btEsquiciSenha -> {
                val esqueciSenhaFragment: Fragment = EsqueciSenhaFragment()
                fragmentTransaction.remove(this@LoginFragment)
                fragmentTransaction.add(R.id.frmLogin, esqueciSenhaFragment)
                fragmentTransaction.commit()
            }*/
        }
    }

    private  fun SingInGoogle(){
        loading.visibility = View.VISIBLE
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException::class.java)!!
                Log.d("ELFARMA", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("ELFARMA", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("ELFARMA", "signInWithCredential:success")

                    loading.visibility = View.VISIBLE
                    loadView()

                    autenticar()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ELFARMA", "signInWithCredential:failure", task.exception)
                 //   updateUI(null)
                }
            }
    }

    private fun autenticar() {
        try {
           // waitDialog!!.show()
            val login = Login()
            login.email = account.email
            login.nome = account.givenName
            login.sobrenome = account.familyName
            login.tokenFCM = AllDeliveryApplication.USER?.tokenFCM

            JsonPostData(
                AllDeliveryApplication.APIAddress.LOGIN.toString(), login,
                this, ""
            ).execute()
        } catch (ex: Exception) {
          //  waitDialog!!.hide()
            Toast.makeText(context, ex.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onTaskCompleted(data: JSONObject?) {
        if (data != null) {
            try {
                //waitDialog!!.hide()
                val mm: Message = Gson().fromJson<Any>(
                    data.toString(),
                    object : TypeToken<Message?>() {}.type
                ) as Message
                if (mm.code!! > 300) {
                    throw Exception(mm.message)
                } else {
                    val gson = GsonBuilder().registerTypeAdapter(
                        Date::class.java,
                        DateDeserializer()
                    ).create()
                    val user: User = gson.fromJson(
                        data.getJSONObject("dados").toString(),
                        object : TypeToken<User?>() {}.type
                    )
                    //
                    AllDeliveryApplication.USER = user

                   /* if (user.validated!!) {
                        if (user.senhaProv!!) {
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
                                    AlterarSenhaFragment::class.java,
                                    ActivityOptions.makeSceneTransitionAnimation(
                                        activity
                                    ).toBundle()
                                )
                                addToBackStack(null)
                            }
                        } else {*/
                            val imm: InputMethodManager? =
                                getSystemService<InputMethodManager>(activity!!, InputMethodManager::class.java)
                            imm?.hideSoftInputFromWindow(view!!.windowToken, 0)
                            //waitDialog!!.dismiss()
                            //(activity as MainActivity).select(R.id.navigation_home)
                    back()
                        /*}
                    } else {
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
                                VerificacaoFragment::class.java,
                                ActivityOptions.makeSceneTransitionAnimation(
                                    activity
                                ).toBundle()
                            )
                            addToBackStack(null)
                        }
                        waitDialog!!.dismiss()
                    }*/
                }
            } catch (e: JSONException) {
                waitDialog!!.hide()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                waitDialog!!.hide()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        } else {
            waitDialog!!.hide()
        }
    }

    private fun setWaitDialog(text: String) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        val viewGroup = view!!.findViewById<ViewGroup>(android.R.id.content)

        //then we will inflate the custom alert dialog xml that we created
        val dialogView: View =
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

    private fun back(){
        (activity as MainActivity).select(R.id.navigation_home)
    }

    fun loadView(){
        if(auth.currentUser != null){
            title.visibility = View.INVISIBLE
            userName.visibility = View.VISIBLE
            userName.text = USER?.nome +" "+ USER?.sobrenome
            btgoogle.visibility = View.INVISIBLE
            skip_button.visibility = View.INVISIBLE
            other_button.visibility = View.VISIBLE
            other_button.setOnClickListener(this)
        }else {
            title.visibility = View.VISIBLE
            userName.visibility = View.INVISIBLE
            btgoogle.visibility = View.VISIBLE
            skip_button.visibility = View.VISIBLE
            other_button.visibility = View.INVISIBLE
            btgoogle.setOnClickListener(this)
        }
    }
}