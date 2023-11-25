package com.nosh.appchi.productivity.parabulaThings

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nosh.appchi.R
import kotlin.math.sqrt

private const val PRESICE = 2

class ParabulaThings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_a_parabula_things)

        val enterA: EditText = findViewById(R.id.enter_a)
        val enterB: EditText = findViewById(R.id.enter_b)
        val enterC: EditText = findViewById(R.id.enter_c)
        val solveBtn: Button = findViewById(R.id.solve_btn)
        val vertexTxt: TextView = findViewById(R.id.vertx_text)
        val iwtyaTxt: TextView = findViewById(R.id.iwtya_text)
        val iwtxaTxt: TextView = findViewById(R.id.iwtxa_text)
        val tableBtn: Button = findViewById(R.id.table_btn)
        val tableRecyclerView: RecyclerView = findViewById(R.id.table_rv)
        val tableLayout: LinearLayout = findViewById(R.id.tableLayout)
        val solveLayout: LinearLayout = findViewById(R.id.solveLayout)
        val evaluateBtn: Button = findViewById(R.id.evulateBtn)

        evaluateBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            // Get the layout inflater
            builder.setTitle(R.string.evulate)
            val inflater = this.layoutInflater
            val view = inflater.inflate(R.layout.d_evulate, null)
            // Inflate and set the layout for the dialog

            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.done) { _, _ ->
                    val a = enterA.text.toString().toDouble()
                    val b = enterB.text.toString().toDouble()
                    val c = enterC.text.toString().toDouble()
                    val parabulaToSolve = Parabula(a, b, c)
                    val x = view.findViewById<EditText>(R.id.enter_x).text.toString().toDouble()
                    val y = parabulaToSolve.evaluate(x)
                    val builderi = AlertDialog.Builder(this)
                    builderi.setMessage("X: ${x.fixed()}, Y: ${y.fixed()}")
                        .setNegativeButton(R.string.cancel) { _, _ -> }
                        .create().show()
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
            builder.create().show()

        }

        solveBtn.setOnClickListener {
            val a = enterA.text.toString().toDouble()
            val b = enterB.text.toString().toDouble()
            val c = enterC.text.toString().toDouble()
            val parabulaToSolve = Parabula(a, b, c)
            solveLayout.isVisible = true
            val (vertexX, vertexY) = parabulaToSolve.findKodod()
            val vertexFixed = "X: ${vertexX.fixed()}, Y: ${vertexY.fixed()}"
            val (iwtyxX, iwtyxY) = parabulaToSolve.findYpoint()
            val iwtyxFixed = "X: ${iwtyxX.fixed()}, Y: ${iwtyxY.fixed()}"
            val xPoints = parabulaToSolve.findXPoints()
            var xFixed = ""
            if (xPoints.isNotEmpty()) {
                for (i in xPoints.indices){
                    val pointNow = xPoints[i]
                    xFixed += "X: ${pointNow.first.fixed()} Y: ${pointNow.second.fixed()}\n"
                }
            } else {
                xFixed = getString(R.string.no_Intersections_with_x)
            }

            vertexTxt.text = vertexFixed
            iwtyaTxt.text = iwtyxFixed
            iwtxaTxt.text = xFixed
        }

        tableBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            // Get the layout inflater
            val inflater = this.layoutInflater
            val view = inflater.inflate(R.layout.d_parabula_table, null)
            // Inflate and set the layout for the dialog
            val startEdt = view.findViewById<EditText>(R.id.enter_start_table)
            val endEdt = view.findViewById<EditText>(R.id.enter_end_table)
            val stepEdt = view.findViewById<EditText>(R.id.enter_step_table)
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.done) { _, _ ->
                    val start = startEdt.text.toString().toInt()
                    val end = endEdt.text.toString().toInt()
                    val step = stepEdt.text.toString().toInt()
                    val a = enterA.text.toString().toDouble()
                    val b = enterB.text.toString().toDouble()
                    val c = enterC.text.toString().toDouble()
                    val parabulaToSolve = Parabula(a, b, c)
                    val table = parabulaToSolve.makeTable(start, end, step)
                    tableLayout.isVisible = true
                    val adapter = TableAdapter(table)
                    tableRecyclerView.adapter = adapter
                    tableRecyclerView.layoutManager = LinearLayoutManager(this)
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
            builder.create().show()
        }
    }


    private fun Double.fixed(): String {
        return if (this % 1 == 0.0) {
            this.toInt().toString()
        } else {
            val splitedNum = this.toString().split(".")
            splitedNum[0] + "." + splitedNum[1].take(PRESICE)
        }
    }

}

class Parabula(
    private val a: Double,
    private val b: Double,
    private val c: Double
){

    fun findIntersectionWith(other: Parabula): List<Pair<Double, Double>> {
        val result = mutableListOf<Pair<Double, Double>>()
            val deltaA = a - other.a
            val deltaB = b - other.b
            val deltaC = c - other.c
            val delta = deltaB * deltaB - 4 * deltaA * deltaC

            if (delta >= 0) {
                val x1 = (-deltaB + sqrt(delta)) / (2 * deltaA)
                val x2 = (-deltaB - sqrt(delta)) / (2 * deltaA)
                result.add(Pair(x1, evaluate(x1)))
                if (x1 != x2) {
                    result.add(Pair(x2, evaluate(x2)))
                }
            }
        return result
    }

    fun makeTable(start: Int, end: Int, step: Int): List<Pair<Double, Double>>{
        val result = mutableListOf<Pair<Double, Double>>()
        for (i in start..end step step) {
            result.add(Pair(i.toDouble(), evaluate(i.toDouble())))
        }
        return result
    }
    // Evaluate the y-coordinate of the parabola at a given x-coordinate
    fun evaluate(x: Double): Double {
        return a * x * x + b * x + c
    }

    fun findYpoint(): Pair<Double, Double>{
        return Pair(0.0, c)
    }

    fun findKodod(): Pair<Double, Double> {
        val x = -b / (a * 2)
        val y = (a * (x*x)) + (b*x) + c
        return Pair(x, y)
    }

    fun findXPoints(): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()

        val discriminant = b * b - 4 * a * c

        if (discriminant < 0.0) {
            points.clear()
            return points
        }
        else if (discriminant == 0.0) {
            // one real root, parabola intersects x-axis at one point
            val root = -b / (2 * a)
            points.add(Pair(root, 0.0))
        } else {
            // two real roots, parabola intersects x-axis at two points
            val root1 = (-b + sqrt(discriminant)) / (2 * a)
            val root2 = (-b - sqrt(discriminant)) / (2 * a)
            points.add(Pair(root1, 0.0))
            points.add(Pair(root2, 0.0))
        }
        return points
    }
}