package no.uio.ifi.in2000.joakimcm.solcelleapp.domain.kalkulasjoner

fun calculateEnergy(
    influx: Double,        // Irradiation(kWh/m²)
    cloudCoverage: Double,      // Cloud coverage (octas, 0–8)
    snowCoverage: Double,      // Snow coverage (cm)
    airTemperature: Double,      // Air temperature (°C)
    panelSize: Double,    // Panel-size (m²)
    direction: Double,       // Direction the panel points to
    panelEfficiency: Double, // Panel efficiency (decimal, e.g. 0.2 for 20%)

): Double {
    // **Step 1: Adjust for cloud coverage**
    /*
     Solar irradiation measurements inherently account for cloud cover,
    since the sensors are located on the ground and record the sunlight that actually reaches the surface.
    In other words, any shading caused by clouds is already reflected in the measured values.

    I've commented out the cloud coverage calculations to show that the data is retrieved,
    but intentionally not used — including it would distort the final results.
     */

    //val cloudFactor = 1 - (cloudCoverage / 8.0)
    val cloudFactorFake = 1
    val effectCloud = influx * cloudFactorFake

    // **Step 2: Adjust for snow coverage**
    val criticalThickness =
        20.0 // Is high for some buffer in for example march (I don't want it to become 0)
    val snowFactor = 1 - (snowCoverage / criticalThickness).coerceIn(0.0, 1.0)
    val effectSnow = effectCloud * snowFactor

    // **Step 3: Adjust for temperature**
    val tempKo = 0.004 // -0.4% effect for each degree over 25 degrees
    val referenceTemp = 25.0
    val panelTemp = airTemperature + 20 // Estimated panel temperature
    val tempFactor = (1 - tempKo * (panelTemp - referenceTemp))

    // **Step 4: Calculate effect out**
    val effectOut = effectSnow * panelSize * panelEfficiency * tempFactor

    return effectOut * direction // Adjust for direction
}