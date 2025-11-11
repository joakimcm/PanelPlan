package no.uio.ifi.in2000.joakimcm.solcelleapp.data.elhub

import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.elhub.ConsumptionMeasurement
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.elhub.ElhubRespons
import javax.inject.Inject
import javax.inject.Singleton

// Included due to the TDD-related test from the team activity.
// Not implemented in the UI layer due to time constraints and other factors.
// Aware it's not ideal to include this in main before full implementation.
@Singleton
open class ElhubDataSource @Inject constructor(
    private val client: HttpClient
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getMonthlyConsumptionPriceArea(
        startDate: String,
        endDate: String,
    ): List<ConsumptionMeasurement> {

        return try {
            val response: HttpResponse = client.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.tadata.no"
                    encodedPath = "/energy-data/v0/price-areas/NO1"
                    parameters.append("dataset", "CONSUMPTION_PER_GROUP_MBA_HOUR")
                    parameters.append("startDate", startDate)
                    parameters.append("endDate", endDate)
                    parameters.append("consumptionGroup", "household")
                }
                headers {
                    append("Tadata-Api-Key", "fdba699b-c77e-424e-9c83-7ac74097c6ad")
                    append("Tadata-Client-Id", "dekabulhan@gmail.com")
                    append("Tadata-Forward-To", "elhub")

                }
            }
            val responseBody = response.bodyAsText()
            //println("Response body: $responseBody")

            if (response.status.value != 200) {
                listOf()
            } else {
                val consumption: ElhubRespons = response.body()
                consumption.data.flatMap { it.attributes.consumptionPerGroupMbaHour ?: listOf() }
            }

        } catch (e: Exception) {
            listOf()
        }
    }
}