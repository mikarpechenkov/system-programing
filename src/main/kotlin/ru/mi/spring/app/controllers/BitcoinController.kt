package ru.mi.spring.app.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import ru.mi.spring.app.service.BitcoinRateService


@RestController
class BitcoinController(
    private val bitcoinRateService: BitcoinRateService
) {

    @GetMapping("bitcoin-rate")
    fun bitcoin(): String {
        val bitcoinRate = bitcoinRateService.getBitcoinRate()
        return when (bitcoinRate?.price) {
            null -> "Не удалось узнать курс биткоина"
            else -> "Курс биткоина на сегодня ${bitcoinRate.price}$"
        }
    }
}
