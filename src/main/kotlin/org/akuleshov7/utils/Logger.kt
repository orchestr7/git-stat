/**
 * Logging utilities
 */

package org.akuleshov7.utils

import org.akuleshov7.stat.isDebug

import kotlin.system.exitProcess

/**
 * Yes, it is yet another logger, because the quality and functionality of the current loggers is extremely poor
 */
fun logError(msg: String) {
    System.err.println("[ERROR] $msg (you can enable debug mode by --debug/-d option)")
}

fun logInfo(msg: String) {
    println("[INFO] $msg")
}

fun logDebug(msg: String) {
    if (isDebug) {
        println("[DEBUG] $msg")
    }
}

infix fun String.logAndExit(exitStatus: Int): Nothing {
    logError(this)
    exitProcess(exitStatus)
}
