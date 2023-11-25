package com.nosh.appchi.`fun`

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nosh.appchi.R

class Hallel : AppCompatActivity() {

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.f_a_hallel)

        MobileAds.initialize(this) {}
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val checkbox00 = findViewById<CheckBox>(R.id.checkbox_00)
        val checkbox01 = findViewById<CheckBox>(R.id.checkbox_01)
        val checkbox02 = findViewById<CheckBox>(R.id.checkbox_02)
        val checkbox03 = findViewById<CheckBox>(R.id.checkbox_03)
        val checkbox04 = findViewById<CheckBox>(R.id.checkbox_04)
        val checkbox05 = findViewById<CheckBox>(R.id.checkbox_05)
        val checkbox06 = findViewById<CheckBox>(R.id.checkbox_06)
        val checkbox07 = findViewById<CheckBox>(R.id.checkbox_07)
        val checkbox08 = findViewById<CheckBox>(R.id.checkbox_08)
        val checkbox09 = findViewById<CheckBox>(R.id.checkbox_09)
        val checkbox10 = findViewById<CheckBox>(R.id.checkbox_10)
        val checkbox11 = findViewById<CheckBox>(R.id.checkbox_11)
        val checkbox12 = findViewById<CheckBox>(R.id.checkbox_12)
        val checkbox13 = findViewById<CheckBox>(R.id.checkbox_13)
        val checkbox14 = findViewById<CheckBox>(R.id.checkbox_14)
        val checkbox15 = findViewById<CheckBox>(R.id.checkbox_15)
        val checkbox16 = findViewById<CheckBox>(R.id.checkbox_16)
        val checkbox17 = findViewById<CheckBox>(R.id.checkbox_17)
        val checkbox18 = findViewById<CheckBox>(R.id.checkbox_18)
        val checkbox19 = findViewById<CheckBox>(R.id.checkbox_19)
        val checkbox20 = findViewById<CheckBox>(R.id.checkbox_20)
        val checkbox21 = findViewById<CheckBox>(R.id.checkbox_21)
        val checkbox22 = findViewById<CheckBox>(R.id.checkbox_22)
        val checkbox23 = findViewById<CheckBox>(R.id.checkbox_23)
        val checkbox24 = findViewById<CheckBox>(R.id.checkbox_24)
        val checkbox25 = findViewById<CheckBox>(R.id.checkbox_25)
        val checkbox26 = findViewById<CheckBox>(R.id.checkbox_26)
        val checkbox27 = findViewById<CheckBox>(R.id.checkbox_27)
        val checkbox28 = findViewById<CheckBox>(R.id.checkbox_28)
        val checkbox29 = findViewById<CheckBox>(R.id.checkbox_29)
        val checkbox30 = findViewById<CheckBox>(R.id.checkbox_30)
        val checkbox31 = findViewById<CheckBox>(R.id.checkbox_31)
        val checkbox32 = findViewById<CheckBox>(R.id.checkbox_32)
        val checkbox33 = findViewById<CheckBox>(R.id.checkbox_33)
        val checkbox34 = findViewById<CheckBox>(R.id.checkbox_34)
        val checkbox35 = findViewById<CheckBox>(R.id.checkbox_35)

        val checkboxes = listOf(checkbox00, checkbox01, checkbox02, checkbox03, checkbox04, checkbox05, checkbox06, checkbox07,
            checkbox08, checkbox09, checkbox10, checkbox11, checkbox12, checkbox13, checkbox14,
            checkbox15, checkbox16, checkbox17, checkbox18, checkbox19, checkbox20,
            checkbox21, checkbox22, checkbox23, checkbox24, checkbox25,
            checkbox26, checkbox27, checkbox28, checkbox29,
            checkbox30, checkbox31, checkbox32,
            checkbox33, checkbox34,
            checkbox35)

        val array1 = arrayOf(checkbox00, checkbox01, checkbox02, checkbox03, checkbox04, checkbox05, checkbox06, checkbox07)
        val array2 = arrayOf(checkbox08, checkbox09, checkbox10, checkbox11, checkbox12, checkbox13, checkbox14)
        val array3 = arrayOf(checkbox15, checkbox16, checkbox17, checkbox18, checkbox19, checkbox20)
        val array4 = arrayOf(checkbox21, checkbox22, checkbox23, checkbox24, checkbox25)
        val array5 = arrayOf(checkbox26, checkbox27, checkbox28, checkbox29)
        val array6 = arrayOf(checkbox30, checkbox31, checkbox32)
        val array7 = arrayOf(checkbox33, checkbox34)
        val array8 = arrayOf(checkbox35)


        checkbox07.isChecked = true
        checkbox07.isEnabled = false
        checkbox00.isChecked = true
        checkbox00.isEnabled = false
        checkbox35.isChecked = true
        checkbox35.isEnabled = false
        checkbox23.isChecked = true
        checkbox23.isEnabled = false
        val scoreText = findViewById<TextView>(R.id.score_text_view)
        val scoreText2 = findViewById<TextView>(R.id.score_text_view2)
        var points = 0
        var points2 = 0
        var playerOne = true
        val p1 : LinearLayout = findViewById(R.id.p1)
        val p2 : LinearLayout = findViewById(R.id.p2)
        p1.setBackgroundResource(R.color.blue)
        val d1 = arrayOf(checkbox07, checkbox14, checkbox20,checkbox25, checkbox29, checkbox32, checkbox34, checkbox35)
        val d2 = arrayOf(checkbox06, checkbox13, checkbox19, checkbox24, checkbox28, checkbox31, checkbox33)
        val d3 = arrayOf(checkbox05, checkbox12, checkbox18, checkbox23, checkbox27, checkbox30)
        val d4 = arrayOf(checkbox04, checkbox11, checkbox17, checkbox22, checkbox26)
        val d5 = arrayOf(checkbox03, checkbox10, checkbox16, checkbox21)
        val d6 = arrayOf(checkbox02, checkbox09, checkbox15)
        val d7 = arrayOf(checkbox01, checkbox08)
        val d8 = arrayOf(checkbox00, checkbox08, checkbox15, checkbox21, checkbox26, checkbox30, checkbox33, checkbox35)
        val d9 = arrayOf(checkbox01, checkbox09, checkbox16, checkbox22, checkbox27, checkbox31, checkbox34)
        val d10 = arrayOf(checkbox02, checkbox10, checkbox17, checkbox23, checkbox28, checkbox32)
        val d11 = arrayOf(checkbox03, checkbox11, checkbox18, checkbox24, checkbox29)
        val d12 = arrayOf(checkbox04, checkbox12, checkbox19, checkbox25)
        val d13 = arrayOf(checkbox05, checkbox13, checkbox20)
        val d14 = arrayOf(checkbox06, checkbox14)
        fun checkForScore(array: Array<CheckBox>){
            if (array.all { it.isChecked }) {
                // Return the array length
                for (cb in array)
                    cb.background = AppCompatResources.getDrawable(this, if (playerOne){(R.drawable.checked_round_checkbox_blue)}else{
                        R.drawable.checked_round_checkbox_red
                    })
                if (playerOne){
                    points += array.size
                    scoreText.text = points.toString()
                }
                else{
                    points2 += array.size
                    scoreText2.text = points2.toString()
                }
            }
        }
        fun checkRow(cb: CheckBox){
            val array = when (cb) {
                checkbox00, checkbox01, checkbox02, checkbox03, checkbox04, checkbox05, checkbox06, checkbox07 -> array1
                checkbox08, checkbox09, checkbox10, checkbox11, checkbox12, checkbox13, checkbox14 -> array2
                checkbox15, checkbox16, checkbox17, checkbox18, checkbox19, checkbox20 -> array3
                checkbox21, checkbox22, checkbox23, checkbox24, checkbox25 -> array4
                checkbox26, checkbox27, checkbox28, checkbox29 -> array5
                checkbox30, checkbox31, checkbox32 -> array6
                checkbox33, checkbox34 -> array7
                checkbox35 -> array8
                else -> throw IllegalStateException("Invalid checkbox")
            }
            checkForScore(array)
        }
        fun checkDiaginal(cb: CheckBox){
            val dia = when (cb) {
                checkbox07, checkbox14, checkbox20,checkbox25, checkbox29, checkbox32, checkbox34, checkbox35 -> d1
                checkbox06, checkbox13, checkbox19, checkbox24, checkbox28, checkbox31, checkbox33 -> d2
                checkbox05, checkbox12, checkbox18, checkbox23, checkbox27, checkbox30 -> d3
                checkbox04, checkbox11, checkbox17, checkbox22, checkbox26 -> d4
                checkbox03, checkbox10, checkbox16, checkbox21 -> d5
                checkbox02, checkbox09, checkbox15 -> d6
                checkbox01, checkbox08 -> d7
                else -> throw IllegalStateException("Invalid checkbox")
            }
            checkForScore(dia)
        }
        fun checkDiaginal2(cb: CheckBox) {
            val dia = when (cb) {
                checkbox00, checkbox08, checkbox15, checkbox21, checkbox26, checkbox30, checkbox33, checkbox35 -> d8
                checkbox01, checkbox09, checkbox16, checkbox22, checkbox27, checkbox31, checkbox34 -> d9
                checkbox02, checkbox10, checkbox17, checkbox23, checkbox28, checkbox32 -> d10
                checkbox03, checkbox11, checkbox18, checkbox24, checkbox29 -> d11
                checkbox04, checkbox12, checkbox19, checkbox25 -> d12
                checkbox05, checkbox13, checkbox20 -> d13
                checkbox06, checkbox14 -> d14
                else -> throw IllegalStateException("Invalid checkbox")
            }
            checkForScore(dia)
        }
            for (cb in checkboxes) {
            cb.setOnCheckedChangeListener { _, _ ->
                // Check which array the checkbox is stored in
                cb.isEnabled = false
                if (checkboxes.all { it.isChecked }){
                    if (points > points2){
                        Toast.makeText(this, R.string.player_a_wins, Toast.LENGTH_LONG).show()
                    }
                    else if (points2 > points){
                        Toast.makeText(this, R.string.player_b_wins, Toast.LENGTH_LONG).show()
                    }
                }
                // Check if all of the other checkboxes in the array are checked
                if (playerOne){
                    cb.background = AppCompatResources.getDrawable(this,
                        R.drawable.checked_round_checkbox_blue
                    )
                    p1.setBackgroundColor(Color.TRANSPARENT)
                    p2.setBackgroundResource(R.color.red)
                }
                else{
                    cb.background = AppCompatResources.getDrawable(this,
                        R.drawable.checked_round_checkbox_red
                    )
                    p1.setBackgroundResource(R.color.blue)
                    p2.setBackgroundColor(Color.TRANSPARENT)
                }
                checkRow(cb)
                checkDiaginal(cb)
                checkDiaginal2(cb)
                playerOne = !playerOne
            }
        }
    }
}