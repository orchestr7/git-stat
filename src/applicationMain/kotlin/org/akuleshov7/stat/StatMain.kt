/**
 * Main logic for launching and CLI arguments parsing
 */

package org.akuleshov7.stat

import org.akuleshov7.stargazers.StargazersCalculator
import org.akuleshov7.stat.StatType.STARGAZERS
import org.akuleshov7.utils.Config
import org.akuleshov7.utils.logInfo

import kotlinx.coroutines.*
import org.akuleshov7.utils.logAndExit

const val PROGRAM_NAME = "git-stat"
var isDebug = false

@Suppress("TOO_LONG_FUNCTION")
fun main(args: Array<String>) =
    runBlocking {
        // Configuration file and all cli-arguments are parsed here:
        val config = Config.create(args)
        // Controller for the global debug logging
        isDebug = config.isDebug

        when (config.statType) {
            STARGAZERS -> {
                val repositories = Repositories(config).requestAllRepos()
                logInfo("getting statistics for the following repositories: $repositories")
                StargazersCalculator(repositories, config)
                        .calculateStargazers()
                        .log()
            }
            else -> "Type $config.type that you have provided with 'type' option is not supported yet" logAndExit 1
        }
    }
