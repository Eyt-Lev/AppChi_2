package com.nosh.appchi.`fun`.jokes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nosh.appchi.R
import java.io.File
import java.io.FileInputStream

class AllJokesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.f_f_all_jokes, container, false)}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val path = requireActivity().filesDir
        val letDirectory = File(path, "LET")
        val file = File(letDirectory, "jokes.txt")
        val inputAsString = FileInputStream(file).bufferedReader().use { it.readText() }

        val allJokes: EditText = view.findViewById(R.id.all_jokes)
        allJokes.setText(inputAsString)

        val fab: FloatingActionButton = view.findViewById(R.id.floatingActionButton2)
        fab.setOnClickListener{
            writeJokes(allJokes.text.toString())
            Toast.makeText(requireActivity(), R.string.done, Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeJokes(jokes: String) {
        val path = requireActivity().filesDir
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        val file = File(letDirectory, "jokes.txt")
        file.writeText(jokes)
    }
}