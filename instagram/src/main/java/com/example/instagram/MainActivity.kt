package com.example.instagram

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.instagram.fragments.HomeFragment
import com.example.instagram.fragments.NotificationsFragment
import com.example.instagram.fragments.ProfileFragment
import com.example.instagram.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView



class MainActivity : AppCompatActivity() {
    internal var selectedFragment: Fragment? = null
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                selectedFragment = HomeFragment()
            }
            R.id.nav_search -> {
                selectedFragment = SearchFragment()
            }
            R.id.nav_add_post -> {
            }
            R.id.nav_notification -> {
                selectedFragment = NotificationsFragment()
            }
            R.id.nav_profile -> {
                selectedFragment = ProfileFragment()
            }
        }
    if(selectedFragment != null){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
        selectedFragment!!
    }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }
}

