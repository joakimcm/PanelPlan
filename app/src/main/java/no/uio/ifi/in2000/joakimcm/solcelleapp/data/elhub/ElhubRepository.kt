package no.uio.ifi.in2000.joakimcm.solcelleapp.data.elhub
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

// Included due to the TDD-related test from the team activity.
// Not implemented in the UI layer due to time constraints and other factors
// Aware it's not ideal to include this in main before full implementation.

@Singleton
open class ElhubRepository @Inject constructor(
    private val elhubDataSource: ElhubDataSource
) {
    //cache for temporaty storage - fewer API-calls
    private val monthlyConsumptionCache = mutableMapOf<Pair<Int, Int>, Double>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun avgMonthlyConsumptionPreviousYear(date: LocalDate): Map<Int, Double> {
        val monthlyData = mutableMapOf<Int, MutableList<Double>>()
        val zoneId = ZoneId.of("Europe/Oslo")

        for (month in 1..12) {
            val key = date.year to month

            // Check if the value is already cached
            val cachedValue = monthlyConsumptionCache[key]
            if (cachedValue != null) {
                // Hvis den er cached, kan du bruke den direkte og hoppe over beregningene
                monthlyData[month] = mutableListOf(cachedValue)
                continue
            }

            val daysInMonth = getDaysInMonth(date.year, month)
            val selectedDays = getRandomDays(daysInMonth, 15) // 15 random days

            for (day in selectedDays) {
                try {
                    val startDate = LocalDate.of(date.year, month, day).atStartOfDay(zoneId)
                    val endDate = startDate.plusDays(1)

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

                    val formattedStart = startDate.format(formatter)
                    println(formattedStart)

                    val formattedEnd = endDate.format(formatter)

                    val consumptionData = elhubDataSource.getMonthlyConsumptionPriceArea(
                        startDate = formattedStart,
                        endDate = formattedEnd
                    )

                    if (consumptionData.isNotEmpty()) {
                        val dailyAverage = consumptionData.map { it.quantityKwh }.average()*24
                        val monthlyAverage = dailyAverage * daysInMonth
                        val numHouseholdsInNO1 = 1104090
                        monthlyData.getOrPut(month) { mutableListOf() }.add(monthlyAverage/numHouseholdsInNO1)
                    }
                } catch (e: Exception) {
                    continue // continues if something goes wrong
                }
            }
            val monthAverage = monthlyData[month]?.average() ?: 0.0
            monthlyConsumptionCache[key] = monthAverage
        }

        return monthlyData.mapValues { (_, monthlyAverages) ->
            if (monthlyAverages.isNotEmpty()) monthlyAverages.average() else 0.0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDaysInMonth(year: Int, month: Int): Int {
        val monthDate = LocalDate.of(year, month, 1)
        return monthDate.lengthOfMonth() // Returns number of days in the month
    }

    private fun getRandomDays(daysInMonth: Int, numDays: Int): List<Int> {
        // Randomly select 'numDays' days from the range 1..daysInMonth
        return (1..daysInMonth).shuffled().take(numDays)
    }


}