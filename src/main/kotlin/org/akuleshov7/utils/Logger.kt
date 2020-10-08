package org.akuleshov7.utils

import kotlin.system.exitProcess

fun logError(msg: String) {
    System.err.println("[ERROR] $msg")
}

fun logInfo(msg: String) {
    println("[INFO] $msg")
}

infix fun String.logAndExit(exitStatus: Int): Nothing {
    logError(this)
    exitProcess(exitStatus)
}
