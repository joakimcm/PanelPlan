package no.uio.ifi.in2000.joakimcm.solcelleapp.domain.kalkulasjoner

import kotlin.math.atan

// Calculate roof angle
fun roofAngle(h: Double, b: Double): Double {
    return Math.toDegrees(atan(h / b)) // Convert from radians to degrees
}
