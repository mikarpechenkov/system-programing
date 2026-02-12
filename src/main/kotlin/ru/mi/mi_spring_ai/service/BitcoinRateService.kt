package ru.mi.mi_spring_ai.service

import ru.mi.mi_spring_ai.model.dto.BitcoinRateRs

interface BitcoinRateService {

    fun getBitcoinRate(): BitcoinRateRs?
}