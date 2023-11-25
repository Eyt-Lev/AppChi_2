package com.nosh.appchi.tools

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.nosh.appchi.R
import java.security.SecureRandom


class Random : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.t_a_random)

        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(this,
            R.array.dice, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this

        val sharedPreference: SharedPreferences =  getSharedPreferences("PREFERENCE_NAME", MODE_PRIVATE)
        val chooseRadio: RadioButton = findViewById(R.id.chosedradio)
        val insertRadio: RadioButton = findViewById(R.id.insertradio)
        val passwordRadio: RadioButton = findViewById(R.id.passwordradio)
        val num1: EditText = findViewById(R.id.num1)
        val num2: EditText = findViewById(R.id.num2)
        val lettres :CheckBox = findViewById(R.id.lettres)
        val special :CheckBox = findViewById(R.id.special)
        val uppercaseLetters :CheckBox = findViewById(R.id.uppercase_letters)
        val numbers :CheckBox = findViewById(R.id.numbers)
        val passLength: EditText = findViewById(R.id.editTextNumber)
        chooseRadio.isChecked=true
        num2.isEnabled=false
        num1.isEnabled=false
        spinner.isEnabled=true
        lettres.isVisible=false
        special.isVisible=false
        uppercaseLetters.isVisible=false
        numbers.isVisible=false
        passLength.isEnabled=false

        chooseRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                num2.isEnabled=false
                num1.isEnabled=false
                lettres.isVisible=false
                special.isVisible=false
                uppercaseLetters.isVisible=false
                numbers.isVisible=false
                passLength.isEnabled=false
                spinner.isEnabled=true
            }
        }
        insertRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                num2.isEnabled=true
                num1.isEnabled=true
                lettres.isVisible=false
                special.isVisible=false
                uppercaseLetters.isVisible=false
                numbers.isVisible=false
                passLength.isEnabled=false
                spinner.isEnabled=false
            }
        }
        passwordRadio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                num2.isEnabled=false
                num1.isEnabled=false
                lettres.isVisible=true
                special.isVisible=true
                uppercaseLetters.isVisible=true
                numbers.isVisible=true
                passwordRadio.isEnabled=true
                passLength.isEnabled=true
                spinner.isEnabled=false
            }
        }
        val btnRandom: Button = findViewById(R.id.btnrandom)
        val result: TextView = findViewById(R.id.textView)

        btnRandom.setOnClickListener{
            if (chooseRadio.isChecked){
                diceMode(result, sharedPreference)
            }
            else if(insertRadio.isChecked) {
                randomMode(result, num1, num2)
            }
            else if (passwordRadio.isChecked){
                passMode(passLength, lettres, uppercaseLetters, numbers, special, result)
            }
        }
    }


    private fun diceMode(result: TextView, sharedPreference: SharedPreferences) {
        result.text = random(1, sharedPreference.getInt("random2", 6)).toString()
    }

    private fun randomMode(result: TextView, num1: EditText, num2: EditText) {
        if (num1.text.isEmpty()){
            num1.error = getString(R.string.field_empty)
            return
        }
        if (num2.text.isEmpty()){
            num2.error = getString(R.string.field_empty)
            return
        }
        val inputString1 = num2.text.toString()
        val inputString2 = num1.text.toString()

        val input1 = try {
            inputString1.toInt()
        } catch (e: NumberFormatException) {
            num2.error = getString(R.string.too_big_num)
            return
        }
        val input2 = try {
            inputString2.toInt()
        } catch (e: NumberFormatException) {
            num1.error = getString(R.string.too_big_num)
            return
        }
        if (input1 > input2){
            num2.error = getString(R.string.random_err)
            return
        }
        result.text = random(input1, input2).toString()
    }

    private fun passMode(passLength: EditText, lettres: CheckBox, uppercaseLetters: CheckBox, numbers: CheckBox, special: CheckBox, result: TextView) {
        var password = ""
        class CopyPassword : View.OnClickListener {
            override fun onClick(v: View) {
                val clipboard: ClipboardManager =
                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("password", password)
                clipboard.setPrimaryClip(clip)
            }
        }
        class PasswordManager {
            val letters : String = "abcdefghijklmnopqrstuvwxyz"
            val uppercaseLetters : String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val numbers : String = "0123456789"
            val special : String = "@#=+!£$%&?"
            fun generatePassword(isWithLetters: Boolean, isWithUppercase: Boolean, isWithNumbers: Boolean, isWithSpecial: Boolean, length: Int) : String {
                var resultPass = ""
                var i = 0
                if(isWithLetters){ resultPass += this.letters }
                if(isWithUppercase){ resultPass += this.uppercaseLetters }
                if(isWithNumbers){ resultPass += this.numbers }
                if(isWithSpecial){ resultPass += this.special }

                val rnd = SecureRandom.getInstance("SHA1PRNG")
                val sb = StringBuilder(length)

                while (i < length) {
                    val randomInt : Int = rnd.nextInt(resultPass.length)
                    sb.append(resultPass[randomInt])
                    i++
                }
                return sb.toString()
            }
        }
        val myPasswordManager = PasswordManager()
        val passLengthInput = passLength.text.toString()

        if (passLengthInput.isEmpty()) {
            passLength.error = getString(R.string.field_empty)
            return
        }
        if (passLengthInput.toInt() > 100) {
            passLength.error = getString(R.string.pass_length_err)
            return
        }
        if (!(lettres.isChecked || uppercaseLetters.isChecked || numbers.isChecked || special.isChecked)) {
            Toast.makeText(applicationContext,"Error",Toast.LENGTH_SHORT).show()
            return
        }

        password = myPasswordManager.generatePassword(
            lettres.isChecked,
            uppercaseLetters.isChecked,
            numbers.isChecked,
            special.isChecked,
            passLengthInput.toInt()
        )
        result.text = password
        val mySnackBar = Snackbar.make(
            findViewById(R.id.myCoordinatorLayout),
            R.string.password_generated,
            Snackbar.LENGTH_LONG
        )
        mySnackBar.setAction(R.string.coppy, CopyPassword())
        mySnackBar.show()
    }

    override fun onItemSelected(parent: AdapterView<*>,view:View?,pos:Int,id:Long){
        val sharedPreference: SharedPreferences =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreference.edit()
        var dice = 4
        when(pos){
            0 -> dice=2
            1 -> dice=4
            2 -> dice=6
            3 -> dice=8
            4 -> dice=10
            5 -> dice=12
            6 -> dice=20
        }
        editor.putInt("random2", dice)
        editor.apply()
        }
    override fun onNothingSelected(parent: AdapterView<*>) {}

    private fun random(a: Int, b: Int): Int {
        return (a..b).random()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.explain -> {
                /*val builder = AlertDialog.Builder(this)
                builder.setMessage(//TODO)
                builder.setPositiveButton("OK") { _, _ ->
                }
                val dialog = builder.create()
                dialog.show()*/
            }
            R.id.shortcut -> {
                val shortcutManager = getSystemService(ShortcutManager::class.java)

                if (shortcutManager!!.isRequestPinShortcutSupported) {
                    // Assumes there's already a shortcut with the ID "my-shortcut".
                    // The shortcut must be enabled.
                    val pinShortcutInfo = ShortcutInfo.Builder(this, "random").build()

                    // Create the PendingIntent object only if your app needs to be notified
                    // that the user allowed the shortcut to be pinned. Note that, if the
                    // pinning operation fails, your app isn't notified. We assume here that the
                    // app has implemented a method called createShortcutResultIntent() that
                    // returns a broadcast intent.
                    val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo)

                    // Configure the intent so that your app's broadcast receiver gets
                    // the callback successfully.For details, see PendingIntent.getBroadcast().
                    val successCallback = PendingIntent.getBroadcast(this, /* request code */ 0,
                        pinnedShortcutCallbackIntent, /* flags */ 0)

                    shortcutManager.requestPinShortcut(pinShortcutInfo,
                        successCallback.intentSender)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}