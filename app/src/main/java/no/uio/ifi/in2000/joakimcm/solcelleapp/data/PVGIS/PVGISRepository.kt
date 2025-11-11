package no.uio.ifi.in2000.joakimcm.solcelleapp.data.PVGIS

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PVGISRepository @Inject constructor(
    private val PVGISDataSource: PVGISDataSource,
) {
    private var cache: MutableMap<String, Map<Int, Double>> = mutableMapOf()

    // Main function
    suspend fun getMonthlyRadiationForAddress(
        lon: String,
        lat: String,
        angle: Double?,
    ): Map<Int, Double> {
        val cacheKey = "$lon - $lat - $angle"
        if (cache.containsKey(cacheKey)) {
            return cache[cacheKey] ?: emptyMap()
        }
        // Get monthly irradiation for each address
        val response = PVGISDataSource.getMonthlyRadiation(lat, lon, angle)

        val monthlyAverageMap = mutableMapOf<Int, Double>()

        // For each month, group the data for that month and calculate average
        response.monthly.groupBy { it.month }.forEach { (month, data) ->
            val average = data.map { it.irradiation }.average()
            monthlyAverageMap[month] = average
        }
        // Save result from API in memory cache
        cache[cacheKey] = monthlyAverageMap

        return monthlyAverageMap

    }
}
