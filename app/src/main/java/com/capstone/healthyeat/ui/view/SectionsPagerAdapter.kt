package com.capstone.healthyeat.ui.view

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.capstone.healthyeat.ui.view.history.HistoryFragment
import com.capstone.healthyeat.ui.view.main.HomeFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    var username: String? = null

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = HistoryFragment()
            1 -> fragment = HomeFragment()
        }
        return fragment as Fragment
    }
}