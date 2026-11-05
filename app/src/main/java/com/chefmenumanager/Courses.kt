package com.chefmenumanager

import androidx.annotation.ColorRes

/** The three courses a dish can belong to, and the colour used for each. */
object Courses {
    const val STARTER = "Starter"
    const val MAIN = "Main Course"
    const val DESSERT = "Dessert"

    val ALL = listOf(STARTER, MAIN, DESSERT)

    @ColorRes
    fun colorFor(course: String): Int = when (course) {
        STARTER -> R.color.course_starter
        MAIN -> R.color.course_main
        else -> R.color.course_dessert
    }
}
