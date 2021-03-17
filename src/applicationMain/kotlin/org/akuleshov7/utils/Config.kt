package org.akuleshov7.utils

import org.akuleshov7.stat.PROGRAM_NAME
import org.akuleshov7.stat.StatType

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

@Suppress("TOO_LONG_FUNCTION")
data class Config(
        val credentials: Credentials?,
        val repositories: String?,
        val organizations: String?,
        val isExtended: Boolean,
        val isDebug: Boolean,
        val statType: StatType,
        val configPath: String?,
) {
    companion object {
        fun create(args: Array<String>): Config {
            // ============ args parsing ===========

            val parser = ArgParser(PROGRAM_NAME)

            val type by parser.option(
                    ArgType.Choice<StatType>(),
                    shortName = "t",
                    description = "Type of statistics"
            ).required()

            val auth by parser.option(
                    ArgType.String,
                    shortName = "a",
                    description = """GitHub username and authorization token separated by ':'
                | (can be used to extend the number of requests)""".trimMargin()
            )

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

            val orgs by parser.option(
                    ArgType.String,
                    shortName = "o",
                    description = "List of organizations, separated by comma, like: \"cqfn, artipie\", e.t.c"
            )

            parser.parse(args)

            // ============ config creation ===========

            val credentials = auth?.let {
                Credentials(it)
            }

            return Config(
                    credentials,
                    repos,
                    orgs,
                    extended,
                    debug,
                    type,
                    configPath,
            )
        }
    }
}
