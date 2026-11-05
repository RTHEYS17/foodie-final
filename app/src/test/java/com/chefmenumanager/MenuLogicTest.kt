package com.chefmenumanager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuLogicTest {

    private val sample = listOf(
        MenuItem(1, "Soup", "Hot tomato soup", Courses.STARTER, 60.0),
        MenuItem(2, "Steak", "Grilled sirloin steak", Courses.MAIN, 180.0),
        MenuItem(3, "Cake", "Chocolate cake slice", Courses.DESSERT, 60.0)
    )

    @Test
    fun validForm_hasNoErrors() {
        val errors = MenuValidator.validate("Soup", "Hot tomato soup", Courses.STARTER, "60,50")
        assertTrue(!errors.hasErrors)
    }

    @Test
    fun emptyForm_reportsEveryField() {
        val errors = MenuValidator.validate("", "", null, "")
        assertEquals(R.string.error_name_required, errors.dishName)
        assertEquals(R.string.error_description_required, errors.description)
        assertEquals(R.string.error_course_required, errors.course)
        assertEquals(R.string.error_price_required, errors.price)
    }

    @Test
    fun badPrices_areRejected() {
        assertEquals(R.string.error_price_invalid, MenuValidator.validate("Soup", "Hot soup", Courses.STARTER, "abc").price)
        assertEquals(R.string.error_price_low, MenuValidator.validate("Soup", "Hot soup", Courses.STARTER, "0").price)
        assertEquals(R.string.error_price_high, MenuValidator.validate("Soup", "Hot soup", Courses.STARTER, "10000").price)
    }

    @Test
    fun parsePrice_acceptsCommaAndDot() {
        assertEquals(85.5, MenuValidator.parsePrice("85,5")!!, 0.0001)
        assertEquals(85.5, MenuValidator.parsePrice("85.5")!!, 0.0001)
        assertNull(MenuValidator.parsePrice("NaN"))
    }

    @Test
    fun stats_countAndAverage() {
        val stats = MenuStats.from(sample)
        assertEquals(3, stats.total)
        assertEquals(100.0, stats.averagePrice, 0.0001)
        assertEquals(1, stats.countPerCourse[Courses.MAIN])
        assertEquals("Steak", stats.priciest?.dishName)
    }

    @Test
    fun stats_emptyMenu() {
        val stats = MenuStats.from(emptyList())
        assertEquals(0, stats.total)
        assertEquals(0.0, stats.averagePrice, 0.0001)
        assertNull(stats.cheapest)
    }
}
