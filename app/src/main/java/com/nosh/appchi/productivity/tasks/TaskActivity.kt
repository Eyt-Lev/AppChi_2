@file:OptIn(DelicateCoroutinesApi::class)

package com.nosh.appchi.productivity.tasks

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.material.textfield.TextInputLayout
import com.nosh.appchi.R
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

const val DB_NAME = "todo.db"

class TaskActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var myCalendar: Calendar
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
    private lateinit var dateEdt: EditText
    private lateinit var timeEdt: EditText
    private lateinit var saveBtn: Button
    private lateinit var spinnerCategory: Spinner
    private lateinit var titleInpLay: EditText
    private lateinit var taskInpLay: EditText
    private lateinit var timeInputLay: TextInputLayout
    private lateinit var enableNotCb: CheckBox
    private lateinit var setNotTimeLayout: LinearLayout
    private var finalDate = 0L
    private var finalTime = 0L
    private var finalDateSet = false
    private var finalTimeSet = false
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_a_edit_task)
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        dateEdt = findViewById(R.id.dateEdt)
        timeEdt = findViewById(R.id.timeEdt)
        saveBtn = findViewById(R.id.saveBtn)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        titleInpLay = findViewById(R.id.titleInpLay)
        taskInpLay = findViewById(R.id.taskInpLay)
        timeInputLay = findViewById(R.id.timeInptLay)
        enableNotCb = findViewById(R.id.enable_not_cb)
        setNotTimeLayout = findViewById(R.id.set_not_tine)
        val imgRemoveCategory: ImageView = findViewById(R.id.imgRemoveCategory)
        val imgAddCategory: ImageView = findViewById(R.id.imgAddCategory)
        dateEdt.setOnClickListener(this)
        enableNotCb.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        imgRemoveCategory.setOnClickListener(this)
        imgAddCategory.setOnClickListener(this)
        if (intent.hasExtra("id")) {
            timeInputLay.visibility = View.VISIBLE
            editTaskMode()
            return
        }
        if (sharedPreference.getString("labels", "")!!.isNotEmpty()) setUpSpinner()
        saveBtn.setOnClickListener(this)
    }

    private fun editTaskMode() {
        db.todoDao().getAll().observe(this@TaskActivity) {
            if (!it.isNullOrEmpty()) {
                val task = it[intent.getIntExtra("id", -1)-1]
                timeEdt.setText(Todo().convertLongToDate(task.date))
                dateEdt.setText(Todo().convertLongToTime(task.time))
                titleInpLay.setText(task.title)
                taskInpLay.setText(task.description)
                //set spinner
                val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
                val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!
                val arrayString = sharedPreference.getString("labels", "")
                if (arrayString!!.isNotEmpty()) {
                    val array = arrayString.split(",").toMutableList()
                    array.removeAll(listOf(""))
                    if (!array.contains(task.category)) array.add(task.category)
                    val adapter =
                        ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, array)
                    spinnerCategory.adapter = adapter
                    spinnerCategory.setSelection(adapter.getPosition(task.category))
                }
                //change button behavior
                saveBtn.setOnClickListener{ editTask(task) }
            }
            else{ Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show() }
        }
        //set all editTexts
    }

    private fun editTask(task: TodoModel) {
        val id = intent.getIntExtra("id", -1)
        val category =
            if (spinnerCategory.selectedItem != null){
                spinnerCategory.selectedItem.toString()
            }
            else {
                ""
            }
        val title = titleInpLay.text.toString()
        val description = taskInpLay.text.toString()
        val time = if (finalTimeSet) { finalTime } else{ task.date }
        val date = if (finalDateSet) { finalDate } else{ task.time }
        GlobalScope.launch(Dispatchers.IO) {
            db.todoDao().updateTask(
                id.toLong(),
                title,
                description,
                category,
                time,
                date,
                0)
        }
        finish()
    }

    private fun setUpSpinner() {
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!
        val arrayString = sharedPreference.getString("labels", "")
        val array = arrayString!!.split(",").toMutableList()
        array.removeAll(listOf(""))
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, array)
        spinnerCategory.adapter = adapter
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dateEdt -> {
                setListener()
            }
            R.id.enable_not_cb -> {
                setNotTimeLayout.isVisible = enableNotCb.isChecked
            }
            R.id.timeEdt -> {
                setTimeListener()
            }
            R.id.saveBtn -> {
                saveTodo()
            }
            R.id.imgAddCategory -> {
                addCat()
            }
            R.id.imgRemoveCategory -> {
                removeCat()
            }
        }
    }

    private fun removeCat(){
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)!!
        val arrayString = sharedPreference.getString("labels", "")
        val array = arrayString!!.split(",")

        val builder = AlertDialog.Builder(this)
        // Set the dialog title and message
        builder.setMessage(R.string.remove_cat)
        // Set up the view
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL

        array.forEach { item ->
            val checkBox = CheckBox(this)
            checkBox.text = item
            checkBox.tag = item
            linearLayout.addView(checkBox)
        }

        builder.setView(linearLayout)

        // Set up the buttons
        builder.setPositiveButton(R.string.save) { _, _ ->
            // Create a new array to store the items that are not selected for removal
            val newArray = array.filterNot { item ->
                val checkBox = linearLayout.findViewWithTag<CheckBox>(item)
                checkBox?.isChecked ?: false
            }
            // Convert the array to a string and store it in SharedPreferences
            val newArrayString = newArray.joinToString(separator = ",")
            val editor = sharedPreference.edit()
            editor.putString("labels", newArrayString)
            editor.apply()
            //setup the spinner
            setUpSpinner()
            Toast.makeText(this, R.string.done, Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }

        // Show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun addCat(){
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        val arrayString = sharedPreference.getString("labels", "")
        val builder = AlertDialog.Builder(this)
        // Set the dialog title and message
        builder.setMessage(R.string.add_cat)
        // Set up the input
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        // Set up the buttons
        builder.setPositiveButton(R.string.save) { _, _ ->
            // Get the input value from the edit text
            val inputValue = input.text.toString()
            // Retrieve the array from SharedPreferences
            val array = arrayString!!.split(",")
            // Add the input value to the array
            val newArray = array + inputValue
            // Convert the array to a string and store it in SharedPreferences
            val newArrayString = newArray.joinToString(separator = ",")
            editor.putString("labels", newArrayString)
            editor.apply()
            setUpSpinner()
        }
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            // Do nothing
        }
        // Show the dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveTodo() {
        val category =
            if (spinnerCategory.selectedItem != null){
                spinnerCategory.selectedItem.toString()
            }
        else {
            ""
            }
        val title = titleInpLay.text.toString()
        val description = taskInpLay.text.toString()
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                return@withContext db.todoDao().insertTask(
                    TodoModel(
                        title,
                        description,
                        category,
                        finalTime,
                        finalDate
                    )
                )
            }
            finish()
        }
    }

    @Entity
    data class TodoModel(
        var title:String,
        var description:String,
        var category: String,
        var date:Long,
        var time:Long,
        var isFinished : Int = 0,
        @PrimaryKey(autoGenerate = true)
        var id:Long = 0,
        var isDeleted: Int = 0
    )

    private fun setTimeListener() {
        myCalendar = Calendar.getInstance()

        timeSetListener =
            TimePickerDialog.OnTimeSetListener{ _: TimePicker, hourOfDay: Int, min: Int ->
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                myCalendar.set(Calendar.MINUTE, min)
                finalTimeSet = true
                updateTime()
            }

        val timePickerDialog = TimePickerDialog(
            this, timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE), false
        )
        timePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateTime() {
        val timeEdt: EditText = findViewById(R.id.timeEdt)
        //Mon, 5 Jan 2020
        val myformat = "h:mm a"
        val sdf = SimpleDateFormat(myformat)
        finalTime = myCalendar.time.time
        timeEdt.setText(sdf.format(myCalendar.time))
    }

    private fun setListener() {
        myCalendar = Calendar.getInstance()

        dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDate()
                finalDateSet = true
            }

        val datePickerDialog = DatePickerDialog(
            this, dateSetListener, myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateDate() {
        val dateEdt: EditText = findViewById(R.id.dateEdt)
        //Mon, 5 Jan 2020
        val myformat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myformat)
        finalDate = myCalendar.time.time
        dateEdt.setText(sdf.format(myCalendar.time))
        timeInputLay.visibility = View.VISIBLE
    }
}
