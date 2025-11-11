package no.uio.ifi.in2000.joakimcm.solcelleapp.data.strom

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.power.PowerPrices
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PowerDataSource @Inject constructor(
    private val client: HttpClient,
) {
    private fun makeURL(date: LocalDate): String {
        val year = date.year
        val monthDay = date.format(DateTimeFormatter.ofPattern("MM-dd"))
        return "https://www.hvakosterstrommen.no/api/v1/prices/$year/${monthDay}_NO1.json"
    }

    suspend fun getPowerPrice(dateString: LocalDate): List<PowerPrices> {
        val url = makeURL(dateString)
        val response: HttpResponse = client.get(url)
        return response.body()
    }


}