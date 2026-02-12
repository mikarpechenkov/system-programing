package ru.mi.spring.app.service

import ru.mi.spring.app.model.dto.BitcoinRateRs

interface BitcoinRateService {

    fun getBitcoinRate(): BitcoinRateRs?
}
