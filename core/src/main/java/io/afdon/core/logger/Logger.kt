package io.afdon.core.logger

import java.lang.Exception

interface Logger {

    fun log(e: Exception)
}