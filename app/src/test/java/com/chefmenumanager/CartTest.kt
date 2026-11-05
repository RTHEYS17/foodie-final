package com.chefmenumanager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CartTest {

    @Before
    fun emptyCart() {
        CartRepository.clear()
    }

    @Test
    fun menu_hasOneHundredDishes() {
        assertEquals(100, MenuRepository.count())
        assertTrue(MenuRepository.search("", Courses.STARTER).isNotEmpty())
        assertTrue(MenuRepository.search("", Courses.MAIN).isNotEmpty())
        assertTrue(MenuRepository.search("", Courses.DESSERT).isNotEmpty())
    }

    @Test
    fun total_isCorrectWhenSeveralOfTheSameDishAreChosen() {
        val first = MenuRepository.findById(1)!!
        val second = MenuRepository.findById(2)!!
        CartRepository.add(first.id)
        CartRepository.add(first.id)
        CartRepository.add(second.id)

        val expected = first.price.toCents() * 2 + second.price.toCents()
        assertEquals(expected, CartRepository.totalCents())
        assertEquals(3, CartRepository.totalItems())
        assertEquals(2, CartRepository.lines().size)
    }

    @Test
    fun decrease_removesTheRowAtZero() {
        CartRepository.add(1)
        CartRepository.add(1)
        CartRepository.decrease(1)
        assertEquals(1, CartRepository.totalItems())
        CartRepository.decrease(1)
        assertTrue(CartRepository.lines().isEmpty())
        assertEquals(0L, CartRepository.totalCents())
    }

    @Test
    fun quantity_isCappedAtMaximum() {
        repeat(CartRepository.MAX_QUANTITY) { assertTrue(CartRepository.add(3)) }
        assertFalse(CartRepository.add(3))
        assertEquals(CartRepository.MAX_QUANTITY, CartRepository.totalItems())
    }

    @Test
    fun centsAreExact() {
        assertEquals(5997L, (19.99 * 1).toCents() * 3)
        assertEquals("R59.97", 5997L.centsToRand())
        assertEquals("R5.05", 505L.centsToRand())
    }

    @Test
    fun deletedDishLeavesTheCart() {
        val added = MenuRepository.add("Test Dish", "Only used in a test", Courses.MAIN, 10.0)
        CartRepository.add(added.id)
        assertEquals(1000L, CartRepository.totalCents())
        MenuRepository.delete(added.id)
        assertEquals(0L, CartRepository.totalCents())
    }
}
