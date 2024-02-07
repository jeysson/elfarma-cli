package hashtag.elfarma.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import hashtag.elfarma.AllDeliveryApplication
import hashtag.elfarma.AllDeliveryApplication.Companion.USER
import hashtag.elfarma.MainActivity
import hashtag.elfarma.R
import hashtag.elfarma.core.async.JsonPostData
import hashtag.elfarma.core.models.Login
import hashtag.elfarma.core.models.Message
import hashtag.elfarma.core.utils.DateDeserializer
import hashtag.elfarma.core.utils.OnTaskCompleted
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hashtag.elfarma.AllDeliveryApplication.Companion.pedidocheckout
import hashtag.elfarma.core.models.User


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment(), View.OnClickListener, OnTaskCompleted{

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loginManager: LoginManager

    private val RC_SIGN_IN: Int = 9001
    private val FB_SIGN_IN: Int = 64206

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
        (activity as MainActivity).hideBag()
        //
        // Initialize Firebase Auth
        auth = Firebase.auth
        //
        loginFacebook()
        //
        // [START config_signin]
        // Configure Google Sign In

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("144553100651-jsou9nfdgr1j52g5qirjunk6bubap2ok.apps.googleusercontent.com")
            .requestEmail()
            .build()
        //
        googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        //
        loadView()

        if(pedidocheckout){
            topbar_title.text = ""
            toolbar2.visibility = View.VISIBLE
            back_button.setOnClickListener { back() }
        }

        setWaitDialog("Validando as credenciais.")
    }

    override fun onStart() {
        super.onStart()
        //
       /* if( auth.currentUser != null)
            autenticar()*/
    }

    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.btgoogle -> SingInGoogle()
            R.id.btfacebook -> SingInFacebook()
            R.id.other_button -> {
                auth.signOut()
                LoginManager.getInstance().logOut()
                googleSignInClient.signOut()
                var user = User()
                user.anonimo = true
                user.token = USER?.token
                user.tokenFCM = USER?.tokenFCM
                USER = user
                loadView()
            }
        }
    }

    private  fun SingInGoogle(){
        loadinggoogle.visibility = View.VISIBLE
        textgoogle.visibility = View.GONE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private  fun SingInFacebook(){
        loadingface.visibility = View.VISIBLE
        textface.visibility = View.GONE
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (!isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        } else {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("ELFARMA", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("ELFARMA", "Google sign in failed", e)
            }
        }else if(requestCode == FB_SIGN_IN){
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                   // account = auth.currentUser
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("ELFARMA", "signInWithCredential:success")
                    loadView()

                    autenticar()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ELFARMA", "signInWithCredential:failure", task.exception)
                 //   updateUI(null)
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
       // Log.d(TAG, "handleFacebookAccessToken:$token")
       // showProgressBar()

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                  //  Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    //updateUI(user)
                    autenticar()
                } else {
                   // // If sign in fails, display a message to the user.
                   /* Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)*/
                }

                //hideProgressBar()
            }
    }

    private fun autenticar() {
        try {
           // waitDialog!!.show()
            val login = Login()
            login.email = auth.currentUser?.email// account.email
            var nomes = auth.currentUser?.displayName?.split(" ")
            login.nome = nomes?.get(0)

            if(nomes?.size!! > 1)
                login.sobrenome = nomes.get(nomes.size -1 )

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
                    loadView()

                    if(pedidocheckout){
                        if(USER?.cpf == null) {
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
                                replace(R.id.nav_host_fragment, CadastrarFragment::class.java, null)
                            }
                        }
                    }else {
                        back()
                    }
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

    fun loginFacebook(){
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                handleFacebookAccessToken(loginResult?.accessToken!!)
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })
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
        activity!!.supportFragmentManager.popBackStackImmediate()
    }

    fun loadView(){
        if(auth.currentUser != null){
            title.visibility = View.INVISIBLE
            userName.visibility = View.VISIBLE
            userName.text = USER?.nome +" "+ USER?.sobrenome
            btgoogle.visibility = View.GONE
            btgoogle.visibility = View.GONE
            loadingface.visibility = View.GONE
            loadinggoogle.visibility = View.GONE
            textgoogle.visibility = View.VISIBLE
            textface.visibility = View.VISIBLE
            other_button.visibility = View.VISIBLE
            other_button.setOnClickListener(this)
        }else {
            title.visibility = View.VISIBLE
            userName.visibility = View.INVISIBLE
            btgoogle.visibility = View.VISIBLE
            btgoogle.visibility = View.VISIBLE
            //skip_button.visibility = View.VISIBLE
            other_button.visibility = View.INVISIBLE
            loadingface.visibility = View.GONE
            loadinggoogle.visibility = View.GONE
            textgoogle.visibility = View.VISIBLE
            textface.visibility = View.VISIBLE
            btgoogle.setOnClickListener(this)
            btfacebook.setOnClickListener(this)
        }
    }
}