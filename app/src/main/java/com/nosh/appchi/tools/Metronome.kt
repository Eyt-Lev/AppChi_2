package com.nosh.appchi.tools

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nosh.appchi.R


@Suppress("DEPRECATION")
class Metronome : AppCompatActivity() {
    var autoIncrement: Boolean = false
    var autoDecrement: Boolean = false
    private var repeatUpdateHandler: Handler = Handler()
    val mainHandler = Handler(Looper.getMainLooper())

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.t_a_metronome)
        val seek: SeekBar = findViewById(R.id.seekBar2)
        MobileAds.initialize(this) {}
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        class RepetitiveUpdater : Runnable {
            override fun run() {
                if (autoIncrement) {
                    seek.progress += 4
                    repeatUpdateHandler.postDelayed(RepetitiveUpdater(), 50L)
                } else if (autoDecrement) {
                    seek.progress -= 4
                    repeatUpdateHandler.postDelayed(RepetitiveUpdater(), 50L)
                }
            }
        }

        val button: ToggleButton = findViewById(R.id.toggleButton)
        val text: TextView = findViewById(R.id.textView2)
        seek.isEnabled = button.isChecked
        seek.progress = 60
        text.text = 100.toString()
        val addbtn: ImageButton = findViewById(R.id.addbtn)
        val removebtn: ImageButton = findViewById(R.id.removebtn)
        removebtn.setOnClickListener {
            seek.progress -= 2
        }
        addbtn.setOnClickListener {
            seek.progress += 2
        }
        addbtn.setOnLongClickListener {
            autoIncrement = true
            repeatUpdateHandler.post(RepetitiveUpdater())
            false
        }
        addbtn.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP && autoIncrement) {
                autoIncrement = false
            }
            false
        }
        removebtn.setOnLongClickListener {
            autoDecrement = true
            repeatUpdateHandler.post(RepetitiveUpdater())
            false
        }
        removebtn.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP && autoDecrement) {
                autoDecrement = false
            }
            false
        }

        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val seekfix = seek.progress + 40
                text.text = seekfix.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        button.setOnCheckedChangeListener { _, isChecked ->
            seek.isEnabled = isChecked
        }
        mainHandler.post(object : Runnable {
            override fun run() {
                if (button.isChecked) {
                    playSound()
                }
                val seekfix = seek.progress + 40
                val bpm: Double = (60.0 / seekfix.toDouble())
                mainHandler.postDelayed(this, ((bpm * 1000).toLong()))
            }
        })
    }

    fun playSound() {
        val tone = ToneGenerator.TONE_PROP_BEEP
        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        toneGenerator.startTone(tone)
        toneGenerator.release()
    }

    override fun onStop() {
        super.onStop()
        mainHandler.removeCallbacksAndMessages(null)
    }
}

