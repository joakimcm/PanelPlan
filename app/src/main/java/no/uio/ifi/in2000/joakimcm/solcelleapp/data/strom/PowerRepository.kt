package no.uio.ifi.in2000.joakimcm.solcelleapp.data.strom


import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/*
This class chooses 15 random days in each month from the gived year (We have set it to last year, so we have a full year)
The repository calls the data source one time for each day of the month, for all the months in the year.
At the end of the program, we aggregate the results and get a monthly average price for power.
 */

@Singleton
class PowerRepository @Inject constructor(
    private val powerDataSource: PowerDataSource,
) {
    private val cache: MutableMap<String, Map<Int, Double>> = mutableMapOf()
    suspend fun getMonthlyAverage2024(year: LocalDate): Map<Int, Double> {
        val monthlyData = mutableMapOf<Int, MutableList<Double>>()
        val cacheKey = year.toString()
        if (cache.containsKey(cacheKey)) {
            return cache[cacheKey] ?: emptyMap()
        }

        for (month in 1..12) {
            // Get list ov all days in month
            val daysInMonth = getDaysInMonth(year.year, month)
            val selectedDays = getRandomDays(daysInMonth, 15) // Random choice of 15 days

            for (day in selectedDays) {
                val date = try {
                    LocalDate.of(year.year, month, day)
                } catch (e: Exception) {
                    continue // Ignore days that does not exist
                }

                val cost = powerDataSource.getPowerPrice(date)

                // Calculate daily average from hourly data
                val dailyAverage = cost.map { it.NOK_per_kWh }.average()

                // Save daily information about this month
                monthlyData.getOrPut(month) { mutableListOf() }.add(dailyAverage)
            }
        }

        // Calculate monthly average from all the daily averages
        val monthlyAverage = monthlyData.mapValues { (_, dailyAverages) ->
            if (dailyAverages.isNotEmpty()) dailyAverages.average() else 0.0
        }
        // Save result from API in memory cache
        cache[cacheKey] = monthlyAverage

        return monthlyAverage
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        val monthDate = LocalDate.of(year, month, 1)
        return monthDate.lengthOfMonth() // Returns number of days in that month
    }

    private fun getRandomDays(daysInMonth: Int, numDays: Int): List<Int> {
        // Choose random day
        return (1..daysInMonth).shuffled().take(numDays)
    }
}