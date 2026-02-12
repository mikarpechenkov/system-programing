package ru.mi.mi_spring_ai.model.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class  BitcoinRateRs(
    var price: String? = null,
    var timestamp: Long? = null,
    @field:JsonProperty("24h_price_change")
    var dayPriceChange: String? = null,
    @field:JsonProperty("24h_price_change_percent")
    var dayPriceChangePercent: String? = null,
    @field:JsonProperty("24h_high")
    var dayHigh: String? = null,
    @field:JsonProperty("24h_low")
    var dayLow: String? = null,
    @field:JsonProperty("24h_volume")
    var dayVolume: String? = null
)
