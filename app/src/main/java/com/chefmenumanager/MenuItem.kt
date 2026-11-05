package com.chefmenumanager

/** One dish on the menu. */
data class MenuItem(
    val id: Long,
    val dishName: String,
    val description: String,
    val course: String,
    val price: Double
)
