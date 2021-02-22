package hashtag.alldelivery

import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.animation.LinearInterpolator
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jaeger.library.StatusBarUtil
import hashtag.alldelivery.core.utils.OnBackPressedListener
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    lateinit var navView: BottomNavigationView
    final val DURATION: Long = 400

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        StatusBarUtil.setTransparent(this)

        setContentView(R.layout.stores_activity_main)

        navView = findViewById(R.id.nav_view)

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
        var fragmentVisible = false
        this.supportFragmentManager.getFragments().forEach {
            if((it as? NavHostFragment) == null && it.isVisible){
                fragmentVisible = true
                (it as? OnBackPressedListener)?.onBackPressed()
            }
        }
        if(!fragmentVisible){
            super.onBackPressed()
        }
    }

    public fun hideBottomNavigation() {
        // bottom_navigation is BottomNavigationView
        with(navView) {
            if (visibility == View.VISIBLE) {

                animate().apply {
                    duration = DURATION
                    translationY(115f)
                    withEndAction {
                        visibility = View.GONE
                    }
                }
            }
        }
    }

    public fun showBottomNavigation() {
        // bottom_navigation is BottomNavigationView
        with(navView) {
            if (visibility == View.GONE) {
                animate().apply {
                    duration = DURATION
                    translationY(0f)
                    withStartAction {
                        visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}