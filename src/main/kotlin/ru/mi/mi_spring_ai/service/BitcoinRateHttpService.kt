package ru.mi.mi_spring_ai.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import ru.mi.mi_spring_ai.exceptions.BitcoinRateHttpException
import ru.mi.mi_spring_ai.model.dto.BitcoinRateRs
import ru.mi.mi_spring_ai.utils.debug
import ru.mi.mi_spring_ai.utils.error
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.ExecutionException

@Service
class BitcoinRateHttpService(
    private val env: Environment,
    private val mapper: ObjectMapper,
    private val httpClient: HttpClient
) : BitcoinRateService {

    override fun getBitcoinRate(): BitcoinRateRs? {
        val request = HttpRequest.newBuilder()
            .uri(URI(HOST + BITCOIN_PATH))
            .header(API_KEY_HEADER, env.getRequiredProperty(API_KEY_PROPERTY))
            .GET()
            .build()

        val responseFuture = httpClient.sendAsync(
            request,
            HttpResponse.BodyHandlers.ofString()
        )
        debug<BitcoinRateHttpService>("Отправен запрос: $request")
        return try {
            val response = responseFuture.get()
            debug<BitcoinRateHttpService>("Успешно получили ответ")
            runCatching { mapper.readValue(response.body(), BitcoinRateRs::class.java) }
                .onSuccess { debug<BitcoinRateHttpService>("Успешно распарсили ответ") }
                .getOrNull() ?: throw BitcoinRateHttpException("Невозможно распарсить тело ответа ${response.body()}")
        } catch (e: ExecutionException) {
            error<BitcoinRateHttpService>(e) { "Ошибка при отправке запроса" }
            null
        }
    }

    companion object {
        const val HOST = "https://api.api-ninjas.com"
        const val BITCOIN_PATH = "/v1/bitcoin"
        const val API_KEY_HEADER = "X-Api-Key"
        const val API_KEY_PROPERTY = "BITCOIN_API_KEY"
    }
}