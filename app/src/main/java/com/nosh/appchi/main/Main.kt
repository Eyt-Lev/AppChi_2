package com.nosh.appchi.main

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.nosh.appchi.`fun`.Hallel
import com.nosh.appchi.R
import com.nosh.appchi.`fun`.Fart
import com.nosh.appchi.`fun`.jokes.Jokes
import com.nosh.appchi.productivity.langs.Lang
import com.nosh.appchi.productivity.parabulaThings.ParabulaThings
import com.nosh.appchi.productivity.tasks.Todo
import com.nosh.appchi.tools.*
import com.nosh.appchi.tools.Random
import java.util.*


class Main : AppCompatActivity() {
    private var mMediaPlayer: MediaPlayer? = null
    private val updateCode = 12123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUpdates()
        lang()
        sneeze()
        installSplashScreen()
        setLayout()
        FirebaseApp.initializeApp(this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val navView : NavigationView = findViewById(R.id.navView)
        val drawerLayout : DrawerLayout = findViewById(R.id.my_drawer_layout)
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout,
            R.string.save,
            R.string.cancel
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.settings -> {
                    startActivity(Intent(this, Settings::class.java))
                }
                R.id.updates -> {
                    val appUpdateManager = AppUpdateManagerFactory.create(this)
                    // Returns an intent object that you use to check for an update.
                    val appUpdateInfoTask = appUpdateManager.appUpdateInfo
                    // Checks that the platform will allow the specified type of update.
                    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                        if (
                            appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE
                        ) {
                            Toast.makeText(this, getString(R.string.no_updates), Toast.LENGTH_LONG).show()
                        }
                        else{
                            startUpdate(appUpdateManager, appUpdateInfo)
                        }
                    }
                }
                R.id.tasks -> {
                    startActivity(Intent(this, Todo::class.java))
                }
                R.id.lang -> {
                    startActivity(Intent(this, Lang::class.java))
                }
                R.id.random -> {
                    startActivity(Intent(this, Random::class.java))
                }
                R.id.metronome -> {
                    startActivity(Intent(this, Metronome::class.java))
                }
                R.id.bmi -> {
                    startActivity(Intent(this, BMI::class.java))
                }
                R.id.light -> {
                    startActivity(Intent(this, Light::class.java))
                }
                R.id.dw -> {
                    startActivity(Intent(this, DWhatsApp::class.java))
                }
                R.id.record -> {
                    startActivity(Intent(this, Record::class.java))
                }
                R.id.jokes -> {
                    startActivity(Intent(this, Jokes::class.java))
                }
                R.id.hallel -> {
                    startActivity(Intent(this, Hallel::class.java))
                }
            }
            true
        }
    }

    private fun checkUpdates(){
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            ) {
                // Request the update.
                startUpdate(appUpdateManager, appUpdateInfo)
            }
        }
    }

    private fun startUpdate(appUpdateManager: AppUpdateManager, appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            // Pass the intent that is returned by 'getAppUpdateInfo()'.
            appUpdateInfo,
            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
            IMMEDIATE,
            // The current activity making the update request.
            this,
            // Include a request code to later monitor this update request.
            updateCode)
    }

    override fun onResume() {
        super.onResume()
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (
                    appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    ||
                    appUpdateInfo.updateAvailability()
                    == UpdateAvailability.UPDATE_AVAILABLE
                ) {
                    // If an in-app update is already running, resume the update.
                    startUpdate(appUpdateManager, appUpdateInfo)
                }
            }
    }

    private fun setLayout() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val divided = sharedPreference.getBoolean("msl", true)
        if (divided) {
            setContentView(R.layout.m_a_main)
            val tabLayout = findViewById<TabLayout>(R.id.tabs)
            val viewPager2 = findViewById<ViewPager2>(R.id.view_pager)
            val myViewPageAdapter = MyViewPagerAdapter(this)
            viewPager2.adapter = myViewPageAdapter
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) { viewPager2.currentItem = tab.position }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.getTabAt(position)?.select()
                }})
        }
        else{
            setContentView(R.layout.m_a_main_united)
            findViewById<Button>(R.id.button3).setOnClickListener{
                startActivity(Intent(this, Todo::class.java))
            }
            findViewById<Button>(R.id.button310).setOnClickListener{
                startActivity(Intent(this, Lang::class.java))
            }
            findViewById<Button>(R.id.button233).setOnClickListener{
                startActivity(Intent(this, Metronome::class.java))
            }
            findViewById<Button>(R.id.button6).setOnClickListener{
                startActivity(Intent(this, Random::class.java))
            }
            findViewById<Button>(R.id.button343).setOnClickListener{
                startActivity(Intent(this, Jokes::class.java))
            }
            findViewById<Button>(R.id.button23).setOnClickListener{
                startActivity(Intent(this, Hallel::class.java))
            }
            findViewById<Button>(R.id.button245).setOnClickListener{
                startActivity(Intent(this, Record::class.java))
            }
            findViewById<Button>(R.id.button642).setOnClickListener{
                startActivity(Intent(this, BMI::class.java))
            }
            findViewById<Button>(R.id.button235).setOnClickListener{
                startActivity(Intent(this, Light::class.java))
            }
            findViewById<Button>(R.id.button221).setOnClickListener{
                startActivity(Intent(this, DWhatsApp::class.java))
            }
            findViewById<Button>(R.id.button454).setOnClickListener{
                startActivity(Intent(this, ParabulaThings::class.java))
            }
            val fart = findViewById<Button>(R.id.button347)
            fart.isVisible = sharedPreference.getBoolean("fart", false)
            fart.setOnClickListener {
                val intent = Intent(this, Fart::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawerLayout :DrawerLayout = findViewById(R.id.my_drawer_layout)
        val actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.save, R.string.cancel)
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun lang() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        if (sharedPreference.contains("lang")) {
            val locale = Locale(sharedPreference.getString("lang", "en").toString())
            Locale.setDefault(locale)
            val resources = this.resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }

    private fun sneeze(){
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        if (sharedPreference.getBoolean("sneeze", false))
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer.create(this, R.raw.sneeze)
                mMediaPlayer!!.start()
            } else mMediaPlayer!!.start()
    }
}
