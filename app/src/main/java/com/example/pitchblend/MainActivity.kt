package com.example.pitchblend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.pitchblend.databinding.ActivityMainBinding

private const val TAG_HOME = "home_fragment"
private const val TAG_EXPLORE = "explore_fragment"
private const val TAG_ANALYZE = "analyze_fragment"
private const val TAG_MY_PROFILE = "my_profile_fragment"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setFragment(TAG_HOME, HomeFragment())
        binding.navigationView.setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.fragment_home -> setFragment(TAG_HOME, HomeFragment())
                R.id.fragment_explore -> setFragment(TAG_EXPLORE, ExploreFragment())
                R.id.fragment_analyze -> setFragment(TAG_ANALYZE, AnalyzeFragment())
                R.id.fragment_my_profile -> setFragment(TAG_MY_PROFILE, MyProfileFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment)  {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.main_container, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val explore = manager.findFragmentByTag(TAG_EXPLORE)
        val analyze = manager.findFragmentByTag(TAG_ANALYZE)
        val my_profile = manager.findFragmentByTag(TAG_MY_PROFILE)

        if (home != null) {
            fragTransaction.hide(home)
        }
        if (explore != null) {
            fragTransaction.hide(explore)
        }
        if (analyze != null) {
            fragTransaction.hide(analyze)
        }
        if (my_profile != null) {
            fragTransaction.hide(my_profile)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        } else if (tag == TAG_EXPLORE) {
            if (explore != null) {
                fragTransaction.show(explore)
            }
        } else if (tag == TAG_ANALYZE) {
            if (analyze != null) {
                fragTransaction.show(analyze)
            }
        } else if (tag == TAG_MY_PROFILE) {
            if (my_profile != null) {
                fragTransaction.show(my_profile)
            }
        }

        fragTransaction.commitAllowingStateLoss()

    }
}