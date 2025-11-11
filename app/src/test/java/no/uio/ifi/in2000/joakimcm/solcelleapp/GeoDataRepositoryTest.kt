import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.geonorge.GeoDataDataSource
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.geonorge.GeoDataRepository
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.geonorge.Address
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.geonorge.AddressResponse
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.geonorge.Representasjonspunkt
import org.junit.Test


class GeoDataRepositoryTest {
    private val fakeDataSource = mockk<GeoDataDataSource>()
    private var repository = GeoDataRepository(fakeDataSource)

    @Test
    fun getCoordinates_returns_list_when_response_is_valid() = runTest {
        // Arrange
        val expected = listOf(
            Address(
                adressetekst = "Karl Johans gate 1",
                postnummer = "0154",
                poststed = "Oslo",
                representasjonspunkt = Representasjonspunkt("EPSG:4258", 59.91, 10.75)
            )
        )

        val response = AddressResponse(adresser = expected)
        coEvery { fakeDataSource.getCoordinates("Oslo") } returns response

        // Act
        val result = repository.getCoordinates("Oslo")

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun getCoordinates_throws_exception_when_response_is_empty() = runTest {
        // Arrange
        val response = AddressResponse(adresser = emptyList())
        coEvery { fakeDataSource.getCoordinates("Ingensteds") } returns response

        // Avt and Assert
        try {
            repository.getCoordinates("Ingensteds")
            // Test fail if exception is not thrown
            assert(false)
        } catch (e: Exception) {
            // Asserting if error text is correct
            assertEquals("Fant ingen adresse for: \"Ingensteds\".", e.message)
        }
    }

    @Test
    fun getCoordinates_throws_exception_when_API_request_fails() = runTest {
        // Arrange
        val exception = Exception("Network error")
        coEvery { fakeDataSource.getCoordinates("Oslo") } throws exception

        // Act and assert
        try {
            repository.getCoordinates("Oslo")
            // Fail if exception is not thrown
            assert(false)
        } catch (e: Exception) {
            // Check iff correct exception is thrown
            assertEquals(exception, e)
        }
    }
}