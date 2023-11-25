package com.nosh.appchi.tools

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nosh.appchi.R

class BMI : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.t_a_bmi)
        val bmibtn: Button = findViewById(R.id.bmibtn)
        val heighti: EditText = findViewById(R.id.bmih)
        val weighti: EditText = findViewById(R.id.bmiw)
        val bmit: TextView = findViewById(R.id.bmit)
        val bmip: ProgressBar = findViewById(R.id.bmip)
        bmit.isVisible = false
        bmip.isVisible = false

        MobileAds.initialize(this) {}
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        bmibtn.setOnClickListener{
            if (heighti.text.toString().isNotEmpty()){
                if (weighti.text.toString().isNotEmpty()){
                    val weight: Float = weighti.text.toString().toFloat()
                    val height: Float = heighti.text.toString().toFloat()
                    val bmi = (weight/(height*height))
                    bmit.isVisible = true
                    bmip.isVisible = true
                    bmip.progress = bmi.toInt()
                    val level: String = when{
                        bmi < 16 -> resources.getString(R.string.bmi1)
                        bmi < 18.25 -> resources.getString(R.string.bmi2)
                        bmi < 25 -> resources.getString(R.string.bmi3)
                        bmi < 30 -> resources.getString(R.string.bmi4)
                        else -> resources.getString(R.string.bmi5)
                    }
                    bmit.text = "BMI: $bmi\n$level"
                } else{weighti.error = resources.getString(R.string.field_empty)}
            } else{heighti.error = resources.getString(R.string.field_empty)}
        }
    }
}