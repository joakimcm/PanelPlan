package no.uio.ifi.in2000.joakimcm.solcelleapp.data.frost

import android.util.Log
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost.FrostApiResponse
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost.MonthlyWeatherData
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost.WeatherElements
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/*
This repository aggregates the monthly data from the data source and returns a
Map with a MonthlyWeatherData-object associated to each month of the year.

In this case, we aggregate over each month one by one (for example january 2008, january 2008 and so on..)
and find the most typical month. The returned map will give information about the most typical year from this location.
 */
@Singleton
class FrostRepository @Inject constructor(
    private val frostDataSource: FrostDataSource,
) {
    private val cache: MutableMap<String, Map<Int, MonthlyWeatherData>> = mutableMapOf()

    // Main function. Calls the other functions to return the final object.
    suspend fun getObservationFromLocation(lon: String, lat: String): Map<Int, MonthlyWeatherData> {
        val cacheKey = "$lon - $lat"
        if (cache.containsKey(cacheKey)) {
            return cache[cacheKey] ?: emptyMap()
        }

        val locationResponse = frostDataSource.getLocation(lon, lat)
        val source = locationResponse?.data?.firstOrNull()?.id

        return if (source != null) {
            // Makes a list over the weather elements. See package "model/frost"
            val elementIds: List<WeatherElements> = WeatherElements.entries.toList()

            // For each element, I make a call to getObservations
            val observations = elementIds.associateWith { element ->
                val response = getObservations(source, element)
                // Check if I get enough observations in response (here >= 100)
                val isValidResponse = response?.let {
                    it.currentItemCount >= 100
                } ?: false
                // Associate each weather element with null if not enough data
                if (isValidResponse) response else null
            }
            // Save result from API in memory cache
            cache[cacheKey] = extractAverageObservations(observations)

            // Makes a map which connects each month to the three weather elements
            return extractAverageObservations(observations)
        } else {
            emptyMap()
        }
    }

    // Function that's called from getObservationsFromLocation
    private suspend fun getObservations(
        source: String,
        element: WeatherElements,
    ): FrostApiResponse? {
        val response = frostDataSource.getObservations(source, element)

        val urlD = response?.currentLink
        if (urlD != null) {
            Log.d("Observation URL: ", urlD)
        }
        return response
    }

    // Aggregate data
    private fun extractAverageObservations(weatherResponses: Map<WeatherElements, FrostApiResponse?>): Map<Int, MonthlyWeatherData> {
        // Makes a map fro each weather element
        val temperatureData = mutableMapOf<Int, MutableList<Double>>()
        val snowData = mutableMapOf<Int, MutableList<Double>>()
        val cloudData = mutableMapOf<Int, MutableList<Double>>()

        // Iterate and aggregate each weather element
        // Finds average month from the whole data set
        weatherResponses.forEach { (elementId, response) ->
            response?.data?.forEach { observationData ->
                // Extract the month from each observation
                val month = observationData.referenceTime.substring(5, 7).toInt()

                observationData.observations.forEach { observation ->
                    // Puts the value in the correct month and weather element
                    when (elementId) {
                        WeatherElements.AIR_TEMPERATURE -> temperatureData.getOrPut(month) { mutableListOf() }
                            .add(observation.value)

                        WeatherElements.SNOW_DEPTH -> snowData.getOrPut(month) { mutableListOf() }
                            .add(observation.value)

                        WeatherElements.CLOUD_COVERAGE -> cloudData.getOrPut(month) { mutableListOf() }
                            .add(observation.value)
                    }
                }
            }
        }

        // Function to find average and the correct decimal point.
        fun List<Double>.averageOrNull(): Double? =
            if (isNotEmpty()) (average() * 10).roundToInt() / 10.0 else null

        // Makes and returns map that associates each month with the average (normal values)
        // You can find the data type MonthlyWeatherData in package "model/frost"
        return (1..12).associateWith { month ->
            MonthlyWeatherData(
                temperature = temperatureData[month]?.averageOrNull(),
                snowDepth = snowData[month]?.averageOrNull(),
                cloudCoverage = cloudData[month]?.averageOrNull()
            )
        }
    }
}
