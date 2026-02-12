package ru.mi.spring.app.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
class ConfigurationService {

    @Bean
    fun defaultHttpClient(): HttpClient = HttpClient.newHttpClient()
}
