package com.chefmenumanager

import androidx.annotation.StringRes

/** Error messages (as string resources) for each field. A null field means it is valid. */
data class FormErrors(
    @StringRes val dishName: Int? = null,
    @StringRes val description: Int? = null,
    @StringRes val course: Int? = null,
    @StringRes val price: Int? = null
) {
    val hasErrors: Boolean
        get() = dishName != null || description != null || course != null || price != null
}

object MenuValidator {

    private const val MAX_PRICE = 9999.0

    /** Turns the price text typed by the chef into a number. Accepts "85.50" and "85,50". */
    fun parsePrice(text: String): Double? {
        val price = text.trim().replace(',', '.').toDoubleOrNull()
        return if (price != null && price.isFinite()) price else null
    }

    fun validate(dishName: String, description: String, course: String?, priceText: String): FormErrors {
        val name = dishName.trim()
        val nameError = when {
            name.isEmpty() -> R.string.error_name_required
            name.length < 2 -> R.string.error_name_short
            name.length > 50 -> R.string.error_name_long
            else -> null
        }

        val desc = description.trim()
        val descriptionError = when {
            desc.isEmpty() -> R.string.error_description_required
            desc.length < 5 -> R.string.error_description_short
            desc.length > 200 -> R.string.error_description_long
            else -> null
        }

        val courseError = if (course in Courses.ALL) null else R.string.error_course_required

        val price = parsePrice(priceText)
        val priceError = when {
            priceText.isBlank() -> R.string.error_price_required
            price == null -> R.string.error_price_invalid
            price <= 0 -> R.string.error_price_low
            price > MAX_PRICE -> R.string.error_price_high
            else -> null
        }

        return FormErrors(nameError, descriptionError, courseError, priceError)
    }
}
