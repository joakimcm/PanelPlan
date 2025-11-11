package no.uio.ifi.in2000.joakimcm.solcelleapp.data.geonorge

import no.uio.ifi.in2000.joakimcm.solcelleapp.model.geonorge.Address
import javax.inject.Inject
import javax.inject.Singleton

// Returns a list of Address-objects to retrieve the coordinates from address name.
@Singleton
class GeoDataRepository @Inject constructor(private val dataSource: GeoDataDataSource) {
    suspend fun getCoordinates(address: String): List<Address> {
        val response = dataSource.getCoordinates(address)
        if (response.adresser.isEmpty()) {
            throw Exception("Fant ingen adresse for: \"$address\".")
        }
        return response.adresser
    }
}