@file:Suppress("DEPRECATION")

package com.example.task.ui.activity

  import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.task.R
import com.example.task.databinding.ActivityHomeBinding
import com.example.task.network.Response
import com.example.task.ui.BottomNavigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
//    private val authVM: AuthViewModel by viewModels()

    @Inject
    lateinit var btmNav : BottomNavigation
    companion object {
        var bottomNavigation: BottomNavigationView? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigation = findViewById(R.id.hmNavCons)


            btmNav.response.value = Response.success(btmNav.fragmentList.value!![1])
            binding.hmNavCons.menu.findItem(R.id.navOrdersBtn).isChecked = true


        btmNav.response().observe(this) {
            processResponse(it)
        }
        binding.hmNavCons.itemIconTintList = null

        bottomNavigation!!.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navHomeBtn -> {
                    btmNav.response.value =
                        Response.success(btmNav.fragmentList.value!![0])
                }

                R.id.navOrdersBtn -> {
                    btmNav.response.value =
                        Response.success(btmNav.fragmentList.value!![1])
                }

                R.id.navEWallBtn -> {
                    btmNav.response.value =
                        Response.success(btmNav.fragmentList.value!![2])
                }

                else -> {
                    btmNav.response.value =
                        Response.success(btmNav.fragmentList.value!![1])
                }
            }
            true
        }
    }

    private fun processResponse(response: Response) {
        if (response.data is Fragment) {
            val fragTrans = supportFragmentManager.beginTransaction()
            if (response.data.isAdded) {
                if (btmNav.previousFragment.value != response.data) {
                    fragTrans.show(response.data).hide(btmNav.previousFragment.value!!)
                    fragTrans.commitAllowingStateLoss()
                }
            } else {
                if (btmNav.previousFragment.value == null) {
                    fragTrans.add(R.id.hmCons1, response.data, response.data.javaClass.name)

                } else {
                    fragTrans.add(R.id.hmCons1, response.data, response.data.javaClass.name)
                        .hide(btmNav.previousFragment.value!!)
                }
                fragTrans.commitAllowingStateLoss()
            }
            btmNav.previousFragment.value = response.data
        }
    }
}