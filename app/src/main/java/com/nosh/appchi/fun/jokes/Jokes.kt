@file:Suppress("NAME_SHADOWING")

package com.nosh.appchi.`fun`.jokes

import android.annotation.SuppressLint
import android.app.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.nosh.appchi.R
import java.io.*
import java.util.*

class Jokes : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.f_a_jokes)
        val tabLayout = findViewById<TabLayout>(R.id.tabsi)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pageri)
        val myViewPageAdapter =
            MyViewPagerAdapterJokes(this)

        viewPager2.adapter = myViewPageAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager2.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)?.select()
            }})
    }
}

