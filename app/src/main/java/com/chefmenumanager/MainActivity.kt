package com.chefmenumanager

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.chefmenumanager.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    // True while the code (not the user) changes the selected tab, so the listener ignores it.
    private var ignoreTabSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener { item ->
            if (!ignoreTabSelection) {
                when (item.itemId) {
                    R.id.nav_menu -> showMenuScreen()
                    R.id.nav_form -> showFormScreen(null)
                    R.id.nav_cart -> showCartScreen()
                    R.id.nav_stats -> showStatsScreen()
                }
            }
            true
        }
        binding.bottomNav.setOnItemReselectedListener { }

        // The back button returns to the menu first, and only then closes the app.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.bottomNav.selectedItemId != R.id.nav_menu) {
                    openMenu()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        if (savedInstanceState == null) {
            showMenuScreen()
        }
        cartChanged()
    }

    override fun openForm(itemToEdit: MenuItem?) {
        selectTab(R.id.nav_form)
        showFormScreen(itemToEdit)
    }

    override fun openMenu() {
        selectTab(R.id.nav_menu)
        showMenuScreen()
    }

    override fun openCart() {
        selectTab(R.id.nav_cart)
        showCartScreen()
    }

    // Shows how many dishes are in the cart as a small number on the Cart tab.
    override fun cartChanged() {
        val count = CartRepository.totalItems()
        if (count > 0) {
            binding.bottomNav.getOrCreateBadge(R.id.nav_cart).number = count
        } else {
            binding.bottomNav.removeBadge(R.id.nav_cart)
        }
    }

    override fun showMessage(message: String, isError: Boolean, actionLabel: String?, onAction: (() -> Unit)?) {
        val colorRes = if (isError) R.color.danger else R.color.success
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.bottomNav)
            .setBackgroundTint(ContextCompat.getColor(this, colorRes))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
        if (actionLabel != null && onAction != null) {
            snackbar.setAction(actionLabel) { onAction() }
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.white))
        }
        snackbar.show()
    }

    private fun showMenuScreen() {
        setFormTabTitle(editing = false)
        showScreen(MenuFragment(), R.string.subtitle_menu)
    }

    private fun showFormScreen(itemToEdit: MenuItem?) {
        setFormTabTitle(editing = itemToEdit != null)
        showScreen(FormFragment.newInstance(itemToEdit?.id), R.string.subtitle_form)
    }

    private fun showCartScreen() {
        setFormTabTitle(editing = false)
        showScreen(CartFragment(), R.string.subtitle_cart)
    }

    private fun showStatsScreen() {
        setFormTabTitle(editing = false)
        showScreen(StatsFragment(), R.string.subtitle_stats)
    }

    private fun showScreen(fragment: Fragment, subtitleRes: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        binding.headerSubtitle.setText(subtitleRes)
    }

    private fun selectTab(tabId: Int) {
        ignoreTabSelection = true
        binding.bottomNav.selectedItemId = tabId
        ignoreTabSelection = false
    }

    // The Add tab says "Edit dish" while a dish is being edited, otherwise "Add dish".
    private fun setFormTabTitle(editing: Boolean) {
        val title = if (editing) R.string.nav_edit else R.string.nav_add
        binding.bottomNav.menu.findItem(R.id.nav_form).setTitle(title)
    }
}
