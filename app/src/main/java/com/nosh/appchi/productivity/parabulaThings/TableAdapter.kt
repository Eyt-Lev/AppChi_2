package com.nosh.appchi.productivity.parabulaThings

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nosh.appchi.R
import java.util.*

private const val PRESICE = 2

class TableAdapter(
    private val table: List<Pair<Double, Double>>
    ) : RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    class ViewHolder(val cardView: LinearLayout) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.r_parabula_table, parent, false) as LinearLayout
        return ViewHolder(cardView)
    }

    private fun Double.fixed(): String {
        return if (this % 1 == 0.0) {
            this.toInt().toString()
        } else {
            val splitedNum = this.toString().split(".")
            splitedNum[0] + "." + splitedNum[1].take(PRESICE)
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.cardView.findViewById<TextView>(R.id.x_text).text = table[position].first.fixed()
            holder.cardView.findViewById<TextView>(R.id.y_text).text = table[position].second.fixed()
        }

    override fun getItemCount() = table.size

}
