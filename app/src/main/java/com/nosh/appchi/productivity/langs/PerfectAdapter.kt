package com.nosh.appchi.productivity.langs

import android.annotation.SuppressLint
import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nosh.appchi.R
import java.util.*

class PerfectAdapter(private val perfectEn: List<String>, private val perfectHe: List<String>, private val context: Context) : RecyclerView.Adapter<PerfectAdapter.ViewHolder>() {
    private lateinit var tts: TextToSpeech

    private fun setTts(callback: (Boolean) -> Unit) {
        tts = TextToSpeech(context) { status ->
            val isTtsInitialized = if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.US)
                !(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            } else {
                false
            }
            callback(isTtsInitialized)
        }
    }
    init {
        setTts { isTtsInitialized ->
            if (!isTtsInitialized) {
                Toast.makeText(context, "Tts not supported", Toast.LENGTH_LONG).show()
            }
        }
    }

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.p_r_lang, parent, false) as CardView
        return ViewHolder(cardView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.setOnClickListener {
            setTts { isTtsInitialized ->
                if (!isTtsInitialized) {
                    Toast.makeText(context, "Tts not supported", Toast.LENGTH_LONG).show()
                }
                else{
                    tts.speak(perfectEn[position], TextToSpeech.QUEUE_ADD, null, null)
                }
            }
        }
        holder.cardView.findViewById<TextView>(R.id.failed_text_en).text = perfectEn[position]
        holder.cardView.findViewById<TextView>(R.id.failed_text_he).text = perfectHe[position]
        holder.cardView.findViewById<TextView>(R.id.mistakes_count).isVisible = false
    }


    override fun getItemCount() = perfectHe.size
}
