package com.chefmenumanager

/**
 * Holds the menu in memory and offers the add, update, delete, search and statistics
 * operations. Every screen uses this one object, so they always see the same data.
 */
object MenuRepository {

    private val items = SampleMenu.items.toMutableList()
    private var nextId = (items.maxOfOrNull { it.id } ?: 0L) + 1

    fun getAll(): List<MenuItem> = items.toList()

    fun count(): Int = items.size

    fun findById(id: Long): MenuItem? = items.find { it.id == id }

    fun add(dishName: String, description: String, course: String, price: Double): MenuItem {
        val item = MenuItem(nextId++, dishName, description, course, price)
        items.add(item)
        return item
    }

    fun update(updated: MenuItem) {
        val index = items.indexOfFirst { it.id == updated.id }
        if (index >= 0) items[index] = updated
    }

    fun delete(id: Long) {
        items.removeAll { it.id == id }
    }

    /** Dishes whose name contains [query] (ignoring case), limited to [course] when it is not null. */
    fun search(query: String, course: String?): List<MenuItem> {
        val text = query.trim()
        return items.filter { item ->
            val matchesName = text.isEmpty() || item.dishName.contains(text, ignoreCase = true)
            val matchesCourse = course == null || item.course == course
            matchesName && matchesCourse
        }
    }

    fun stats(): MenuStats = MenuStats.from(items)
}
