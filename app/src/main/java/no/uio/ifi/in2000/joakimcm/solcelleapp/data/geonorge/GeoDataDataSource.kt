package no.uio.ifi.in2000.joakimcm.solcelleapp.data.geonorge

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.geonorge.AddressResponse
import javax.inject.Inject
import javax.inject.Singleton

/*
Retrieves coordinates from address-input
Makes a search for best match in API. Gegit ts address-name and coordinates
(among other stuff, see package "model/geonorge"
 */

@Singleton
open class GeoDataDataSource @Inject constructor(
    private val client: HttpClient,
) {

    suspend fun getCoordinates(address: String): AddressResponse {
        val response = client.get("https://ws.geonorge.no/adresser/v1/sok?") {
            url {
                parameters.append("sok", address)
                parameters.append("fuzzy", "true")
                parameters.append(
                    "filtrer",
                    "adresser.adressetekst,adresser.postnummer,adresser.poststed,adresser.representasjonspunkt"
                )
                parameters.append("utkoordsys", "4258")
                parameters.append("treffPerSide", "5")
                parameters.append("asciiKompatibel", "true")
            }
        }
        return response.body()
    }
}