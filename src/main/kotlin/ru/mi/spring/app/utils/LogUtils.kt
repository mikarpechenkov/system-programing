package ru.mi.spring.app.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> logger(): Logger = LoggerFactory.getLogger(T::class.java)

inline fun <reified T> debug(message: String) = logger<T>().debug(message)

inline fun <reified T> warning(vararg args: Any, messageSupplier: () -> String) =
    logger<T>().warn(messageSupplier(), *args)

inline fun <reified T> error(vararg args: Any, messageSupplier: () -> String) =
    logger<T>().error(messageSupplier(), *args)
