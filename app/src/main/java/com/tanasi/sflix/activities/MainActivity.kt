package com.tanasi.sflix.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.tanasi.navigation.widget.setupWithNavController
import com.tanasi.sflix.R
import com.tanasi.sflix.databinding.ActivityMainBinding
import com.tanasi.sflix.fragments.player.PlayerFragment

class MainActivity : FragmentActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = (supportFragmentManager
            .findFragmentById(binding.navMainFragment.id) as NavHostFragment)
            .navController

        binding.navMain.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.search,
                R.id.home,
                R.id.movies,
                R.id.tv_shows -> binding.navMain.visibility = View.VISIBLE
                else -> binding.navMain.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navMainFragment.id) as NavHostFragment
        val navController = navHostFragment.navController

        when (navController.currentDestination?.id) {
            R.id.home -> when {
                binding.navMain.hasFocus() -> finish()
                else -> binding.navMain.requestFocus()
            }
            R.id.search,
            R.id.movies,
            R.id.tv_shows -> when {
                binding.navMain.hasFocus() -> binding.navMain.findViewById<View>(R.id.home).let {
                    it.requestFocus()
                    it.performClick()
                }
                else -> binding.navMain.requestFocus()
            }
            else -> {
                val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()
                when (currentFragment) {
                    is PlayerFragment -> currentFragment.onBackPressed()
                    else -> false
                }.takeIf { !it }?.let {
                    super.onBackPressed()
                }
            }
        }
    }
}