package hashtag.alldelivery

import android.os.Bundle
import android.os.StrictMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jaeger.library.StatusBarUtil
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        /*if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath().build())
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath().build())
        }*/
        super.onCreate(savedInstanceState)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        StatusBarUtil.setTransparent(this)

        setContentView(R.layout.activity_main)
//        supportActionBar?.hide()
/*
        more_icon.setImageResource(R.drawable.ic_medicine)
        quantity.setText("1x")
        name.setText("Bezetacil")
        price.setText("R$87.00")
        complements.setText("Remedio usado para gripe e sei lá.")
        observation.setText("indicado para maiores de 18 anos")
*/


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_requests,
                R.id.navigation_perfil
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }

    override fun onBackPressed() {
        super.onBackPressed()
//        StatusBarUtil.setLightMode(this)
    }
}