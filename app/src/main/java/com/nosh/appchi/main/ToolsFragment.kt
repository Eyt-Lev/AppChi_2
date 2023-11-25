package com.nosh.appchi.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.nosh.appchi.R
import com.nosh.appchi.tools.*

class ToolsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.m_f_tools, container, false)}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button3).setOnClickListener {
            val intent = Intent(activity, Metronome::class.java)
            startActivity(intent)
        }
        view.findViewById<Button>(R.id.button642).setOnClickListener{
            val intent = Intent(activity, BMI::class.java)
            startActivity(intent)
        }
        view.findViewById<Button>(R.id.button6).setOnClickListener{
            val intent = Intent(activity, Random::class.java)
            startActivity(intent)
        }
        view.findViewById<Button>(R.id.button235).setOnClickListener{
            val intent = Intent(activity, Light::class.java)
            startActivity(intent)
        }
        view.findViewById<Button>(R.id.button245).setOnClickListener{
            val intent = Intent(activity, Record::class.java)
            startActivity(intent)
        }
        view.findViewById<Button>(R.id.button221).setOnClickListener{
            val intent = Intent(activity, DWhatsApp::class.java)
            startActivity(intent)
        }
    }
}