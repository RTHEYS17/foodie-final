package com.chefmenumanager

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.util.Locale

/** Formats a number as South African Rand, e.g. 85.0 becomes "R85.00". */
fun Double.toRand(): String = String.format(Locale.US, "R%.2f", this)

fun View.hideKeyboard() {
    val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(windowToken, 0)
}

/** Prices are added up in cents (whole numbers) so the cart total never has rounding errors. */
fun Double.toCents(): Long = Math.round(this * 100)

/** Formats an amount in cents as Rand, e.g. 8950 becomes "R89.50". */
fun Long.centsToRand(): String = String.format(Locale.US, "R%d.%02d", this / 100, this % 100)
