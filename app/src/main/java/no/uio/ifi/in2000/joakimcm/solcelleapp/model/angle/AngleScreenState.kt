package no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle

data class AngleScreenState(
    var height: String,
    var width: String,
    var panelSize: String,
    var angle: Double?,
    var direction: Direction,
)