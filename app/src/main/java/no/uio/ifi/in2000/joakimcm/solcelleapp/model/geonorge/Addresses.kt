package no.uio.ifi.in2000.joakimcm.solcelleapp.model.geonorge


import kotlinx.serialization.Serializable

// Objects for getCoordinates

@Serializable
data class AddressResponse(
    val adresser: List<Address>,
)

@Serializable
data class Address(
    val adressetekst: String,
    val postnummer: String,
    val poststed: String,
    val representasjonspunkt: Representasjonspunkt,
)

@Serializable
data class Representasjonspunkt(
    val epsg: String,
    val lat: Double,
    val lon: Double,
)