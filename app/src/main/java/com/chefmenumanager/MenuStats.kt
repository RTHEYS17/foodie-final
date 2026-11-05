package com.chefmenumanager

/** The numbers shown on the Statistics screen. */
data class MenuStats(
    val total: Int,
    val averagePrice: Double,
    val countPerCourse: Map<String, Int>,
    val cheapest: MenuItem?,
    val priciest: MenuItem?
) {
    companion object {
        fun from(items: List<MenuItem>): MenuStats {
            val average = if (items.isEmpty()) 0.0 else items.sumOf { it.price } / items.size
            return MenuStats(
                total = items.size,
                averagePrice = average,
                countPerCourse = Courses.ALL.associateWith { course -> items.count { it.course == course } },
                cheapest = items.minByOrNull { it.price },
                priciest = items.maxByOrNull { it.price }
            )
        }
    }
}
