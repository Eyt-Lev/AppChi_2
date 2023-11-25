package com.nosh.appchi.productivity.langs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nosh.appchi.R

interface OnItemClickListener {
    fun onItemClick(name: String)
}

class SavedAdapter(private val wordsEn: List<String>, context: Context) : RecyclerView.Adapter<SavedAdapter.ViewHolder>() {
    private val sharedPreference = context.getSharedPreferences("PREFERENCE_NAME", AppCompatActivity.MODE_PRIVATE)

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    
    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.p_r_lang, parent, false) as CardView
        return ViewHolder(cardView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = wordsEn[position].substringBefore("_words_en")
        val awEn = sharedPreference.getString((name + "_words_en"), "error")?.replace("[","")
            ?.replace("]","")?.split(", ")
        val awHe = sharedPreference.getString((name + "_words_he"), "error")?.replace("[","")
            ?.replace("]","")?.split(", ")
        holder.itemView.setOnClickListener {
            listener?.onItemClick(name)
        }
        holder.cardView.findViewById<TextView>(R.id.failed_text_en).text = name
        holder.cardView.findViewById<TextView>(R.id.failed_text_he).text = "${awEn?.get(0)}, ${awEn?.get(1)}, ${awEn?.get(2)}..."
        holder.cardView.findViewById<TextView>(R.id.mistakes_count).text = "${awHe?.get(0)}, ${awHe?.get(1)}, ${awHe?.get(2)}..."
    }

    override fun getItemCount() = wordsEn.size
}
