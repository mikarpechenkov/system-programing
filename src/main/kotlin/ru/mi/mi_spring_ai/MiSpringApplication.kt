package ru.mi.mi_spring_ai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["ru.mi.mi_spring_ai"])
class MiSpringApplication

fun main(args: Array<String>) {
	runApplication<MiSpringApplication>(*args)
}
