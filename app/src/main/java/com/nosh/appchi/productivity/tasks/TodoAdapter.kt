package com.nosh.appchi.productivity.tasks

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nosh.appchi.R
import java.text.SimpleDateFormat
import java.util.*

interface EditTask {
    fun onItemClick(position: Int)
}

// first create adapter class. This inherits recycler view. Recycler view now requires view holder
class TodoAdapter(private val list: List<TaskActivity.TodoModel>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    // 3 functions of the view holder
    // 1st func
    // In this Layout inflater is called which converts view in such a form that adapter can consume it

    private var listener: EditTask? = null

    fun setOnItemClickListener(listener: EditTask?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.p_r_item_todo, parent, false)
        )
    }

    override fun getItemCount() = list.size


    // 2nd func
    // this will set data in each card
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.itemView.findViewById<RelativeLayout>(R.id.taski).setOnClickListener {
            listener?.onItemClick(position)
        }
        holder.itemView.findViewById<View>(R.id.viewColorTag).setOnClickListener {
            listener?.onItemClick(position)
        }
        holder.bind(list[position]) // we are passing the object of the list that we made in the ToDoModel.kt
    }

    // 3rd func
    override fun getItemId(position: Int): Long {
        return list[position].id
    }


    // view holder is present inside the recycler view
    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(todoModel: TaskActivity.TodoModel) {
            with(itemView) {
                val viewColorTag: View = findViewById(R.id.viewColorTag)
                val txtShowCategory :TextView = findViewById(R.id.txtShowCategory)
                val txtShowTask :TextView = findViewById(R.id.txtShowTask)
                val txtShowTitle :TextView = findViewById(R.id.txtShowTitle)
                val colors = resources.getIntArray(R.array.random_color)
                val randomColor = colors.random()
                viewColorTag.setBackgroundColor(randomColor)
                txtShowTitle.text = todoModel.title
                txtShowTask.text = todoModel.description
                txtShowCategory.text = todoModel.category
                updateTime(todoModel.date)
                updateDate(todoModel.time)
            }
        }
        @SuppressLint("SimpleDateFormat")
        private fun updateTime(time: Long) {
            //Mon, 5 Jan 2020
            val myformat = "h:mm a"
            val sdf = SimpleDateFormat(myformat)
            itemView.findViewById<TextView>(R.id.txtShowTime).text = sdf.format(Date(time))
        }

        @SuppressLint("SimpleDateFormat")
        private fun updateDate(time: Long) {
            //Mon, 5 Jan 2020
            val myformat = "EEE, d MMM yyyy"
            val sdf = SimpleDateFormat(myformat)
            itemView.findViewById<TextView>(R.id.txtShowDate).text = sdf.format(Date(time))
        }
    }
}

