package com.nosh.appchi.productivity.tasks

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nosh.appchi.R
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class Todo : AppCompatActivity() {

    private val list = arrayListOf<TaskActivity.TodoModel>()
    var adapter = TodoAdapter(list)
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_a_tasks)
        MobileAds.initialize(this) {}
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        initSwipe()
        findViewById<FloatingActionButton>(R.id.newtbtn).setOnClickListener{
            startActivity(Intent(this, TaskActivity::class.java))
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setupTasks(){
        val todoRv: RecyclerView = findViewById(R.id.todoRv)
        todoRv.adapter = adapter
        todoRv.layoutManager = LinearLayoutManager(this)
        db.todoDao().getTask().observe(this) {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
            } else {
                list.clear()
            }
            adapter.notifyDataSetChanged()
        }
        adapter.setOnItemClickListener(object : EditTask {
            override fun onItemClick(position: Int){
                val fixedTime = convertLongToTime(list[position].time)
                val fixedDate = convertLongToDate(list[position].date)
                val builder = AlertDialog.Builder(this@Todo)
                builder.setTitle(list[position].title)
                builder.setMessage(
                    getString(R.string.task_desc) + ": ${list[position].description}\n" +
                    getString(R.string.task_cat) + ": ${list[position].category}\n" +
                    getString(R.string.task_time) + ": $fixedDate\n" +
                    getString(R.string.task_date) + ": $fixedTime"
                )
                builder.setPositiveButton("OK") { _, _ ->
                }
                builder.setNeutralButton(R.string.edit){_,_ ->
                    val aa = Intent(this@Todo, TaskActivity::class.java)
                    aa.putExtra("id", list[position].id.toInt())
                    startActivity(aa)
                }
                val dialog = builder.create()
                dialog.show()
            }
        })
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return format.format(date)
    }

    fun convertLongToDate(date: Long): String {
        val datei = Date(date)
        val formati = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formati.format(datei)
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun initSwipe() {

        @SuppressLint("NotifyDataSetChanged")
        fun delete(position: Int) {
            val builder = AlertDialog.Builder(this@Todo)
            builder.setMessage(R.string.delete)
            builder.setPositiveButton(R.string.yes) { _, _ ->
                val taskToDelete = list[position].id
                runBlocking {
                    val deferred = async {
                        db.todoDao().deleteTask(taskToDelete)
                    }
                    deferred.await()
                }
            }
            builder.setNegativeButton(R.string.cancel){_,_ ->
                adapter.notifyDataSetChanged()
            }
            val dialog = builder.create()
            dialog.show()
        }

        fun finish(position: Int) {
            db.todoDao().finishTask(list[position].id)
        }

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    delete(position)
                } else if (direction == ItemTouchHelper.RIGHT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        finish(position)
                    }
                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val icon: Bitmap

                    if (dX > 0) {
                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_check_white_png)
                        paint.color = Color.parseColor("#388E3C")
                        canvas.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left.toFloat() + dX, itemView.bottom.toFloat(), paint
                        )
                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat(),
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    } else {
                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_delete_white_png)
                        paint.color = Color.parseColor("#D32F2F")
                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                        )
                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }
                    viewHolder.itemView.translationX = dX
                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        val todoRv: RecyclerView = findViewById(R.id.todoRv)
        itemTouchHelper.attachToRecyclerView(todoRv)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                displayTodo()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                displayTodo()
                return true
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    displayTodo(newText)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun displayTodo(newText: String = "") {
        db.todoDao().getTask().observe(this) {
            if (it.isNotEmpty()) {
                list.clear()
                list.addAll(
                    it.filter { todo ->
                        todo.title.contains(newText, true)
                    }
                )
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupTasks()
    }
}
