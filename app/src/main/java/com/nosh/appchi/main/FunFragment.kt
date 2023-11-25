package com.nosh.appchi.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.nosh.appchi.`fun`.Fart
import com.nosh.appchi.`fun`.jokes.Jokes
import com.nosh.appchi.R
import com.nosh.appchi.`fun`.Hallel
import com.nosh.appchi.`fun`.Internet


class FunFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.m_f_fun, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreference = requireActivity().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        view.findViewById<Button>(R.id.button3).setOnClickListener {
            val intent = Intent(activity, Jokes::class.java)
            startActivity(intent)
        }
        val hallel = view.findViewById<Button>(R.id.button23)
        hallel.setOnClickListener {
            val intent = Intent(activity, Hallel::class.java)
            startActivity(intent)
        }
        val fart = view.findViewById<Button>(R.id.button347)
        fart.isVisible = sharedPreference.getBoolean("fart", false)
        fart.setOnClickListener {
            val intent = Intent(activity, Fart::class.java)
            startActivity(intent)
        }
    }
}