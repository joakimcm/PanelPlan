package no.uio.ifi.in2000.joakimcm.solcelleapp.model.favorites

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle.Direction

@Entity(tableName = "favorite")
data class Favorite(
    @SerialName("address")
    val address: String,
    @SerialName("lon")
    val lon: String,
    @SerialName("lat")
    val lat: String,
    @SerialName("angle")
    val angle: Double?,
    @SerialName("areal")
    val panelSize: Double?,
    @SerialName("direction")
    val direction: Direction,
) {
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    var id: Int? = null
}

