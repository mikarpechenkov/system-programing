package ru.mi.mi_spring_ai.exceptions

import java.lang.RuntimeException

class BitcoinRateHttpException(message: String?, cause: Throwable? = null) :
    RuntimeException(message, cause)