package org.akuleshov7.stat

import org.akuleshov7.api.ReposJson
import org.akuleshov7.api.reposEndPoint
import org.akuleshov7.utils.Config
import org.akuleshov7.utils.HttpClientFactory

class Repositories(val config: Config) {
    var repos: Set<String> = emptySet()
    var orgs: Set<String> = emptySet()

    init {
        repos = (config.repositories).splitAndTrim() ?: emptySet()
        orgs = (config.organizations).splitAndTrim() ?: emptySet()
    }

    suspend fun requestAllRepos(): Set<String> {
        val repositoriesList: Set<String> = orgs
                .map { it.reposEndPoint() }
                .toSet()

        val reposFromOrganizations = repositoriesList.let { repositories ->
            HttpClientFactory(repositories, config)
                    .requestAllData<Array<ReposJson>>()
                    .flatMap { array ->
                        mutableListOf<ReposJson>()
                                .also { it.addAll(array) }
                    }
                    .toSet()
        }

        return reposFromOrganizations.map { it.fullName }.toSet() + repos
    }

    private fun String?.splitAndTrim() =
        this
                ?.split(',')
                ?.map { it.trim() }
                ?.toSet()
}
