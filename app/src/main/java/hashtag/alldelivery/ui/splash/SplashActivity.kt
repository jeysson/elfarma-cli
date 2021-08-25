package hashtag.alldelivery.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import hashtag.alldelivery.AllDeliveryApplication
import hashtag.alldelivery.MainActivity
import hashtag.alldelivery.R
import hashtag.alldelivery.core.async.JsonPostData
import hashtag.alldelivery.core.models.Login
import hashtag.alldelivery.core.models.Message
import hashtag.alldelivery.core.utils.DateDeserializer
import hashtag.alldelivery.core.utils.OnTaskCompleted
import hashtag.alldelivery.ui.home.HomeFragment
import hashtag.alldelivery.ui.order.User
import kotlinx.android.synthetic.main.splash.*
import org.jetbrains.anko.doAsync
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity(), OnTaskCompleted {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash)
        supportActionBar?.hide()
       /* window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
       window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)*/
        contentSplash.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        loadUser()
        Handler().postDelayed({
            val intent =Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    fun loadUser(){

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {

                //  Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                val login = Login()
                login.email = currentUser.email
                login.tokenFCM = task.result
                //
                JsonPostData(
                    AllDeliveryApplication.APIAddress.LOGIN.toString(), login,
                    this, ""
                ).execute()
            } else {
                val login = Login()
                login.tokenFCM = task.result

                JsonPostData(
                    AllDeliveryApplication.APIAddress.LOGINTOKEN.toString(), login,
                    this, ""
                ).execute()
            }
        })

    }

    override fun onTaskCompleted(data: JSONObject?) {

        if (data != null) {
            try {
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
                }
            } catch (e: JSONException) {

                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {

                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}