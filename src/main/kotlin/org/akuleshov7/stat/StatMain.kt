package org.akuleshov7.stat

import org.akuleshov7.stat.StatType.*
import org.akuleshov7.stargazers.StargazersCalculator
import kotlinx.cli.*
import kotlinx.coroutines.*
import org.akuleshov7.utils.logInfo

const val PROGRAM_NAME = "stats"

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
            description = "List of repositories, separated by comma, like: cqfn/diKTat, yegor256/tacit, e.t.c"
        )

        parser.parse(args)

        when (type) {
            STARGAZERS -> {
                logInfo("getting statistics for the following repositories: $repos")
                val stargazers = StargazersCalculator(repos, extended, configPath)
                stargazers.calculateStargazers()
                println(stargazers.uniqueStargazers)
                println(stargazers.uniqueStargazers.size)
            }
        }
    }
