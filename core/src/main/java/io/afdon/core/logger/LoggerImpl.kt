package io.afdon.core.logger

import io.afdon.core.BuildConfig
import java.lang.Exception
import javax.inject.Inject

class LoggerImpl @Inject constructor() : Logger {

    override fun log(e: Exception) {
        if (BuildConfig.DEBUG) e.printStackTrace()
    }
}