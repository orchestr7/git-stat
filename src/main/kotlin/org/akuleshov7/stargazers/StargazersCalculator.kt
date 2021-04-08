package org.akuleshov7.stargazers

import org.akuleshov7.api.StargazersJson
import org.akuleshov7.api.stargazersEndPoint
import org.akuleshov7.utils.Config
import org.akuleshov7.utils.HttpClientFactory
import org.akuleshov7.utils.logAndExit
import org.akuleshov7.utils.logInfo

class StargazersCalculator(private val repositories: Set<String>, val config: Config) {
    var numberOfDuplicatedStars: Int = 0
    lateinit var uniqueStargazers: List<String>
    lateinit var duplicatedStargazers: Map<StargazersJson, Int>

    suspend fun calculateStargazers(): StargazersCalculator {
        // creating correct urls from the list of repos
        val repositoriesList: Set<String> = repositories
                .map { it.stargazersEndPoint() }
                .toSet()

        // preparing correct urls to github from the list of repos
        if (repositoriesList.isNotEmpty()) {
            val allStargazers = HttpClientFactory(repositoriesList, config)
                    .requestAllData<Array<StargazersJson>>()
                    // flatten list<array> with stargazers
                    .flatMap { array ->
                        mutableListOf<StargazersJson>()
                                .also {
                                    it.addAll(array)
                                }
                    }

            // calculate number of stars and stargazers
            doCalculations(allStargazers)
        } else {
            "List of repositories was not provided. It can be provided with a '-r' or '-o' option" logAndExit 8
        }

        return this
    }

    fun log() {
        logInfo("Number of stargazers: ${uniqueStargazers.size}")
        logInfo("Number of duplicated stars: $numberOfDuplicatedStars")

        if (config.isExtended) {
            logInfo("Unique stargazers: ${uniqueStargazers.joinToString()}")
            logInfo(
                    "Duplicated stars: ${
                        duplicatedStargazers.map { "User:${it.key.login} Stars:${it.value}" }.joinToString("; ")
                    }",
            )
        }
    }

    private fun doCalculations(allStargazers: List<StargazersJson>) {
        val groupedStargazers = allStargazers.groupingBy { it }.eachCount()
        uniqueStargazers = groupedStargazers.keys.map { it.login }
        duplicatedStargazers = groupedStargazers.filter { it.value > 1 }
        numberOfDuplicatedStars = duplicatedStargazers.values.map { it - 1 }.sum()
    }
}
