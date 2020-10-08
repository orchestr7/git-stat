package org.akuleshov7.utils

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.net.SocketException
import java.net.UnknownHostException


class HttpClientFactory(val urls: List<String>) {
    companion object {
        // FixMe: move it to properties/arguments
        const val REQUEST_TIMEOUT = 10000L
    }

    val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend inline fun <reified T> asyncRequests(): List<T> =
        urls.map { url ->
            logInfo("checking $url")
            GlobalScope.async {
                try {
                    withTimeout(REQUEST_TIMEOUT) {
                        client.get<T> {
                            url(url)
                            accept(ContentType.Application.Json)
                        }
                    }
                } catch (e: ClientRequestException) {
                    "Please check the name of your repo. Not able to access $url repository to get statistics <$e>" logAndExit 1
                } catch (e: TimeoutCancellationException) {
                    "Timeout during requesting the github. Is it available for you? Restart the app if possible <$e>" logAndExit 2
                } catch (e: SocketException) {
                    "Are you using proxy? Not able to access $url due to <$e>" logAndExit 3
                } catch (e: UnknownHostException) {
                    "Check your internet connection. Not able to access $url due to <$e>" logAndExit 4
                }
            }
        }.map {
            it.await()
        }
}
