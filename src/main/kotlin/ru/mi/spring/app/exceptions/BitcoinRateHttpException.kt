package ru.mi.spring.app.exceptions

import java.lang.RuntimeException

class BitcoinRateHttpException(message: String?, cause: Throwable? = null) :
    RuntimeException(message, cause)
