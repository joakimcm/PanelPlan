package no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost

import kotlinx.serialization.Serializable

// Objects for getLocation in Frost API

@Serializable
data class LocationResponse(
    val apiVersion: String,
    val license: String,
    val createdAt: String,
    val queryTime: Double,
    val currentItemCount: Int,
    val itemsPerPage: Int,
    val offset: Int,
    val totalItemCount: Int,
    val currentLink: String,
    val data: List<SensorSystem>,
)

@Serializable
data class SensorSystem(
    val id: String, // We're interested in this one. To get the correct weather station
    val name: String,
    val shortName: String,
    val country: String,
    val countryCode: String,
    val geometry: Geometry,
    val distance: Double,
    val validFrom: String,
    val county: String,
    val countyId: Int,
    val municipality: String,
    val municipalityId: Int,
    val stationHolders: List<String>,
)

@Serializable
data class Geometry(
    val coordinates: List<Double>,
    val nearest: Boolean,
)
