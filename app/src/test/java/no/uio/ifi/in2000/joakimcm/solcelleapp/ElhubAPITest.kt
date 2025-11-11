package no.uio.ifi.in2000.joakimcm.solcelleapp


import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.elhub.ElhubDataSource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

import org.junit.Test


class ElhubAPITest {
    @Test
    fun getHourlyConsumption() {
        val server = MockWebServer()
        server.start()

        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
            {
                "data": [
                        {
                          "attributes": {
                            "consumptionPerGroupMbaHour": [
                              {
                                "consumptionGroup": "household",
                                "endTime": "2023-09-01T01:00:00+02:00",
                                "lastUpdatedTime": "2025-03-30T11:40:40+02:00",
                                "meteringPointCount": 1091302,
                                "priceArea": "NO1",
                                "quantityKwh": 1000215.7,
                                "startTime": "2023-09-01T00:00:00+02:00"
                              }
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()
            )
        )

        val baseUrl = server.url("/").toString()

        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest {
                baseUrl
            }
        }

        val elhubDataSource = ElhubDataSource(client)

        val result = runBlocking {
            runCatching {
                elhubDataSource.getMonthlyConsumptionPriceArea(
                    startDate = "2023-09-01T00:00:00+02:00",
                    endDate = "2023-09-01T01:00:00+02:00"

                )
            }
        }
        assertTrue(result.isSuccess)

        server.shutdown()

    }
}