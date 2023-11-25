package com.nosh.appchi.tools

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nosh.appchi.R
import java.util.*

class Light : AppCompatActivity() {
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var toggleButton: ToggleButton
    private var flashOn = false
    private lateinit var timer : Timer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.t_a_light)
        accessLight()

        MobileAds.initialize(this) {}
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val intervalEditText: EditText = findViewById(R.id.flashivertal)
        toggleButton = findViewById(R.id.onOffFlashlight)
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (intervalEditText.text.toString().isNotEmpty()){
                        checkInterval()
                    }
                    else{
                        intervalEditText.error = getString(R.string.field_empty)
                    }
                } else {
                    if (this::timer.isInitialized){
                    timer.cancel()
                    timer.purge()
                    }
                    cameraManager.setTorchMode(cameraId, false)
                }
            }
        }

    private fun checkInterval(){
        val intervalEditText: EditText = findViewById(R.id.flashivertal)
        val interval = intervalEditText.text.toString().toInt()
        if (interval in 15..20){
                val alert = AlertDialog.Builder(this)
                    .create()
                alert.setTitle(R.string.warning)
                alert.setMessage(getString(R.string.flash_warn))
                alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes)) { _, _ ->
                    blinkingLight()
                }
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no)) { _, _ -> }
                alert.show()
            }
            else if (interval < 15){
                Toast.makeText(this, R.string.flash_too_low, Toast.LENGTH_SHORT).show()
            }
            else{
                blinkingLight()
            }
    }

    private fun blinkingLight(){
        val intervalEditText: EditText = findViewById(R.id.flashivertal)
        val interval = intervalEditText.text.toString()
        if (interval.toInt() < 5){return}
        if (intervalEditText.text.isNotEmpty()) {
            timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    flashOn = !flashOn
                    switchFlashLight(flashOn)
                }
            }, 0, interval.toLong())
        } else {
            intervalEditText.error = getString(R.string.field_empty)
        }
    }

    private fun showNoFlashError() {
        val alert = AlertDialog.Builder(this)
            .create()
        alert.setTitle("Oops!")
        alert.setMessage("Flash not available in this device...")
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { _, _ -> finish() }
        alert.show()
    }

    private fun switchFlashLight(status: Boolean) {
        try {
            cameraManager.setTorchMode(cameraId, status)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun accessLight(){
        val isFlashAvailable = applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)

        if (!isFlashAvailable) {
            showNoFlashError()
        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            cameraId =  cameraManager.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.explain, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.explain -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.flash_info)
                builder.setPositiveButton("OK") { _, _ ->
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        if (this::timer.isInitialized){
            timer.cancel()
            timer.purge()
        }
        cameraManager.setTorchMode(cameraId, false)
    }
}
