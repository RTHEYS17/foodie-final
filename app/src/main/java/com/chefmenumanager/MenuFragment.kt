package com.chefmenumanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chefmenumanager.databinding.FragmentMenuBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Shows every dish, with search, a course filter, edit and delete. */
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MenuAdapter
    private var courseFilter: String? = null // null means "All"

    private val navigator: Navigator
        get() = requireActivity() as Navigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MenuAdapter(
            onAddToCart = { item -> addToCart(item) },
            onEdit = { item -> navigator.openForm(item) },
            onDelete = { item -> confirmDelete(item) }
        )
        binding.menuList.layoutManager = LinearLayoutManager(requireContext())
        binding.menuList.adapter = adapter

        binding.searchInput.doOnTextChanged { _, _, _, _ -> refreshList() }

        binding.filterGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            courseFilter = when (checkedIds.firstOrNull()) {
                R.id.chipStarter -> Courses.STARTER
                R.id.chipMain -> Courses.MAIN
                R.id.chipDessert -> Courses.DESSERT
                else -> null
            }
            refreshList()
        }

        binding.clearButton.setOnClickListener { clearSearchAndFilter() }

        refreshList()
    }

    private fun clearSearchAndFilter() {
        binding.searchInput.setText("")
        binding.filterGroup.check(R.id.chipAll)
        courseFilter = null
        refreshList()
        binding.root.hideKeyboard()
    }

    // Applies the current search text and course filter to the list.
    private fun refreshList() {
        val query = binding.searchInput.text?.toString().orEmpty()
        val results = MenuRepository.search(query, courseFilter)
        val total = MenuRepository.count()
        val isFiltering = query.isNotBlank() || courseFilter != null

        adapter.submitList(results)
        binding.clearButton.visibility = if (isFiltering) View.VISIBLE else View.GONE

        binding.countText.text = when {
            isFiltering -> getString(R.string.showing_count, results.size, total)
            total == 1 -> getString(R.string.menu_count_one)
            else -> getString(R.string.menu_count_many, total)
        }

        updateEmptyState(isEmpty = results.isEmpty(), menuIsEmpty = total == 0)
    }

    private fun updateEmptyState(isEmpty: Boolean, menuIsEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.menuList.visibility = if (isEmpty) View.GONE else View.VISIBLE
        if (!isEmpty) return

        if (menuIsEmpty) {
            binding.emptyTitle.setText(R.string.empty_menu_title)
            binding.emptyText.setText(R.string.empty_menu_text)
            binding.emptyButton.setText(R.string.add_dish_action)
            binding.emptyButton.setOnClickListener { navigator.openForm(null) }
        } else {
            binding.emptyTitle.setText(R.string.empty_filter_title)
            binding.emptyText.setText(R.string.empty_filter_text)
            binding.emptyButton.setText(R.string.clear_filters_action)
            binding.emptyButton.setOnClickListener { clearSearchAndFilter() }
        }
    }

    private fun addToCart(item: MenuItem) {
        if (CartRepository.add(item.id)) {
            navigator.cartChanged()
            navigator.showMessage(
                getString(R.string.message_added_to_cart, item.dishName),
                isError = false,
                actionLabel = getString(R.string.view_cart),
                onAction = { navigator.openCart() }
            )
        } else {
            navigator.showMessage(getString(R.string.message_max_quantity), isError = true)
        }
    }

    private fun confirmDelete(item: MenuItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_title)
            .setMessage(getString(R.string.dialog_delete_message, item.dishName))
            .setNegativeButton(R.string.keep_dish, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                MenuRepository.delete(item.id)
                CartRepository.remove(item.id)
                navigator.cartChanged()
                refreshList()
                navigator.showMessage(getString(R.string.message_deleted, item.dishName), isError = false)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
