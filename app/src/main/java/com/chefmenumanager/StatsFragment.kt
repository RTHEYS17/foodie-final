package com.chefmenumanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chefmenumanager.databinding.FragmentStatsBinding
import com.google.android.material.progressindicator.LinearProgressIndicator
import android.widget.TextView

/** Shows an overview of the menu: totals, average price, dishes per course and price range. */
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stats = MenuRepository.stats()
        if (stats.total == 0) {
            binding.statsEmpty.visibility = View.VISIBLE
            binding.statsContent.visibility = View.GONE
            return
        }

        binding.totalValue.text = stats.total.toString()
        binding.averageValue.text = stats.averagePrice.toRand()

        val largest = maxOf(1, stats.countPerCourse.values.maxOrNull() ?: 1)
        showCourseCount(binding.starterCount, binding.starterBar, stats.countPerCourse[Courses.STARTER] ?: 0, largest)
        showCourseCount(binding.mainCount, binding.mainBar, stats.countPerCourse[Courses.MAIN] ?: 0, largest)
        showCourseCount(binding.dessertCount, binding.dessertBar, stats.countPerCourse[Courses.DESSERT] ?: 0, largest)

        stats.priciest?.let {
            binding.priciestValue.text = getString(R.string.stats_dish_with_price, it.dishName, it.price.toRand())
        }
        stats.cheapest?.let {
            binding.cheapestValue.text = getString(R.string.stats_dish_with_price, it.dishName, it.price.toRand())
        }
    }

    private fun showCourseCount(label: TextView, bar: LinearProgressIndicator, count: Int, largest: Int) {
        label.text = count.toString()
        bar.max = largest
        bar.progress = count
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
