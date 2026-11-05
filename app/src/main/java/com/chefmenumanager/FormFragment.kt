package com.chefmenumanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.chefmenumanager.databinding.FragmentFormBinding
import java.util.Locale

/** Used for both adding a new dish and editing an existing one. */
class FormFragment : Fragment() {

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!

    private var editingItem: MenuItem? = null

    private val navigator: Navigator
        get() = requireActivity() as Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = arguments?.getLong(ARG_ITEM_ID, NO_ITEM) ?: NO_ITEM
        editingItem = MenuRepository.findById(itemId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = editingItem
        if (item == null) {
            binding.formHeading.setText(R.string.form_heading_add)
            binding.formHint.setText(R.string.form_hint_add)
            binding.saveButton.setText(R.string.add_dish)
        } else {
            binding.formHeading.setText(R.string.form_heading_edit)
            binding.formHint.setText(R.string.form_hint_edit)
            binding.saveButton.setText(R.string.save_changes)
            fillFields(item)
        }

        // An error disappears as soon as the chef starts fixing that field.
        binding.nameInput.doOnTextChanged { _, _, _, _ -> binding.nameLayout.error = null }
        binding.descriptionInput.doOnTextChanged { _, _, _, _ -> binding.descriptionLayout.error = null }
        binding.priceInput.doOnTextChanged { _, _, _, _ -> binding.priceLayout.error = null }
        binding.courseGroup.setOnCheckedStateChangeListener { _, _ -> binding.courseError.visibility = View.GONE }

        binding.saveButton.setOnClickListener { saveDish() }
        binding.cancelButton.setOnClickListener {
            binding.root.hideKeyboard()
            navigator.openMenu()
        }
    }

    private fun fillFields(item: MenuItem) {
        binding.nameInput.setText(item.dishName)
        binding.descriptionInput.setText(item.description)
        binding.priceInput.setText(String.format(Locale.US, "%.2f", item.price))
        val chipId = when (item.course) {
            Courses.STARTER -> R.id.formStarter
            Courses.MAIN -> R.id.formMain
            else -> R.id.formDessert
        }
        binding.courseGroup.check(chipId)
    }

    private fun selectedCourse(): String? = when (binding.courseGroup.checkedChipId) {
        R.id.formStarter -> Courses.STARTER
        R.id.formMain -> Courses.MAIN
        R.id.formDessert -> Courses.DESSERT
        else -> null
    }

    private fun saveDish() {
        val dishName = binding.nameInput.text?.toString().orEmpty()
        val description = binding.descriptionInput.text?.toString().orEmpty()
        val priceText = binding.priceInput.text?.toString().orEmpty()
        val course = selectedCourse()

        val errors = MenuValidator.validate(dishName, description, course, priceText)
        showErrors(errors)
        if (errors.hasErrors) {
            navigator.showMessage(getString(R.string.message_fix_errors), isError = true)
            return
        }

        val name = dishName.trim()
        val price = Math.round((MenuValidator.parsePrice(priceText) ?: 0.0) * 100) / 100.0
        val existing = editingItem
        if (existing == null) {
            MenuRepository.add(name, description.trim(), course.orEmpty(), price)
            navigator.showMessage(getString(R.string.message_added, name), isError = false)
        } else {
            MenuRepository.update(existing.copy(dishName = name, description = description.trim(), course = course.orEmpty(), price = price))
            navigator.showMessage(getString(R.string.message_updated, name), isError = false)
        }

        binding.root.hideKeyboard()
        navigator.openMenu()
    }

    private fun showErrors(errors: FormErrors) {
        binding.nameLayout.error = errors.dishName?.let { getString(it) }
        binding.descriptionLayout.error = errors.description?.let { getString(it) }
        binding.priceLayout.error = errors.price?.let { getString(it) }
        if (errors.course != null) {
            binding.courseError.setText(errors.course)
            binding.courseError.visibility = View.VISIBLE
        } else {
            binding.courseError.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ITEM_ID = "item_id"
        private const val NO_ITEM = -1L

        /** Pass the id of the dish to edit, or null to start a blank form. */
        fun newInstance(itemId: Long?): FormFragment {
            val fragment = FormFragment()
            fragment.arguments = Bundle().apply { putLong(ARG_ITEM_ID, itemId ?: NO_ITEM) }
            return fragment
        }
    }
}
