package ru.mi.mi_spring_ai.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.core.env.Environment
import ru.mi.mi_spring_ai.exceptions.BitcoinRateHttpException
import ru.mi.mi_spring_ai.model.dto.BitcoinRateRs
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BitcoinRateHttpServiceTest {

    @Mock
    private lateinit var env: Environment

    @Mock
    private lateinit var mapper: ObjectMapper

    @Mock
    private lateinit var httpClient: HttpClient

    @Mock
    private lateinit var httpResponse: HttpResponse<String>

    private lateinit var service: BitcoinRateHttpService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        service = BitcoinRateHttpService(env, mapper, httpClient)

        `when`(env.getRequiredProperty(BitcoinRateHttpService.API_KEY_PROPERTY))
            .thenReturn(API_KEY)
    }

    @Test
    fun `getBitcoinRate should successfully return rate when API responds correctly`() {
        // Given
        val responseFuture = CompletableFuture.completedFuture(httpResponse)
        `when`(
            httpClient.sendAsync(
                any(HttpRequest::class.java),
                any(HttpResponse.BodyHandler::class.java)
            )
        ).thenReturn(responseFuture)

        `when`(httpResponse.body()).thenReturn(responseBody)
        `when`(mapper.readValue(responseBody, BitcoinRateRs::class.java))
            .thenReturn(bitcoinRateRs)

        // When
        val result = service.getBitcoinRate()

        // Then
        assertEquals(bitcoinRateRs, result)
        verify(httpClient).sendAsync(
            any(HttpRequest::class.java),
            any(HttpResponse.BodyHandler::class.java)
        )
    }

    @Test
    fun `getBitcoinRate should throw exception when response cannot be parsed`() {
        // Given
        val responseFuture = CompletableFuture.completedFuture(httpResponse)
        `when`(
            httpClient.sendAsync(
                any(HttpRequest::class.java),
                any(HttpResponse.BodyHandler::class.java)
            )
        ).thenReturn(responseFuture)

        `when`(httpResponse.body()).thenReturn("invalid json")
        `when`(mapper.readValue("invalid json", BitcoinRateRs::class.java))
            .thenThrow(RuntimeException("Parse error"))

        // When & Then
        val exception = assertThrows<BitcoinRateHttpException> {
            service.getBitcoinRate()
        }

        assertEquals("Невозможно распарсить тело ответа invalid json", exception.message)
    }

    @Test
    fun `getBitcoinRate should return null when ExecutionException occurs`() {
        // Given
        val responseFuture = CompletableFuture.failedFuture<HttpResponse<String>>(
            ExecutionException("Connection error", Throwable())
        )
        `when`(
            httpClient.sendAsync(
                any(HttpRequest::class.java),
                any(HttpResponse.BodyHandler::class.java)
            )
        ).thenReturn(responseFuture)

        // When
        val result = service.getBitcoinRate()

        // Then
        assertNull(result)
    }

    @Test
    fun `getBitcoinRate should return null when ExecutionException wraps other exception`() {
        // Given
        val responseFuture = CompletableFuture.failedFuture<HttpResponse<String>>(
            ExecutionException("Timeout", RuntimeException("Connection timeout"))
        )
        `when`(
            httpClient.sendAsync(
                any(HttpRequest::class.java),
                any(HttpResponse.BodyHandler::class.java)
            )
        ).thenReturn(responseFuture)

        // When
        val result = service.getBitcoinRate()

        // Then
        assertNull(result)
    }

    @Test
    fun `getBitcoinRate should build request with correct parameters`() {
        // Given
        val responseFuture = CompletableFuture.completedFuture(httpResponse)
        `when`(
            httpClient.sendAsync(
                any(HttpRequest::class.java),
                any(HttpResponse.BodyHandler::class.java)
            )
        ).thenAnswer { invocation ->
            val request = invocation.getArgument<HttpRequest>(0)

            // Verify request parameters
            assertEquals(
                "${BitcoinRateHttpService.HOST}${BitcoinRateHttpService.BITCOIN_PATH}",
                request.uri().toString()
            )
            assertEquals("GET", request.method())
            assertEquals(API_KEY, request.headers().firstValue("X-Api-Key").get())

            responseFuture
        }

        `when`(httpResponse.body()).thenReturn(responseBody)
        `when`(mapper.readValue(responseBody, BitcoinRateRs::class.java))
            .thenReturn(bitcoinRateRs)

        // When
        service.getBitcoinRate()

        // Then
        verify(env).getRequiredProperty(BitcoinRateHttpService.API_KEY_PROPERTY)
    }

    @Test
    fun `getBitcoinRate should use ObjectMapper for deserialization`() {
        // Given
        val responseFuture = CompletableFuture.completedFuture(httpResponse)
        `when`(
            httpClient.sendAsync(
                any(HttpRequest::class.java),
                any(HttpResponse.BodyHandler::class.java)
            )
        ).thenReturn(responseFuture)

        `when`(httpResponse.body()).thenReturn(responseBody)
        `when`(mapper.readValue(responseBody, BitcoinRateRs::class.java))
            .thenReturn(bitcoinRateRs)

        // When
        service.getBitcoinRate()

        // Then
        verify(mapper).readValue(responseBody, BitcoinRateRs::class.java)
    }

    @Test
    fun `getBitcoinRate should handle null response from ObjectMapper`() {
        // Given
        val responseFuture = CompletableFuture.completedFuture(httpResponse)
        `when`(
            httpClient.sendAsync(
                any(HttpRequest::class.java),
                any(HttpResponse.BodyHandler::class.java)
            )
        ).thenReturn(responseFuture)

        `when`(httpResponse.body()).thenReturn(responseBody)
        `when`(mapper.readValue(responseBody, BitcoinRateRs::class.java))
            .thenReturn(null)

        // When & Then
        val exception = assertThrows<BitcoinRateHttpException> {
            service.getBitcoinRate()
        }

        assertEquals("Невозможно распарсить тело ответа $responseBody", exception.message)
    }

    @Test
    fun `getBitcoinRate should handle interrupted exception wrapped in ExecutionException`() {
        // Given
        val responseFuture = CompletableFuture.failedFuture<HttpResponse<String>>(
            ExecutionException("Interrupted", InterruptedException("Thread was interrupted"))
        )
        `when`(
            httpClient.sendAsync(
                any(HttpRequest::class.java),
                any(HttpResponse.BodyHandler::class.java)
            )
        ).thenReturn(responseFuture)

        // When
        val result = service.getBitcoinRate()

        // Then
        assertNull(result)
    }

    // Helper function for mocking HttpRequest
    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    companion object {
        private const val API_KEY = "test-api-key-123"
        private val responseBody =
            """
            {
              "price": "94962.21000000",
              "timestamp": 1736824504,
              "24h_price_change": "849.92000000",
              "24h_price_change_percent": "0.903",
              "24h_high": "95222.00000000",
              "24h_low": "89438.45000000",
              "24h_volume": "26.39660000"
            }
        """.trimIndent()
        private val bitcoinRateRs = BitcoinRateRs(
            price = "94962.21000000",
            timestamp = 1736824504,
            dayPriceChange = "849.92000000",
            dayPriceChangePercent = "0.903",
            dayHigh = "95222.00000000",
            dayLow = "89438.45000000",
            dayVolume = "26.39660000"
        )
    }
}