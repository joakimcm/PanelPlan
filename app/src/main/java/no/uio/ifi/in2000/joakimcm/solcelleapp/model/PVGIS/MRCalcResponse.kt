package no.uio.ifi.in2000.joakimcm.solcelleapp.model.PVGIS

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MonthlyData(
    val year: Int,
    val month: Int,
    @SerialName("H(i)_m") val irradiation: Double, // Another name for easier access
)

@Serializable
data class Outputs(
    val monthly: List<MonthlyData>,
)

@Serializable
data class ApiResponse(
    val outputs: Outputs,
)
