package com.chefmenumanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.chefmenumanager.databinding.FragmentCartBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Shows the dishes in the cart, lets the customer change quantities, and checks out. */
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CartAdapter

    private val navigator: Navigator
        get() = requireActivity() as Navigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CartAdapter(
            onIncrease = { item -> increase(item) },
            onDecrease = { item -> decrease(item) },
            onRemove = { item -> remove(item) }
        )
        binding.cartList.layoutManager = LinearLayoutManager(requireContext())
        binding.cartList.adapter = adapter

        binding.checkoutButton.setOnClickListener { confirmCheckout() }
        binding.clearCartButton.setOnClickListener { clearCart() }
        binding.browseButton.setOnClickListener { navigator.openMenu() }

        refresh()
    }

    private fun increase(item: MenuItem) {
        if (!CartRepository.add(item.id)) {
            navigator.showMessage(getString(R.string.message_max_quantity), isError = true)
        }
        refresh()
    }

    private fun decrease(item: MenuItem) {
        CartRepository.decrease(item.id)
        refresh()
    }

    private fun remove(item: MenuItem) {
        CartRepository.remove(item.id)
        refresh()
        navigator.showMessage(getString(R.string.message_removed_from_cart, item.dishName), isError = false)
    }

    private fun clearCart() {
        CartRepository.clear()
        refresh()
        navigator.showMessage(getString(R.string.message_cart_cleared), isError = false)
    }

    // Redraws the list and the totals from the cart, and updates the number on the Cart tab.
    private fun refresh() {
        val lines = CartRepository.lines()
        val isEmpty = lines.isEmpty()

        adapter.submitList(lines)
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.cartList.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.cartFooter.visibility = if (isEmpty) View.GONE else View.VISIBLE

        binding.itemsValue.text = CartRepository.totalItems().toString()
        binding.totalValue.text = CartRepository.totalCents().centsToRand()

        navigator.cartChanged()
    }

    // Shows a receipt and asks the customer to confirm before the order is placed.
    private fun confirmCheckout() {
        val lines = CartRepository.lines()
        if (lines.isEmpty()) return

        val totalText = CartRepository.totalCents().centsToRand()
        val receipt = lines.joinToString("\n") { line ->
            "${line.quantity} x ${line.item.dishName} - ${line.subtotalCents.centsToRand()}"
        } + "\n\n" + getString(R.string.receipt_total, totalText)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_checkout_title)
            .setMessage(receipt)
            .setNegativeButton(R.string.back_to_cart, null)
            .setPositiveButton(R.string.place_order) { _, _ ->
                CartRepository.clear()
                refresh()
                navigator.showMessage(getString(R.string.message_order_placed, totalText), isError = false)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
