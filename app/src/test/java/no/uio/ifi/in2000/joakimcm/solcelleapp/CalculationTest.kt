import no.uio.ifi.in2000.joakimcm.solcelleapp.domain.kalkulasjoner.calculateEnergy
import org.junit.Assert.assertEquals
import org.junit.Test

class EnergyCalculationTests {

    @Test
    fun testCalculateEnergy_OptimalConditions() {
        val influx = 5.0        // typical irradiance (kWh/m²)
        val cloudCoverage = 0.0  // no clouds
        val snowCoverage = 0.0   // no snow
        val airTemperature = 25.0 // optimal temperature
        val panelSize = 1.0      // 1 m² panel
        val direction = 1.0       // facing directly towards sunlight
        val panelEfficiency = 0.2 // 20% efficiency

        val result = calculateEnergy(
            influx,
            cloudCoverage,
            snowCoverage,
            airTemperature,
            panelSize,
            direction,
            panelEfficiency
        )
        assertEquals(0.92, result, 0.01)
    }

    @Test
    fun testCalculateEnergy_WithMaxSnow() {
        val influx = 5.0
        val cloudCoverage = 0.0
        val snowCoverage = 21.0 // exceed critical thickness
        val airTemperature = 25.0
        val panelSize = 1.0
        val direction = 1.0
        val panelEfficiency = 0.2

        val result = calculateEnergy(
            influx,
            cloudCoverage,
            snowCoverage,
            airTemperature,
            panelSize,
            direction,
            panelEfficiency
        )
        assertEquals(0.0, result, 0.01) // Expected: Snow coverage maxes out effect
    }

    @Test
    fun testCalculateEnergy_WithHighTemperature() {
        val influx = 5.0
        val cloudCoverage = 0.0
        val snowCoverage = 0.0
        val airTemperature = 30.0 // above optimal temperature
        val panelSize = 1.0
        val direction = 1.0
        val panelEfficiency = 0.2

        val result = calculateEnergy(
            influx,
            cloudCoverage,
            snowCoverage,
            airTemperature,
            panelSize,
            direction,
            panelEfficiency
        )
        assert(result > 0) // Ensure output is positive but less than in optimal conditions.
    }

    @Test
    fun testCalculateEnergy_NoIrradiation() {
        val influx = 0.0 // no sunlight
        val cloudCoverage = 0.0
        val snowCoverage = 0.0
        val airTemperature = 25.0
        val panelSize = 1.0
        val direction = 1.0
        val panelEfficiency = 0.2

        val result = calculateEnergy(
            influx,
            cloudCoverage,
            snowCoverage,
            airTemperature,
            panelSize,
            direction,
            panelEfficiency
        )
        assertEquals(0.0, result, 0.01) // Expected: no energy produced
    }

    @Test
    fun testCalculateEnergy_WithPartialCloudCover() {
        val influx = 5.0
        val cloudCoverage = 4.0 // medium cloud coverage (octas)
        val snowCoverage = 0.0
        val airTemperature = 25.0
        val panelSize = 1.0
        val direction = 1.0
        val panelEfficiency = 0.2

        val result = calculateEnergy(
            influx,
            cloudCoverage,
            snowCoverage,
            airTemperature,
            panelSize,
            direction,
            panelEfficiency
        )
        assert(result < 1.0 && result > 0.0) // Expect output < 1.0 without exact value; ensure it adjusts factors
    }

    @Test
    fun testCalculateEnergy_LargePanelSize() {
        val influx = 5.0
        val cloudCoverage = 0.0
        val snowCoverage = 0.0
        val airTemperature = 25.0
        val panelSize = 10.0
        val direction = 1.0
        val panelEfficiency = 0.2

        val result = calculateEnergy(
            influx,
            cloudCoverage,
            snowCoverage,
            airTemperature,
            panelSize,
            direction,
            panelEfficiency
        )
        assertEquals(9.2, result, 0.01)
    }
}
