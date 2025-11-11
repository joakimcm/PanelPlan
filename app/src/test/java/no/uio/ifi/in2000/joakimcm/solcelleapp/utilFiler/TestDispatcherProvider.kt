package no.uio.ifi.in2000.joakimcm.solcelleapp.utilFiler

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.util.DispatcherProvider

// Fake dispatcher-klasse for å injecte dispatchers når jeg tester suspend funksjoner
class TestDispatcherProvider(
    testDispatcher: CoroutineDispatcher = StandardTestDispatcher(),
) : DispatcherProvider {
    override val main = testDispatcher
    override val io = testDispatcher
}

