package no.uio.ifi.in2000.joakimcm.solcelleapp.model.power

import kotlinx.serialization.Serializable

@Serializable
data class PowerPrices(
    val NOK_per_kWh: Double,
    val EUR_per_kWh: Double,
    val EXR: Double,
    val time_start: String,
    val time_end: String,
)