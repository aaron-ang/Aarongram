package com.example.aarongram.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.aarongram.R
import com.example.aarongram.fragments.ComposeFragment
import com.example.aarongram.fragments.FeedFragment
import com.example.aarongram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseUser

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.setTitleTextAppearance(this, R.style.grandHotelTextAppearance)
        if (isLightTheme(this)) {
            toolbar.setBackgroundColor(resources.getColor(R.color.white))
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.action_home -> {
                    fragment = FeedFragment()
                }
                R.id.action_compose -> {
                    fragment = ComposeFragment()
                }
                R.id.action_profile -> {
                    fragment = ProfileFragment()
                }
            }
            if (fragment != null) {
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit()
            }
            // Return true to signify user interaction is handled
            true
        }
        bottomNavigationView.selectedItemId = R.id.action_home
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_option -> logoutUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() {
        ParseUser.logOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isLightTheme(activity: Activity): Boolean {
        return activity.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO
    }

    companion object {
        val TAG = MainActivity::class.simpleName as String
    }
}