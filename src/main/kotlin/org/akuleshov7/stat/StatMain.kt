package org.akuleshov7.stat

import org.akuleshov7.stat.StatType.*
import org.akuleshov7.stargazers.StargazersCalculator
import org.akuleshov7.api.StargazersJson
import org.akuleshov7.utils.HttpClientFactory
import org.akuleshov7.utils.logAndExit
import kotlinx.cli.*
import kotlinx.coroutines.*
import org.akuleshov7.utils.logInfo

const val PROGRAM_NAME = "git-stat"
var isDebug = false

fun main(args: Array<String>) =
    runBlocking<Unit> {
        val parser = ArgParser(PROGRAM_NAME)

        val type by parser.option(
            ArgType.Choice<StatType>(),
            shortName = "t",
            description = "Type of statistics"
        ).required()

        val configPath by parser.option(
            ArgType.String,
            shortName = "c",
            description = "Full path to a configuration file"
        )

        val extended by parser.option(
            ArgType.Boolean,
            shortName = "e",
            description = "Extended report with more statistics"
        ).default(false)

        val repos by parser.option(
            ArgType.String,
            shortName = "r",
            description = "List of repositories, separated by comma, like: \"cqfn/diKTat, yegor256/tacit\", e.t.c"
        )

        val debug by parser.option(
            ArgType.Boolean,
            shortName = "d",
            description = "Debug mode to see additional information about the program execution"
        ).default(false)

        val organizations by parser.option(
            ArgType.String,
            shortName = "o",
            description = "List of organizations, separated by comma, like: \"cqfn, artipie\", e.t.c"
        )

        parser.parse(args)
        isDebug = debug

        when (type) {
            STARGAZERS -> {
                val repositories = Repositories(repos, organizations, configPath).requestAllRepos()
                logInfo("getting statistics for the following repositories: $repos")
                StargazersCalculator(repositories, extended, configPath)
                    .calculateStargazers()
                    .log()
            }
            else -> {
                "Type $type is not supported yet" logAndExit 1
            }
        }
    }
