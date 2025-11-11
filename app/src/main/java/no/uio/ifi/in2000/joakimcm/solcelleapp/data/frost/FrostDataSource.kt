package no.uio.ifi.in2000.joakimcm.solcelleapp.data.frost

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost.FrostApiResponse
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost.LocationResponse
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost.WeatherElements
import javax.inject.Inject
import javax.inject.Singleton

/*
The program first calls getLocation to find the nearest weather station to the given coordinates.
It then fetches weather data from getObservations using that station.

Since Frost data can be sparse, the query allows data up to 80 years old (except for temperature) to reduce errors.
While not ideal, the "latest" keyword ensures the most recent available data is used, even if it's old.

Monthly averages are calculated from up to the 300 most recent observations.
 */

@Singleton
class FrostDataSource @Inject constructor(
    private val client: HttpClient,
) {

    suspend fun getObservations(source: String, element: WeatherElements): FrostApiResponse? {
        // Authentication (Safe to include on GitHub) 
        val apiKey = "41890636-49ea-40dd-99f0-b9071789be6c:"
        val credentials = "$apiKey:" // Frost uses username, no password.
        val encodedCredentials = android.util.Base64.encodeToString(
            credentials.toByteArray(), android.util.Base64.NO_WRAP
        )

        // Adds authentication header.
        val response = client.get("https://frost.met.no/observations/v0.jsonld?") {
            headers {
                append("Authorization", "Basic $encodedCredentials")
                append("Accept", "application/json")
            }
            // Construct URL with the necessary parameters
            url {
                parameters.append("sources", source)
                parameters.append(
                    "referencetime", "latest"
                )  // Finds latest data (ups, can be old)
                parameters.append(
                    "maxage", "P80Y"
                )
                parameters.append("limit", "300")
                parameters.append("elements", element.queryString)
            }

        }
        return response.body()
    }


    suspend fun getLocation(lon: String, lat: String): LocationResponse? {
        // Authentication
        val apiKey = "41890636-49ea-40dd-99f0-b9071789be6c:"
        val credentials = "$apiKey:"
        val encodedCredentials = android.util.Base64.encodeToString(
            credentials.toByteArray(), android.util.Base64.NO_WRAP
        )
        val response = client.get("https://frost.met.no/sources/v0.jsonld?") {
            headers {
                append("Authorization", "Basic $encodedCredentials")
                append("Accept", "application/json")
            }
            url {
                parameters.append("elements",
                    WeatherElements.entries.joinToString(",") { it.queryString })
                parameters.append("geometry", "nearest(POINT($lon $lat))")
            }
        }
        return response.body()
    }


}
