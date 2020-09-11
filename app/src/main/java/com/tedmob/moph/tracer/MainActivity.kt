package com.tedmob.moph.tracer

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.tedmob.moph.tracer.fragment.ForUseByOTCFragment
import com.tedmob.moph.tracer.fragment.HelpFragment
import com.tedmob.moph.tracer.fragment.HomeFragment
import com.tedmob.moph.tracer.logging.CentralLog
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main_new.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var tedmobApiDisposable: Disposable? = null
    private var disposeObj: Disposable? = null

    // navigation
    private var mNavigationLevel = 0
    var LAYOUT_MAIN_ID = 0
    private var selected = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)

        Utils.startBluetoothMonitoringService(this)

        LAYOUT_MAIN_ID = R.id.content

        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        if (selected != R.id.navigation_home) {
                            openFragment(
                                LAYOUT_MAIN_ID, HomeFragment(),
                                HomeFragment::class.java.name, 0
                            )
                        }
                        selected = R.id.navigation_home
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.navigation_upload -> {
                        if (selected != R.id.navigation_upload) {
                            openFragment(
                                LAYOUT_MAIN_ID, ForUseByOTCFragment(),
                                ForUseByOTCFragment::class.java.name, 0
                            )
                        }

                        selected = R.id.navigation_upload
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.navigation_help -> {
                        if (selected != R.id.navigation_help) {
                            openFragment(
                                LAYOUT_MAIN_ID,
                                HelpFragment(),
                                HelpFragment::class.java.name,
                                0
                            )
                        }

                        selected = R.id.navigation_help
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

        nav_view.itemIconTintList = null
        nav_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        goToHome()

        getFCMToken()
        fetchFirebaseUser()
    }

    private fun getFCMToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    CentralLog.w(TAG, "failed to get fcm token ${task.exception}")
                    return@addOnCompleteListener
                } else {
                    // Get new Instance ID token
                    val token = task.result?.token
                    // Log and toast
                    CentralLog.d(TAG, "FCM token: $token")
                }
            }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun goToHome() {
        nav_view.selectedItemId = R.id.navigation_home
    }

    fun openFragment(
        containerViewId: Int,
        fragment: Fragment,
        tag: String,
        title: Int
    ) {
        try { // pop all fragments
            supportFragmentManager.popBackStackImmediate(
                LAYOUT_MAIN_ID,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            mNavigationLevel = 0
            val transaction =
                supportFragmentManager.beginTransaction()
            transaction.replace(containerViewId, fragment, tag)
            transaction.commit()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    private fun fetchFirebaseUser() {
        val user = FirebaseAuth.getInstance().currentUser
        //user exists
        //...
    }


    override fun onDestroy() {
        super.onDestroy()
        tedmobApiDisposable?.dispose()
        disposeObj?.dispose()
    }
}
