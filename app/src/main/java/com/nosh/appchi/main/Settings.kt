package com.nosh.appchi.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.nosh.appchi.BuildConfig
import com.nosh.appchi.R
import com.nosh.appchi.`fun`.Internet
import java.util.*

class Settings : AppCompatActivity(){
    private var tapCounter = 0
    private val handler = Handler()
    private val resetCounterRunnable = Runnable { tapCounter = 0 }

    @SuppressLint("UseSwitchCompatOrMaterialCode", "QueryPermissionsNeeded", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m_a_settings)
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()

        val currentMsl = sharedPreference.getBoolean("msl", true)
        val currentMslSpinner: Int = if (currentMsl) {0} else{1}
        val setMainLayoutCard: CardView = findViewById(R.id.set_main_layout)
        val ms: Spinner = findViewById(R.id.ms)
        val msAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.home_layouts))
        msAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ms.adapter = msAdapter
        ms.setSelection(currentMslSpinner)
        setMainLayoutCard.setOnClickListener {
            ms.performClick()
        }
        ms.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == 0){
                    setMainLayout(true)
                }
                else if (pos == 1){
                    setMainLayout(false)
                }
            }
        }


        val langCard: CardView = findViewById(R.id.langCard)
        val languageSpinner: Spinner = findViewById(R.id.ls)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.langs))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter
        langCard.setOnClickListener {
            languageSpinner.performClick()
        }
        val currentLang = sharedPreference.getString("lang", getString(R.string.choose_language))
        val currentLangPos = if (currentLang == "en") 1 else 2
        languageSpinner.setSelection(
            currentLangPos
        )

        languageSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (pos == 1 && currentLangPos != 1){setLocale(Locale("en")) }
                else if (pos == 2 && currentLangPos != 2){setLocale(Locale("he"))}
            }
        }

        val fbBtn: CardView = findViewById(R.id.feeCard)
        fbBtn.setOnClickListener{
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // only email apps should handle this
                putExtra(Intent.EXTRA_EMAIL, arrayOf("eyt.lev@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Feedback on AppChi app v"+ BuildConfig.VERSION_NAME)
            }
            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(emailIntent)
            }
        }


        val verText: TextView = findViewById(R.id.vertext)
        verText.text = "v"+ BuildConfig.VERSION_NAME
        val verCard: CardView = findViewById(R.id.verCard)
        verCard.setOnClickListener{
            secretCode()
        }


        val appchyswitch: Switch = findViewById(R.id.appchiswitch)
        val appchiCard: CardView = findViewById(R.id.appchiCard)
        appchiCard.isVisible= sharedPreference.getBoolean("sneezei", false)
        appchyswitch.isChecked = sharedPreference.getBoolean("sneeze", false)
        appchiCard.setOnClickListener{
            appchyswitch.isChecked = !appchyswitch.isChecked
        }

        appchyswitch.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                editor.putBoolean("sneeze", true)
            }
            else{
                editor.putBoolean("sneeze", false)
            }
            editor.apply()
        }

        val fartSwitch: Switch = findViewById(R.id.fartswitch)
        val fartCard: CardView = findViewById(R.id.fartCard)
        fartCard.isVisible= sharedPreference.getBoolean("farti", false)
        fartSwitch.isChecked = sharedPreference.getBoolean("fart", false)
        fartCard.setOnClickListener{
            fartSwitch.isChecked = !fartSwitch.isChecked
        }

        fartSwitch.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked) {
                editor.putBoolean("fart", true)
            }
            else{
                editor.putBoolean("fart", false)
            }
            editor.apply()
        }
    }

    private fun secretCode(){
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        tapCounter++
        if (tapCounter >= 7) {
            val builder = AlertDialog.Builder(this)
            // Set the dialog title and message
            builder.setMessage(R.string.enter_code)
            // Set up the input
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            // Set up the buttons
            builder.setPositiveButton(R.string.done) { _, _ ->
                when {
                    input.text.toString() == "אפצ'י" -> {
                        Toast.makeText(this, "לבריאות", Toast.LENGTH_SHORT).show()
                        editor.putBoolean("sneeze", true)
                        editor.putBoolean("sneezei", true)
                        editor.apply()
                    }
                    input.text.toString() == "פפפפפפפ" -> {
                        Toast.makeText(this, "איכסה", Toast.LENGTH_SHORT).show()
                        editor.putBoolean("fart", true)
                        editor.putBoolean("farti", true)
                        editor.apply()
                    }
                    input.text.toString().first().toString() == "." -> {
                        val intent = Intent(this, Internet::class.java)
                        intent.putExtra("url", input.text.toString())
                        startActivity(intent)
                    }
                }
            }
            builder.setNegativeButton(R.string.cancel) { _, _ ->
                // Do nothing
            }
            // Show the dialog
            val dialog = builder.create()
            dialog.show()
            // Do something when a user taps quickly 7 times on the CardView
            tapCounter = 0
        } else {
            // Reset the tap counter after 10 seconds if the user doesn't tap again
            handler.removeCallbacks(resetCounterRunnable)
            handler.postDelayed(resetCounterRunnable, 5000)
        }
    }

    private fun setMainLayout(divided: Boolean) {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putBoolean("msl", divided)
        editor.apply()
    }

    private fun setLocale(locale: Locale) {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        Locale.setDefault(locale)
        val resources = this.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        val currentLang = sharedPreference.getString("lang", getString(R.string.choose_language))
        editor.putString("lang", locale.toString())
        editor.apply()
        val newLang = sharedPreference.getString("lang", getString(R.string.choose_language))
        if (currentLang != newLang){
            finish()
            startActivity(intent)
        }
    }
}