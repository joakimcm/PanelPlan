package no.uio.ifi.in2000.joakimcm.solcelleapp.data.PVGIS


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.PVGIS.ApiResponse
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.PVGIS.Outputs
import javax.inject.Inject
import javax.inject.Singleton

/*
The program fetches monthly averages for solar radiation from PVGIS API. We don't use the calculator for estimating
power production, but instead get raw data about solar irradiation.
The API is very easy, only input location and the parameters we want.
We chose to include angle, since the math behind it would otherwise be very difficult.
 */
@Singleton
class PVGISDataSource @Inject constructor(
    private val client: HttpClient,
) {

    suspend fun getMonthlyRadiation(lat: String, lon: String, angle: Double?): Outputs {
        val response: ApiResponse = client.get("https://re.jrc.ec.europa.eu/api/v5_3/MRcalc?") {
            url {
                parameters.append("lat", lat)
                parameters.append("lon", lon)
                parameters.append("selectrad", "1")
                parameters.append("outputformat", "json")
                parameters.append("angle", angle.toString())
            }
        }.body()


        return response.outputs
    }
}