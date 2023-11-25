package com.nosh.appchi.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.nosh.appchi.R
import com.nosh.appchi.productivity.langs.Lang
import com.nosh.appchi.productivity.parabulaThings.ParabulaThings
import com.nosh.appchi.productivity.tasks.Todo

class ProductivityFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.m_f_prodactive, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button3).setOnClickListener {
            val intent = Intent(activity, Todo::class.java)
            startActivity(intent)
        }
        view.findViewById<Button>(R.id.button310).setOnClickListener{
            startActivity(Intent(activity, Lang::class.java))
        }
        view.findViewById<Button>(R.id.button314).setOnClickListener{
            startActivity(Intent(activity, ParabulaThings::class.java))
        }
    }
}