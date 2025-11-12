package com.example.instagram.socialActivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.instagram.R
import com.example.instagram.chattoActivity.Main1Activity
import com.example.instagram.fragments.socialFragments.HomeFragment
import com.example.instagram.fragments.socialFragments.NotificationsFragment
import com.example.instagram.fragments.socialFragments.ProfileFragment
import com.example.instagram.fragments.socialFragments.SearchFragment


class MainActivity : AppCompatActivity() {


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                item.isChecked = false
                startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notification -> {
                moveToFragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val fabMessage: FloatingActionButton = findViewById(R.id.fab_message)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        fabMessage.setOnClickListener {
            val chatIntent = Intent(this@MainActivity, Main1Activity::class.java)
            startActivity(chatIntent)
        }

        moveToFragment(HomeFragment())
    }


    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }
}
