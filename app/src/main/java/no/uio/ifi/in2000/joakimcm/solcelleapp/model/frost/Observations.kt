package no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost

import kotlinx.serialization.Serializable

// Objects from getObservations API-call

@Serializable
data class FrostApiResponse(
    val apiVersion: String,
    val license: String,
    val createdAt: String,
    val queryTime: Double,
    val currentItemCount: Int,
    val itemsPerPage: Int,
    val offset: Int,
    val totalItemCount: Int,
    val currentLink: String,
    val data: List<ObservationData>,
)

@Serializable
data class ObservationData(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<Observation>, // List of all observations
)

@Serializable
data class Observation(
    val elementId: String,
    val value: Double, // We're very interested in this one, it's the value of the given observation
    val unit: String,
    val level: Level? = null,
    val timeOffset: String,
    val timeResolution: String,
    val timeSeriesId: Int,
    val performanceCategory: String,
    val exposureCategory: String,
)

@Serializable
data class Level(
    val levelType: String,
    val unit: String,
    val value: Int,
)
