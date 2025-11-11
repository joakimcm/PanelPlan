package no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost

// Enum class for weather elements
enum class WeatherElements(val queryString: String) {
    AIR_TEMPERATURE("mean(air_temperature P1M)"),
    CLOUD_COVERAGE("mean(cloud_area_fraction P1M)"),
    SNOW_DEPTH("mean(surface_snow_thickness P1M)");

    // Want access to string associated to each object.
    override fun toString(): String = queryString
}
