package com.chefmenumanager

/** What the screens (fragments) can ask the main activity to do. */
interface Navigator {
    /** Opens the form. Pass a dish to edit it, or null to add a new one. */
    fun openForm(itemToEdit: MenuItem?)

    fun openMenu()

    fun openCart()

    /** Call after the cart changes so the number on the Cart tab is updated. */
    fun cartChanged()

    /** Shows a success or error message at the bottom of the screen, with an optional action button. */
    fun showMessage(
        message: String,
        isError: Boolean,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    )
}
