package com.chefmenumanager

/** One row in the cart: a dish and how many of it were chosen. */
data class CartLine(val item: MenuItem, val quantity: Int) {
    val subtotalCents: Long
        get() = item.price.toCents() * quantity
}

/**
 * Holds the customer's cart. Only the dish id and quantity are stored, so if a dish's price is
 * edited the cart automatically uses the new price, and a deleted dish disappears from the cart.
 */
object CartRepository {

    const val MAX_QUANTITY = 99

    private val quantities = LinkedHashMap<Long, Int>()

    /** Adds one of the dish. Returns false when the maximum quantity has already been reached. */
    fun add(itemId: Long): Boolean {
        val current = quantities[itemId] ?: 0
        if (current >= MAX_QUANTITY) return false
        quantities[itemId] = current + 1
        return true
    }

    /** Removes one of the dish, and removes the row when the quantity reaches zero. */
    fun decrease(itemId: Long) {
        val current = quantities[itemId] ?: return
        if (current <= 1) quantities.remove(itemId) else quantities[itemId] = current - 1
    }

    fun remove(itemId: Long) {
        quantities.remove(itemId)
    }

    fun clear() {
        quantities.clear()
    }

    fun lines(): List<CartLine> = quantities.mapNotNull { (id, quantity) ->
        MenuRepository.findById(id)?.let { CartLine(it, quantity) }
    }

    /** Total number of dishes in the cart (a dish chosen twice counts as two). */
    fun totalItems(): Int = lines().sumOf { it.quantity }

    fun totalCents(): Long = lines().sumOf { it.subtotalCents }
}
