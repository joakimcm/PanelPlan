package no.uio.ifi.in2000.joakimcm.solcelleapp.model.elhub

// Included due to the TDD-related test from the team activity.
// Not implemented in the UI layer due to time constraints and other factors.
// Aware it's not ideal to include this in main before full implementation.
import kotlinx.serialization.Serializable
@Serializable
data class ElhubRespons(
    val data: List<AreaConsumptionData>
)

@Serializable
data class AreaConsumptionData(
    val attributes: ConsumptionAttributes
)

@Serializable
data class ConsumptionAttributes(
    val consumptionPerGroupMbaHour: List<ConsumptionMeasurement>?
)

@Serializable
data class ConsumptionMeasurement(
    val startTime: String,
    val endTime: String,
    val quantityKwh: Double,
    val consumptionGroup: String
)