package com.nosh.appchi.`fun`

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nosh.appchi.R

class Fart : AppCompatActivity() {

    private var mMediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.f_activity_fart)

        val buttonIds = listOf(R.id.fart1, R.id.fart2, R.id.fart3, R.id.fart4,
            R.id.fart5, R.id.fart6, R.id.fart7, R.id.fart8, R.id.fart9,
            R.id.fart10, R.id.fart11, R.id.fart12)

        buttonIds.forEachIndexed { index, buttonId ->
            findViewById<Button>(buttonId).setOnClickListener {
                fart(index + 1)
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun fart(fart: Int) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, this.resources.
            getIdentifier("fart$fart", "raw", this.packageName))
            mMediaPlayer!!.start()
        } else{ mMediaPlayer!!.start()}
            mMediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }
}