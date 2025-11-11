package no.uio.ifi.in2000.joakimcm.solcelleapp.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// Interface and class for what dispatcher I want available for the suspend functions
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override val main = Dispatchers.Main
    override val io = Dispatchers.IO
}