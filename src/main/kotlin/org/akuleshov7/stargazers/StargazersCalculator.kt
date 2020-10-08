package org.akuleshov7.stargazers

import org.akuleshov7.api.StargazersJson
import org.akuleshov7.api.stargazers
import org.akuleshov7.utils.HttpClientFactory
import org.akuleshov7.utils.logError

class StargazersCalculator(private val repos: String?, private val extended: Boolean, val config: String?) {
    lateinit var uniqueStargazers: List<String>
    lateinit var duplicatedStargazers: Map<StargazersJson, Int>

    suspend fun calculateStargazers() {
        val repositoriesList: List<String>? = (readConfig() ?: repos)
            ?.split(',')
            ?.map { it.stargazers() }

        if (repositoriesList != null) {
            val allStargazers = HttpClientFactory(repositoriesList)
                .asyncRequests<Array<StargazersJson>>()
                // flatten list<array> with stargazers
                .flatMap { array ->
                    mutableListOf<StargazersJson>()
                        .also {
                            it.addAll(array)
                        }
                }
            val groupedStargazers = allStargazers.groupingBy { it }.eachCount()
            uniqueStargazers = groupedStargazers.keys.map { it.login }
            duplicatedStargazers = groupedStargazers.filter { it.value > 1 }
        } else {
            logError(
                "List of repositories was not provided. It can be provided in the configuration file or with a '-r' option"
            )
        }
    }

    private fun readConfig(): String? {
        // FixMe: add config reader
        return null
    }
}
