package org.akuleshov7.stat

import org.akuleshov7.api.ReposJson
import org.akuleshov7.api.reposEndPoint
import org.akuleshov7.utils.HttpClientFactory

class Repositories() {
    var repos: Set<String> = emptySet()
    var orgs: Set<String> = emptySet()

    constructor(repositories: String?, organizations: String?, configPath: String?) : this() {
        repos = (readReposFromConfig() ?: repositories).splitAndTrim() ?: emptySet()
        orgs = (readOrgsFromConfig() ?: organizations).splitAndTrim() ?: emptySet()
    }

    suspend fun requestAllRepos(): Set<String> {
        val repositoriesList: Set<String>? = orgs
                .map { it.reposEndPoint() }
                .toSet()

        val reposFromOrganizations = if (repositoriesList != null) {
            HttpClientFactory(repositoriesList)
                    .requestAllData<Array<ReposJson>>()
                    .flatMap { array ->
                        mutableListOf<ReposJson>()
                                .also { it.addAll(array) }
                    }
                    .toSet()
        } else {
            emptySet()
        }

        return reposFromOrganizations.map { it.fullName }.toSet() + repos
    }

    private fun String?.splitAndTrim() =
        this
                ?.split(',')
                ?.map { it.trim() }
                ?.toSet()

    private fun readReposFromConfig(): String? {
        // FixMe: implement and move out of this class
        return null
    }

    private fun readOrgsFromConfig(): String? {
        // FixMe: implement and move out of this class
        return null
    }
}
