package ru.mi.spring.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["ru.mi.spring.app"])
class MiSpringApplication

fun main(args: Array<String>) {
    runApplication<MiSpringApplication>(*args)
}
