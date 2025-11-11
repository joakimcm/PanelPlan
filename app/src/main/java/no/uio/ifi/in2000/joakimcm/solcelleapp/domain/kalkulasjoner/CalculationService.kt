package no.uio.ifi.in2000.joakimcm.solcelleapp.domain.kalkulasjoner

import no.uio.ifi.in2000.joakimcm.solcelleapp.data.PVGIS.PVGISRepository
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.frost.FrostRepository
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle.Direction
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost.MonthlyWeatherData
import javax.inject.Inject

/*
We use both PVGIS and Frost API to make our calculations. I therefore decided to make a class that takes data from each repository.
This class doesn't do anything other than collect the required data (from APIs and user) and send it to the calculation-function
and map the results.
 */
class CalculationService @Inject constructor(
    private val frostRepo: FrostRepository,
    private val pvgisRepo: PVGISRepository,
) {

    // These variables are only to retrieve data AFTER we've made the calls to the API.
    // Because we combine them in this class, we need a way to retrieve them individually to show
    // some of the information in PresentationScreen.
    // This is then our solution:
    private var observations: Map<Int, MonthlyWeatherData> = emptyMap()
    private var radiation: Map<Int, Double> = emptyMap()

    fun getFrostDataFromRepo(): Map<Int, MonthlyWeatherData> {
        return observations
    }

    fun getPvgisDataFromRepo(): Map<Int, Double> {
        return radiation
    }

    suspend fun getMonthlyEnergyEstimate(
        lon: String,
        lat: String,
        angle: Double?,
        areal: Double,
        direction: Direction,
    ): Map<Int, Double> {
        // Here, we retrieve the data from the APIs.
        radiation = pvgisRepo.getMonthlyRadiationForAddress(lon, lat, angle)
        observations = frostRepo.getObservationFromLocation(lon, lat)

        return (1..12).associateWith { month ->
            val influx = radiation[month] ?: 0.0
            val weather = observations[month] ?: MonthlyWeatherData(null, null, null)

            val temp = weather.temperature ?: 20.0
            val snow = weather.snowDepth ?: 0.0
            val cloud = weather.cloudCoverage ?: 0.0

            val directionNumber = when (direction) {
                Direction.NORTH -> 0.5
                Direction.EAST -> 0.8
                Direction.WEST -> 0.8
                Direction.SOUTH -> 1.0
                Direction.SOUTHWEST -> 0.9
                Direction.SOUTHEAST -> 0.9
                Direction.NORTHEAST -> 0.65
                Direction.NORTHWEST -> 0.65
            }

            calculateEnergy(
                influx = influx,
                cloudCoverage = cloud,
                snowCoverage = snow,
                airTemperature = temp,
                panelSize = areal,
                direction = directionNumber,
                panelEfficiency = 0.25
            )
        }
    }
}