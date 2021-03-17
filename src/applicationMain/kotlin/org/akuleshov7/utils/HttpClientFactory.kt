@file:Suppress("FILE_WILDCARD_IMPORTS")

package org.akuleshov7.utils

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*

class HttpClientFactory(val urls: Set<String>, val config: Config) {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        config.credentials?.let {
            install(Auth) {
                basic {
                    sendWithoutRequest = true
                    username = it.gitHubUserName
                    password = it.gitHubAuthToken
                }
            }
        }
    }

    suspend inline fun <reified T> requestAllData() = getNumberOfPages()
            .map { (url, numberOfPages) -> asyncRequestsWithPagination<T>(url, numberOfPages) }
            .flatten()

    suspend inline fun <reified T> doRequest(url: String) =
        try {
            withTimeout(REQUEST_TIMEOUT) {
                client.get<T> {
                    url(url)
                    accept(ContentType.Application.Json)
                }
            }
        } catch (e: ClientRequestException) {
            when (e.response.status) {
                HttpStatusCode.Forbidden -> """It looks like you have reached the limit of requests to GitHub. 
                        | To extend the number of requests use your Username and GitHubToken: '-auth user:token'
                        | 
                        | <$e>""".trimMargin() logAndExit 1
                else -> "Please check the name of your repo. Not able to access $url repository to get statistics <$e>" logAndExit 1
            }
        } catch (e: TimeoutCancellationException) {
            "Timeout during requesting the github. Is it available for you? Restart the app if possible <$e>" logAndExit 2
        }/* catch (e: SocketException) {
            "Are you using proxy? Not able to access $url due to <$e>" logAndExit 3
        } catch (e: UnknownHostException) {
            "Check your internet connection. Not able to access $url due to <$e>" logAndExit 4
        }*/

    suspend inline fun <reified T> asyncRequestsWithPagination(url: String, numberOfPages: Int): List<T> =
        (1..numberOfPages).map { pageNum ->
            GlobalScope.async {
                val urlWithPageNumber = "$url&page=$pageNum"
                logDebug("checking $urlWithPageNumber")
                doRequest<T>(urlWithPageNumber)
            }
        }.map {
            it.await()
        }

    suspend inline fun getNumberOfPages(): Map<String, Int> =
        urls.map { url ->
            GlobalScope.async {
                logDebug("getting pagination size (maximum page number) for $url")
                withTimeout(REQUEST_TIMEOUT) {
                    val response: HttpResponse = doRequest(url)
                    if (response.status != HttpStatusCode.OK) {
                        "Incorrect response status: ${response.status}" logAndExit 7
                    }

                    val linkFromHeader = response.headers["link"]
                    val numberOfPages = linkFromHeader?.findPaginationLastPageNumber() ?: 1
                    url to numberOfPages
                }
            }
        }
                .map {
                    it.await()
                }
                .toMap()

    companion object {
        // FixMe: move it to properties/arguments
        const val REQUEST_TIMEOUT = 10_000L
    }
}

fun String.findPaginationLastPageNumber(): Int {
    val links = this.split(";")
    if (links.size != 3) {
        "Cannot parse response header (link section)" logAndExit (6)
    }

    val regex = Regex("&page=(.+?)>")
    return regex
            .findAll(links[1])
            .map { it.groupValues[1] }
            .joinToString()
            .toInt()
}
