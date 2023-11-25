package com.nosh.appchi.tools

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.nosh.appchi.R


class Record : AppCompatActivity() {

    private var isRecording = false
    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.t_a_record)

        MobileAds.initialize(this) {}
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        chcekPerm()
        val rectoggle: ToggleButton = findViewById(R.id.rectoggle)
        val sens: SeekBar = findViewById(R.id.sens)
        val senst: TextView = findViewById(R.id.senst)
        val toggleButton: ImageButton = findViewById(R.id.playButton)

        sens.max = 999
        sens.min = 10
        sens.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                senst.text = sens.progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        sens.progress = 38

        rectoggle.setOnCheckedChangeListener{_, isChecked->
            if (isChecked){
                startRecording(sens.progress)
                toggleButton.isEnabled = false
            }
            else{
                stopRecording()
                toggleButton.isEnabled = true
            }
        }
        toggleButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->{
                    startRecording(sens.progress)}
                MotionEvent.ACTION_CANCEL -> {
                    stopRecording()
                }
                MotionEvent.ACTION_UP ->{
                    stopRecording()}
            }
            false
        }
    }

    private fun startRecording(senp: Int) {
        val toggleButton: ImageButton = findViewById(R.id.playButton)
        toggleButton.setImageResource(R.drawable.ic_baseline_pause_24)
        toggleButton.scaleX = 4F
        toggleButton.scaleY = 4F
        isRecording = true
        val bufferSize = AudioRecord.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_IN_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_STEREO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioTrack = AudioTrack.Builder()
            .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(44100).setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).build())
            .setBufferSizeInBytes(bufferSize)
            .build()
        audioTrack?.play()

        val audioData = ByteArray(bufferSize)
        audioRecord?.startRecording()
        val volume = if (senp < 100){ "0.0$senp" } else { "0.$senp" }.toFloat()
        audioTrack?.setVolume(volume)

        val recordingThread = Thread {
            while (isRecording) {
                val read = audioRecord?.read(audioData, 0, bufferSize)
                if (read != null) {
                    audioTrack?.write(audioData, 0, read)
                }
            }
        }
        recordingThread.start()
    }

    private fun stopRecording() {
        val toggleButton: ImageButton = findViewById(R.id.playButton)
        toggleButton.setImageResource(R.drawable.ic_baseline_fiber_manual_record_24)
        toggleButton.scaleX = 3F
        toggleButton.scaleY = 3F
        isRecording = false
        audioRecord!!.stop()
        audioTrack!!.stop()
        audioTrack!!.release()
    }

    private var permissionGranted = false

    private fun chcekPerm(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1
            )
            return
        }
        permissionGranted = true
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (!permissionGranted) {
                    finish()
                }
            }
        }
    }
}
